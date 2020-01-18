package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class PartyEverydayCommand extends CampingSerializable implements HasCategories, TextCommand {

	private static final String EXCESSIVE_CATEGORY = "excessive";
	private static final String PARTY_START = "I kinda feel like pa";
	private static final String PARTY_END = "tying!";

	private static final String PARTY_REGEX = ".*pa([r]{3,})ty.*";

	private final ZoneId zone = ZoneId.systemDefault();

	private Cooldown cooldown = new Cooldown(3 * 60);

	private Pattern p;
	protected CampingBot bot;
	private CategoriedItems<String> categories = new CategoriedItems<String>(EXCESSIVE_CATEGORY);
	private RespondWithImage images;
	private List<String> excessives;

	public PartyEverydayCommand(CampingBot bot) {
		this.bot = bot;
		images = new RespondWithImage(bot);
		excessives = categories.getList(EXCESSIVE_CATEGORY);
		for (int i = 1; i <= 3; i++) {
			images.add(new ImageLink("http://www.hapke.ca/images/party-boy" + i + ".gif", ImageLink.GIF));
		}
		p = Pattern.compile(PARTY_REGEX);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		if (!cooldown.isReady())
			return false;

		String lowerCase = msg.toLowerCase();
//		boolean matches = lowerCase.matches(PARTY_REGEX);
		Matcher m = p.matcher(lowerCase);

		return m.matches();
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		cooldown.setExec();

		Instant now = Instant.now();
		LocalDate ld = LocalDate.ofInstant(now, zone);
		LocalTime lt = LocalTime.ofInstant(now, zone);
		int day = ld.getDayOfWeek().getValue();
		int hours = lt.getHour();

		String partying = generateParrrty(message);

		if (day >= DayOfWeek.MONDAY.getValue() && day <= DayOfWeek.FRIDAY.getValue() && hours >= 8 && hours <= 16) {
			// weekdays SFW
			return new TextCommandResult(BotCommand.PartyEveryday, partying, true);
		} else {
			return images.sendImage(BotCommand.PartyEveryday, chatId, partying);
		}
	}

	public String generateParrrty(Message message) {

		String lowerCase = message.getText().toLowerCase();
		Matcher m = p.matcher(lowerCase);
		int count;
		if (m.matches()) {
			String g = m.group(1);
			count = g.length();
		} else {
			count = 3;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(PARTY_START);
		for (int i = 0; i < count; i++) {
			sb.append('r');
		}
		sb.append(PARTY_END);

		if (count >= 7) {
			sb.append("\n\n");
			sb.append(CampingUtil.getRandom(excessives));
		}

		return sb.toString();
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
	public String getContainerName() {
		return "Party";
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "party";
		of.start(tag);
		of.tagCategories(categories);
		of.finish(tag);
	}
}
