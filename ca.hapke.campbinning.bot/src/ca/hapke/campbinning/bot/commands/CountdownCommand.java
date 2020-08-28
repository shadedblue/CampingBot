package ca.hapke.campbinning.bot.commands;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.api.BotCommandIds;
import ca.hapke.campbinning.bot.commands.api.SlashCommandType;
import ca.hapke.campbinning.bot.commands.api.SlashCommand;
import ca.hapke.campbinning.bot.mbiyf.MbiyfCommand;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.TimeFormatter;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CountdownCommand extends AbstractCommand
		implements HasCategories<String>, CampingSerializable, SlashCommand {

	private static final SlashCommandType SlashCountdown = new SlashCommandType("Countdown", "countdown",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);

	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashCountdown };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private boolean shouldSave = false;

	public static final String COUNTDOWN_CONTAINER = "Countdown";
	public static final String HYPE_CATEGORY = "hype";
	// Month is 0-indexed for some stupid inconsistent reason...
	private ZonedDateTime countdownTarget = new GregorianCalendar(2020, 3, 10, 20, 0, 00).toZonedDateTime();
	private List<String> hypes;
	private Resources res;
	private MbiyfCommand ballsCommand;
//	private ZoneId zone = TimeZone.getDefault().toZoneId();
	private CategoriedItems<String> categories;
	private TimeFormatter tf = new TimeFormatter(2, " ", false, true);
//	private CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

	public CountdownCommand(Resources res, MbiyfCommand ballsCommand) {
		this.res = res;
		this.ballsCommand = ballsCommand;
		categories = new CategoriedItems<String>(HYPE_CATEGORY);
		hypes = categories.getList(HYPE_CATEGORY);
	}

	@Override
	public String getContainerName() {
		return COUNTDOWN_CONTAINER;
	}

	public void setHypes(List<String> h) {
		categories.putAll(HYPE_CATEGORY, h);
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		if (categories.put(category, value))
			shouldSave = true;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		ZonedDateTime now = new GregorianCalendar().toZonedDateTime();
		CommandResult result = new TextCommandResult(CountdownCommand.SlashCountdown);

		ZonedDateTime targetEvent;
		if (countdownTarget != null && now.isBefore(countdownTarget)) {
//			int rtv = 558638791;
			int andrew = 642767839;
//			int jamieson = 708570894;
//			CampingUser target = userMonitor.monitor(andrew, null, null, null);

			result.add("GETTING JACKED", CaseChoice.Upper);
//			result.add(new MentionFragment(target, MentionDisplay.Nickname, CaseChoice.Upper, null, "'s"));

			result.add(" COUNTDOWN\n");

			targetEvent = countdownTarget;
		} else {
			result.add("MBIYFRIDAY COUNTDOWN\n");
			targetEvent = ballsCommand.getNearestFutureEnablement();
		}

		for (int i = 0; i < 5; i++) {
			result.add(res.getRandomFaceEmoji());
		}
		result.add(ResultFragment.NEWLINE);
		result.add(res.getRandomBallEmoji());
		result.add(ResultFragment.SPACE);
		result.add(tf.toPrettyString(targetEvent));

		result.add(ResultFragment.NEWLINE);

		result.add(res.getRandomBallEmoji());
		result.add(ResultFragment.SPACE);
		String hypeMsg = CampingUtil.getRandom(hypes);

		result.add(hypeMsg);

		return result;
	}

	protected List<String> getHypes() {
		return hypes;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "countdown";
		of.start(tag);
		of.tagAndValue(HYPE_CATEGORY, hypes);
		of.finish(tag);

		shouldSave = false;
	}

	@Override
	public String getCommandName() {
		return COUNTDOWN_CONTAINER;
	}

}
