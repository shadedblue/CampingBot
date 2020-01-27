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

	private static final int SFW_START_HOUR = 8;
	private static final int SFW_END_HOUR = 16;
	private static final String EXCESSIVE_CATEGORY = "excessive";
	private static final String PARTY_START = "I kinda feel like pa";
	private static final String PARTY_END = "tying!";

	private static final String PARTY_REGEX = ".*pa([r]{3,})ty.*";

	private final ZoneId zone = ZoneId.systemDefault();

	private Cooldown cooldown = new Cooldown(3 * 60);

	private Pattern p;
	protected CampingBot bot;
	private CategoriedItems<String> categories = new CategoriedItems<String>(EXCESSIVE_CATEGORY);
	private RespondWithImage imagesNsfw;
	private RespondWithImage imagesSfw;
	private List<String> excessives;

	public PartyEverydayCommand(CampingBot bot) {
		this.bot = bot;
		imagesNsfw = new RespondWithImage(bot);
		imagesSfw = new RespondWithImage(bot);
		excessives = categories.getList(EXCESSIVE_CATEGORY);
		for (int i = 1; i <= 3; i++) {
			addImage("http://www.hapke.ca/images/party-boy" + i + ".gif", false);
		}
		addImage("http://www.hapke.ca/images/party-rave-girls.gif", true);
		addImage("http://www.hapke.ca/images/party-beasties.gif", true);
		addImage("http://www.hapke.ca/images/party-futurama.gif", true);
		addImage("http://www.hapke.ca/images/party-office.gif", true);
		addImage("http://www.hapke.ca/images/party-zebra.gif", true);
		p = Pattern.compile(PARTY_REGEX);
	}

	private void addImage(String url, boolean sfw) {
		imagesSfw.add(new ImageLink(url, ImageLink.GIF));
		if (!sfw)
			imagesNsfw.add(new ImageLink(url, ImageLink.GIF));
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		if (!cooldown.isReady())
			return false;

		String lowerCase = msg.toLowerCase();
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

		RespondWithImage images;
//		if (true) {
		if (day >= DayOfWeek.MONDAY.getValue() && day <= DayOfWeek.FRIDAY.getValue() && hours >= SFW_START_HOUR
				&& hours < SFW_END_HOUR) {
			images = imagesSfw;
		} else {
			images = imagesNsfw;
		}
		return images.sendImage(BotCommand.PartyEveryday, chatId, partying);
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

		if (count >= 5) {
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
