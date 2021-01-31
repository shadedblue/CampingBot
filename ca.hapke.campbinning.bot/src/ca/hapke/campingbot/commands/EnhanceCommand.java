package ca.hapke.campingbot.commands;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.processors.CharacterRepeater;
import ca.hapke.campingbot.processors.FontGarbler;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;
import ca.hapke.campingbot.xml.OutputFormatter;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class EnhanceCommand extends AbstractCommand
		implements HasCategories<String>, CampingSerializable, SlashCommand {
	private static final String ENHANCE_CONTAINER = "Enhance";
	private static final String ENHANCE_COMMAND = "enhance";
	private static final String RICK_ROLL = "rickroll";
	private static final String OVER_ENHANCED = "over";

	private static final SlashCommandType SlashEnhance = new SlashCommandType(ENHANCE_CONTAINER, ENHANCE_COMMAND,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.PIC | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashEnhance };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private CampingBot bot;
	private MessageProcessor garbler;
	private CategoriedItems<String> categories;
	private Resources res;
	private CategoriedItems<ImageLink> resultImages;
	private List<ImageLink> rickImages;
	private List<String> rickText;
	private List<String> overEnhanced;
	private Map<Integer, Integer> tracking = new HashMap<>();
	private Map<CommandResult, Integer> trackingPending = new HashMap<>();
	private boolean shouldSave = false;

	private enum Direction {
		UpLeft(7),
		Up(8),
		UpRight(9),
		MidLeft(4),
		Mid(5),
		MidRight(6),
		DownLeft(1),
		Down(2),
		DownRight(3);

		public final int i;

		private Direction(int i) {
			this.i = i;

		}

		public int getX() {
			switch (this) {
			case Up:
			case Mid:
			case Down:
				return 1;
			case UpLeft:
			case MidLeft:
			case DownLeft:
				return 0;
			case UpRight:
			case MidRight:
			case DownRight:
				return 2;
			default:
				return -1;
			}
		}

		public int getY() {
			switch (this) {
			case Up:
			case UpLeft:
			case UpRight:
				return 0;
			case Mid:
			case MidLeft:
			case MidRight:
				return 1;
			case Down:
			case DownLeft:
			case DownRight:
				return 2;
			default:
				return -1;
			}
		}

		public static Direction fromString(String s) {
			s = s.strip();
			int x = -1;
			try {
				x = Integer.parseInt(s);
			} catch (NumberFormatException e) {
			}

			for (Direction d : values()) {
				if (d.i == x)
					return d;
				if (s.equalsIgnoreCase(d.toString()))
					return d;
			}

			return Mid;
		}

		public static Direction findDirection(Message message) {
			String msg = message.getText();
			String[] split = msg.split(" ");
			if (split.length >= 2)
				return fromString(split[1]);

			return Mid;
		}

	}

	public EnhanceCommand(CampingBot bot) {
		this.bot = bot;
		res = bot.getRes();
		this.garbler = new CharacterRepeater(true).addAtEnd(new FontGarbler(0.3));
//		this.garbler = new SwitchableProcessor(true, garblerPipe);
		categories = new CategoriedItems<String>(RICK_ROLL, OVER_ENHANCED);
		rickText = categories.getList(RICK_ROLL);

		overEnhanced = categories.getList(OVER_ENHANCED);

		this.resultImages = new CategoriedItems<ImageLink>(RICK_ROLL);
		rickImages = resultImages.getList(RICK_ROLL);
		for (int i = 1; i <= 3; i++) {
			String url = "http://www.hapke.ca/images/rick" + i + ".mp4";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			rickImages.add(lnk);
		}
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, CampingChat chat,
			CampingUser campingFromUser) throws TelegramApiException {
		Long chatId = chat.chatId;

		// update previous enhancements chain
		Set<Entry<CommandResult, Integer>> entrySet = trackingPending.entrySet();
		Iterator<Entry<CommandResult, Integer>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<CommandResult, Integer> item = iter.next();
			SendResult result = item.getKey().send(bot, chatId);
			if (result != null) {
				Integer repliedTo = item.getValue();
				tracking.put(result.outgoingMsg.getMessageId(), repliedTo);
				iter.remove();
			}
		}
		Message replyTo = message.getReplyToMessage();
		if (replyTo == null)
			return null;
		else {
			CommandResult enh = generateEnhancement(message);
			if (enh != null) {
				trackingPending.put(enh, replyTo.getMessageId());
			}
			return enh;
		}
	}

	private CommandResult generateEnhancement(Message message) {
		Message replyTo = message.getReplyToMessage();
		if (replyTo == null)
			return null;

		int enhancementCount = 0;
		Integer target = replyTo.getMessageId();
		while ((target = tracking.get(target)) != null) {
			enhancementCount++;
		}
		if (enhancementCount >= 2) {
			String text = CollectionUtil.getRandom(overEnhanced);
			return new TextCommandResult(SlashEnhance, new TextFragment(text, CaseChoice.Upper))
					.setReplyTo(replyTo.getMessageId());
		}

		// vid
		Document document = replyTo.getDocument();
		if (document != null) {
			String docType = document.getMimeType().toLowerCase();
			if (docType.startsWith("video")) {
				return createVideoResponse(replyTo);
			}
		}
		if (replyTo.getVideo() != null) {
			return createVideoResponse(replyTo);
		}

		// pic
		String picFileId = getPictureFileId(replyTo);
		if (picFileId != null) {
			Direction d = Direction.findDirection(message);
			CommandResult pictureResponse = createPictureResponse(picFileId, replyTo.getCaption(), d);
			if (pictureResponse != null)
				return pictureResponse;
		}

		// else == text only
		String text = replyTo.getText();
		return new TextCommandResult(SlashEnhance, garble(text)).setReplyTo(replyTo.getMessageId());
	}

	private CommandResult createPictureResponse(String picFileId, String caption, Direction d) {
		try {
			GetFile get = new GetFile(picFileId);
			File in = null;
			in = bot.downloadFile(bot.execute(get));
			File outImg;
			BufferedImage originalImg = ImageIO.read(in);
			int w = originalImg.getWidth();
			int h = originalImg.getHeight();
			int wRemove = w / 6;
			int hRemove = h / 6;
			int x = d.getX() * wRemove;
			int y = d.getY() * hRemove;
			BufferedImage centreImg = originalImg.getSubimage(x, y, w - (2 * wRemove), h - (2 * hRemove));

			String str = "M" + res.getRandomBall() + "IY" + res.getRandomFace();
			Graphics graphics = centreImg.getGraphics();
			int size = centreImg.getHeight() / 10;
			graphics.setFont(new Font("SansSerif", Font.PLAIN, size));
			graphics.drawString(str, 5, 5 + size);
			outImg = File.createTempFile(bot.getBotUsername(), ".jpg");
			boolean success = ImageIO.write(centreImg, "jpeg", outImg);
			if (caption == null || caption.length() == 0)
				caption = "ENHANCE";
			if (success)
				return new ImageCommandResult(SlashEnhance, outImg, garble(caption));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private CommandResult createVideoResponse(Message replyTo) {
		ImageLink img = CollectionUtil.getRandom(rickImages);
		String lyric = CollectionUtil.getRandom(rickText);
		return new ImageCommandResult(SlashEnhance, img, garble(lyric)).setReplyTo(replyTo.getMessageId());
	}

	public TextFragment garble(String input) {
		return new TextFragment(garbler.processString(input, true));
	}

	private String getPictureFileId(Message replyTo) {
		Animation ani = replyTo.getAnimation();
		if (ani != null)
			return ani.getFileId();
		List<PhotoSize> pics = replyTo.getPhoto();
		if (pics == null)
			return null;
		int height = -1;
		PhotoSize pic = null;
		for (PhotoSize p : pics) {
			if (p.getHeight() > height)
				pic = p;
		}
		if (pic == null)
			return null;
		return pic.getFileId();
	}

	@Override
	public String getCommandName() {
		return ENHANCE_COMMAND;
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		categories.put(category, value);
		shouldSave = true;
	}

	@Override
	public List<String> getCategory(String name) {
		return categories.getList(name);
	}

	@Override
	public String getContainerName() {
		return ENHANCE_CONTAINER;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		of.start(ENHANCE_COMMAND);
		of.tagCategories(categories);
		of.finish(ENHANCE_COMMAND);
		shouldSave = false;
	}

}
