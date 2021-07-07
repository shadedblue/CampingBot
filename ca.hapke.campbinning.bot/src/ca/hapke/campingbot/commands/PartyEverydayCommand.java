package ca.hapke.campingbot.commands;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.category.CategoriedImageLinks;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.CategoriedStringsPersisted;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class PartyEverydayCommand extends AbstractCommand implements HasCategories<String>, TextCommand {

	public static final ResponseCommandType PartyEverydayCommand = new ResponseCommandType("PartyEveryday",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final String PARTY = "Party";

	private static final int SFW_START_HOUR = 8;
	private static final int SFW_END_HOUR = 16;
	private static final String EXCESSIVE_CATEGORY = "excessive";
	private static final String PARTY_START = "I kinda feel like pa";
	private static final String PARTY_END = "tying!";

	private static final String PARTY_REGEX = ".*pa([r]{3,})ty.*";
	private static final String NSFW_CATEGORY = "NSFW";
	private static final String SFW_CATEGORY = "SFW";

	private final ZoneId zone = ZoneId.systemDefault();

	private Pattern p;
	protected CampingBot bot;
	private CategoriedStringsPersisted categories = new CategoriedStringsPersisted(PARTY, EXCESSIVE_CATEGORY);
	private CategoriedItems<ImageLink> imgCategories = new CategoriedImageLinks(NSFW_CATEGORY, SFW_CATEGORY);

	public PartyEverydayCommand(CampingBot bot) {
		this.bot = bot;
		addImage("http://www.hapke.ca/images/party-boy1.gif", false);
		addImage("http://www.hapke.ca/images/party-boy3.gif", false);
		addImage("http://www.hapke.ca/images/party-rave-girls.gif", true);
		addImage("http://www.hapke.ca/images/party-beasties.gif", true);
		addImage("http://www.hapke.ca/images/party-futurama.gif", true);
		addImage("http://www.hapke.ca/images/party-office2.gif", true);
		addImage("http://www.hapke.ca/images/party-zebra2.gif", true);
		p = Pattern.compile(PARTY_REGEX);
	}

	private void addImage(String url, boolean sfw) {
		ImageLink lnk = new ImageLink(url, ImageLink.GIF);
		imgCategories.put(NSFW_CATEGORY, lnk);
		if (sfw)
			imgCategories.put(SFW_CATEGORY, lnk);
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		String lowerCase = msg.toLowerCase();
		Matcher m = p.matcher(lowerCase);

		return m.matches();
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		Instant now = Instant.now();
		LocalDate ld = LocalDate.ofInstant(now, zone);
		LocalTime lt = LocalTime.ofInstant(now, zone);
		int day = ld.getDayOfWeek().getValue();
		int hour = lt.getHour();

		String category;
		if (day >= DayOfWeek.MONDAY.getValue() && day <= DayOfWeek.FRIDAY.getValue() && hour >= SFW_START_HOUR
				&& hour < SFW_END_HOUR) {
			category = SFW_CATEGORY;
		} else {
			category = NSFW_CATEGORY;
		}
		ImageLink img = imgCategories.getRandom(category);

		List<ResultFragment> captionFrags = generateParrrty(message);
		ImageCommandResult result = new ImageCommandResult(PartyEverydayCommand, img, captionFrags);
		result.setReplyTo(message.getMessageId());
		return result;
	}

	public List<ResultFragment> generateParrrty(Message message) {
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
		TextFragment partyFrag = new TextFragment(sb.toString(), TextStyle.Bold);

		List<ResultFragment> list = new ArrayList<>(3);
		list.add(partyFrag);
		list.add(new TextFragment("\n\n"));
		list.add(new TextFragment(categories.getRandom(EXCESSIVE_CATEGORY)));
		return list;
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		categories.put(category, value);
	}

	@Override
	public String getContainerName() {
		return PARTY;
	}

	@Override
	public String getCommandName() {
		return PARTY;
	}

	@Override
	public int getSize(String s) {
		return categories.getSize(s);
	}
}
