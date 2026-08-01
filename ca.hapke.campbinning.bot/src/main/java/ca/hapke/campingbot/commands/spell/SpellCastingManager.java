package ca.hapke.campingbot.commands.spell;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.CategoriedImageLinks;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class SpellCastingManager implements CalendaredEvent<CampingUser> {

	private static final String CONTAINER = SpellCommand.SPELL + "Manager";
	private static final int DELAY_AMOUNT = 15;
	private static final String KNOCK_OUT = "KKNNNNOOOOCCKKKK OUUUUTTTTT";
	private static final String MG_G = "MG G";
	private static final String NG_B = "NG B";
	private static final String NG = "NG! ";

	private TimesProvider<CampingUser> times = new TimesProvider<>();
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts = new HashMap<>();
	private SpellPropogationManager propogationManager;
	private CampingBotEngine bot;
	private static final String COMBO_BREAKER = "ComboBreaker";
	private CategoriedItems<ImageLink> images;
	private ImageLink revengeImg = new ImageLink("http://www.hapke.ca/images/spell-revenge-1.gif", ImageLink.GIF);
	private ImageLink koDeadImg = new ImageLink("http://www.hapke.ca/images/spell-ko-dead.mp4", ImageLink.GIF);
	private ImageLink gbImg = new ImageLink("http://www.hapke.ca/images/spell-gangbang-carrots.mp4", ImageLink.GIF);
	private Resources resources;

	public SpellCastingManager(CampingBot bot) {
		this.bot = bot;
		propogationManager = new SpellPropogationManager(pendingCasts);
		resources = bot.getRes();
		images = new CategoriedImageLinks(CONTAINER, COMBO_BREAKER);
		for (int i = 1; i <= 4; i++) {
			String url = "http://www.hapke.ca/images/spell-combo-breaker-" + i + ".gif";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			images.put(COMBO_BREAKER, lnk);
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

				times.add(new ByFrequency<CampingUser>(caster, DELAY_AMOUNT, ChronoUnit.SECONDS));
			}
			futures.add(pending);
		}

		return immediate;
	}

	private void castNow(CampingUser caster, CampingUser victim, SpellResult spell, CampingChat chat) {
		ComboType comboType = propogationManager.getComboResult(caster, victim);
		System.out.println("Casting " + caster + " => " + victim + " (" + comboType + ")!");

		CommandResult outgoing = null;
		switch (comboType) {
		case Dead:
			// NO CAST!
			return;
		case Breaker:
			// COMBO BREAKER GIF
			ImageLink img = images.getRandom(COMBO_BREAKER);
			outgoing = new ImageCommandResult(SpellCommand.SlashSpellCommand, img);
			break;
		case GangBang:
			outgoing = new ImageCommandResult(SpellCommand.SlashSpellCommand, gbImg);
			break;
		case Revenge:
			outgoing = new ImageCommandResult(SpellCommand.SpellDipshitCommand, revengeImg);
			break;
		case KO:
			outgoing = new ImageCommandResult(SpellCommand.SlashSpellCommand, koDeadImg);
			break;
		// NOOPs
		case Fizzle:
		case Normal:
			break;
		}

		CommandResult outgoingSpell = spell.provideCommandResult(outgoing);

		switch (comboType) {
		case Dead:
			// NO CAST!
			return;
		case Breaker:
			break;
		case GangBang:
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(resources.getRandomFaceEmoji());
			outgoingSpell.add(MG_G);
			outgoingSpell.add(resources.getRandomFaceEmoji());
			outgoingSpell.add(NG_B);
			outgoingSpell.add(resources.getRandomFaceEmoji());
			outgoingSpell.add(NG);
			outgoingSpell.add(resources.getRandomBallEmoji());
			outgoingSpell.add(resources.getRandomBallEmoji());
			outgoingSpell.add(resources.getRandomBallEmoji());
			break;
		case KO:
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(ResultFragment.NEWLINE);
			outgoingSpell.add(KNOCK_OUT);
			outgoingSpell.add(resources.getBall("fire"));
			break;
		// NOOPs
		case Fizzle:
		case Normal:
		case Revenge:
			break;
		}

		if (comboType.sendsSpell) {
			// SendResult result =
			outgoingSpell.sendAndLog(bot, chat);
		}
	}

	@Override
	public TimesProvider<CampingUser> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<CampingUser> event, CampingUser caster) {
		System.out.println("SpellCastingManager::doWork " + caster);

		LinkedList<PendingCast> futures = pendingCasts.get(caster);

		boolean remove = false;
		if (futures == null || futures.size() == 0) {
			remove = true;
		} else {
			PendingCast nextCast = futures.peek();
			nextCast.waits--;
			System.out.println("Next cast is on: " + nextCast.victim + " in " + nextCast.waits + " more activations");
			if (nextCast.waits == 0) {
				futures.remove();
				castNow(caster, nextCast.victim, nextCast.result, nextCast.chat);

				if (futures.isEmpty()) {
					remove = true;
				}
			}
		}

		if (remove) {
			System.out.println("Remove " + caster + "!");
			pendingCasts.remove(caster);
			times.remove(event);
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

	public void setMe(CampingUser me) {
		propogationManager.setMe(me);
	}

	public String getPendingString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pendingCasts.size());
		sb.append(" pending\n");
		for (Entry<CampingUser, LinkedList<PendingCast>> e : pendingCasts.entrySet()) {
			CampingUser from = e.getKey();
			LinkedList<PendingCast> tos = e.getValue();

			sb.append(from.getFirstOrUserName());
			sb.append("=>");

			boolean first = true;
			for (PendingCast to : tos) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(to.victim.getFirstOrUserName());
				sb.append("(");
				sb.append(to.waits);
				sb.append(")");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
