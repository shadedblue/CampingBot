package ca.hapke.campingbot.commands.spell;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class SpellCastingManager implements CalendaredEvent<CampingUser> {

	private static final String KNOCK_OUT = "KKNNNNOOOOCCKKKK OUUUUTTTTT";

	private class PendingCast {

		private final CampingUser victim;
		private final SpellResult result;
		private final CampingChat chat;
		private int waits;

		public PendingCast(CampingUser victim, SpellResult result, CampingChat chat, int waits) {
			this.victim = victim;
			this.result = result;
			this.chat = chat;
			this.waits = waits;
		}

	}

	private TimesProvider<CampingUser> times = new TimesProvider<>();
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts = new HashMap<>();
	private SpellPropogationManager propogationManager = new SpellPropogationManager();
	private CampingBotEngine bot;
	private static final String COMBO_BREAKER = "ComboBreaker";
	private CategoriedItems<ImageLink> images;
	private List<ImageLink> breakerImages;
	private ImageLink koImg = new ImageLink("http://www.hapke.ca/images/spell-ko.mp4", ImageLink.GIF);
	private ImageLink deadImg = new ImageLink("http://www.hapke.ca/images/spell-dead.mp4", ImageLink.GIF);
	private Resources resources;

	public SpellCastingManager(CampingBot bot) {
		this.bot = bot;
		resources = bot.getRes();
		images = new CategoriedItems<>(COMBO_BREAKER);
		breakerImages = images.getList(COMBO_BREAKER);
		for (int i = 1; i <= 7; i++) {
			String url = "http://www.hapke.ca/images/spell-combo-breaker-" + i + ".gif";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			breakerImages.add(lnk);
		}
	}

	/**
	 * @return if the spell is cast immediately
	 */
	public boolean softCast(CampingUser caster, CampingUser victim, SpellResult result, CampingChat chat) {
		int waits = propogationManager.getWaits(caster);

		boolean immediate = waits == 0;

		if (immediate) {
			castNow(caster, victim, result, chat);
		} else {
			PendingCast pending = new PendingCast(victim, result, chat, waits);
			LinkedList<PendingCast> futures = pendingCasts.get(caster);
			if (futures == null) {
				futures = new LinkedList<>();
				pendingCasts.put(caster, futures);

				times.add(new ByFrequency<CampingUser>(caster, 15, ChronoUnit.SECONDS));
			}
			futures.add(pending);
		}

		return immediate;
	}

	private void castNow(CampingUser caster, CampingUser victim, SpellResult spell, CampingChat chat) {
		ComboType comboType = propogationManager.getComboResult(caster, victim);
		if (comboType == ComboType.Dead) {
			return;
		}
		if (comboType == ComboType.Breaker) {
			// Remove all future victim => caster casts
			pendingCasts.remove(victim);

			// COMBO BREAKER GIF
			ImageLink img = CollectionUtil.getRandom(breakerImages);
			ImageCommandResult breaker = new ImageCommandResult(SpellCommand.SlashSpellCommand, img);
			breaker.sendAndLog(bot, chat);
		}

		TextCommandResult outgoingSpell = spell.provideCommandResult();

		if (comboType == ComboType.KO) {
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(KNOCK_OUT);
			outgoingSpell.add(resources.getBall("fire"));
		}

//		SendResult result = 
		outgoingSpell.sendAndLog(bot, chat);

		if (comboType == ComboType.Fizzle) {
			// TODO Punish a bit
		} else if (comboType == ComboType.KO) {
			// KO GIF
			ImageCommandResult ko = new ImageCommandResult(SpellCommand.SlashSpellCommand, koImg);
			ko.sendAndLog(bot, chat);
			// DEAD GIF
			ImageCommandResult dead = new ImageCommandResult(SpellCommand.SlashSpellCommand, deadImg);
			dead.sendAndLog(bot, chat);
		}
	}

	@Override
	public TimesProvider<CampingUser> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<CampingUser> event, CampingUser caster) {
		LinkedList<PendingCast> futures = pendingCasts.get(caster);

		if (futures != null) {
			PendingCast nextCast = futures.peek();
			nextCast.waits--;
			if (nextCast.waits == 0) {
				futures.remove();
				castNow(caster, nextCast.victim, nextCast.result, nextCast.chat);

				if (futures.isEmpty()) {
					pendingCasts.remove(caster);
					times.remove(event);
				}
			}
		}
	}

	@Override
	public boolean shouldRun() {
		return !pendingCasts.isEmpty();
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}
}
