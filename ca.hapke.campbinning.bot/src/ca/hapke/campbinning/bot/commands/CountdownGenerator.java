package ca.hapke.campbinning.bot.commands;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.TimeFormatter;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CountdownGenerator extends CampingSerializable implements HasCategories<String> {

	private static final String HYPE_CATEGORY = "hype";
	// Month is 0-indexed for some stupid inconsistent reason...
	private ZonedDateTime countdownTarget = new GregorianCalendar(2020, 3, 9, 18, 20, 00).toZonedDateTime();
	private List<String> hypes;
	private Resources res;
	private MbiyfCommand ballsCommand;
//	private ZoneId zone = TimeZone.getDefault().toZoneId();
	private CategoriedItems<String> categories;
	private TimeFormatter tf = new TimeFormatter(2, " ", false, true);

	public CountdownGenerator(Resources res, MbiyfCommand ballsCommand) {
		this.res = res;
		this.ballsCommand = ballsCommand;
		categories = new CategoriedItems<String>(HYPE_CATEGORY);
		hypes = categories.getList(HYPE_CATEGORY);
	}

	@Override
	public String getContainerName() {
		return "Countdown";
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

	// public List<String> getHypes() {
	// return hypes;
	// }

	public CommandResult countdownCommand(CampingUserMonitor userMonitor, Long chatId) {
		ZonedDateTime now = new GregorianCalendar().toZonedDateTime();
		CommandResult result = new TextCommandResult(BotCommand.Countdown);

		ZonedDateTime targetEvent;
		if (countdownTarget != null && now.isBefore(countdownTarget)) {
//			CampingUser rtv = userMonitor.monitor(558638791, null, null, null);
			CampingUser andrew = userMonitor.monitor(642767839, null, null, null);
//			CampingUser jamieson = userMonitor.monitor(708570894, null, null, null);
			CampingUser target = andrew;

			result.add("MY BALLS IN ");
			result.add(new MentionFragment(target, CaseChoice.Upper, null, "'s"));

			result.add(" EASTER COUNTDOWN\n");

			targetEvent = countdownTarget;
		} else {
			result.add("MBIY\\[F]RIDAY COUNTDOWN\n");
			targetEvent = ballsCommand.getNearestFutureEnablement();
		}

		for (int i = 0; i < 5; i++) {
			result.add(res.getRandomFaceEmoji());
		}
		result.add("\n");
		result.add(res.getRandomBallEmoji());
		result.add(" ");
		result.add(tf.toPrettyString(targetEvent));

		result.add("\n");

		result.add(res.getRandomBallEmoji());
		result.add(" ");
		String hypeMsg = CampingUtil.getRandom(hypes);

		result.add(hypeMsg);

		return result;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "countdown";
		of.start(tag);
		of.tagAndValue(HYPE_CATEGORY, hypes);
		of.finish(tag);
	}

}
