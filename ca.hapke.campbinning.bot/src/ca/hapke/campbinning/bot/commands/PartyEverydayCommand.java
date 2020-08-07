package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.ImageCommandResult;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.ImageLink;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class PartyEverydayCommand extends AbstractCommand
		implements HasCategories<String>, TextCommand, CampingSerializable {
	private static final String PARTY = "Party";

	private boolean shouldSave = false;

	private static final int SFW_START_HOUR = 8;
	private static final int SFW_END_HOUR = 16;
	private static final String EXCESSIVE_CATEGORY = "excessive";
	private static final String PARTY_START = "I kinda feel like pa";
	private static final String PARTY_END = "tying!";

	private static final String PARTY_REGEX = ".*pa([r]{3,})ty.*";
	private static final String NSFW_CATEGORY = "NSFW";
	private static final String SFW_CATEGORY = "SFW";

	private final ZoneId zone = ZoneId.systemDefault();

	private Cooldown cooldown = new Cooldown(3 * 60);

	private Pattern p;
	protected CampingBot bot;
	private CategoriedItems<String> categories = new CategoriedItems<String>(EXCESSIVE_CATEGORY);
	private CategoriedItems<ImageLink> imgCategories = new CategoriedItems<ImageLink>(NSFW_CATEGORY, SFW_CATEGORY);
	private List<ImageLink> imagesNsfw;
	private List<ImageLink> imagesSfw;
	private List<String> excessives;

	public PartyEverydayCommand(CampingBot bot) {
		this.bot = bot;
		imagesNsfw = imgCategories.getList(NSFW_CATEGORY);
		imagesSfw = imgCategories.getList(SFW_CATEGORY);
		excessives = categories.getList(EXCESSIVE_CATEGORY);
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
		imagesNsfw.add(lnk);
		if (sfw)
			imagesSfw.add(lnk);
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		if (!cooldown.isReady())
			return false;

		String lowerCase = msg.toLowerCase();
		Matcher m = p.matcher(lowerCase);

		return m.matches();
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		cooldown.setExec();

		Instant now = Instant.now();
		LocalDate ld = LocalDate.ofInstant(now, zone);
		LocalTime lt = LocalTime.ofInstant(now, zone);
		int day = ld.getDayOfWeek().getValue();
		int hour = lt.getHour();

		List<ImageLink> images;
		if (day >= DayOfWeek.MONDAY.getValue() && day <= DayOfWeek.FRIDAY.getValue() && hour >= SFW_START_HOUR
				&& hour < SFW_END_HOUR) {
			images = imagesSfw;
		} else {
			images = imagesNsfw;
		}
		ImageLink img = CampingUtil.getRandom(images);

		List<ResultFragment> captionFrags = generateParrrty(message);
		ImageCommandResult result = new ImageCommandResult(BotCommand.PartyEveryday, img, captionFrags);
//		result.setReplyTo(message.getMessageId());
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

		if (count >= 5) {
			List<ResultFragment> list = new ArrayList<>(3);
			list.add(partyFrag);
			list.add(new TextFragment("\n\n"));
			list.add(new TextFragment(CampingUtil.getRandom(excessives)));
			return list;
		} else {
			return Collections.singletonList(partyFrag);
		}
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
		return PARTY;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "party";
		of.start(tag);
		of.tagCategories(categories);
		of.finish(tag);

		shouldSave = false;
	}

	@Override
	public String getCommandName() {
		return PARTY;
	}
}
