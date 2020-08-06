package ca.hapke.campbinning.bot.commands;

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

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.commands.response.SendResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.afd.AprilFoolsDayProcessor;
import ca.hapke.campbinning.bot.commands.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.ImageLink;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class EnhanceCommand extends AbstractCommand
		implements HasCategories<String>, CampingSerializable, SlashCommand {

	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.ImageEnhance };

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private static final String ENHANCE_CONTAINER = "Enhance";
	private static final String ENHANCE_COMMAND = "enhance";
	private static final String RICK_ROLL = "rickroll";
	private static final String OVER_ENHANCED = "over";
	private CampingBot bot;
	private AprilFoolsDayProcessor afdText;
	private CategoriedItems<String> categories;
	private Resources res;
	private CategoriedItems<ImageLink> resultImages;
	private List<ImageLink> rickImages;
	private List<String> rickText;
	private List<String> overEnhanced;
	private Map<Integer, Integer> tracking = new HashMap<>();
	private Map<CommandResult, Integer> trackingPending = new HashMap<>();
	private boolean shouldSave = false;

	public EnhanceCommand(CampingBot bot) {
		this.bot = bot;
		res = bot.getRes();
		this.afdText = new AprilFoolsDayProcessor(true);
		this.afdText.enable(true);
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

//	public CommandResult enhanceCommand(Message message) {
	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) {

		// update previous enhancements chain
		Set<Entry<CommandResult, Integer>> entrySet = trackingPending.entrySet();
		Iterator<Entry<CommandResult, Integer>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<CommandResult, Integer> item = iter.next();
			SendResult result = item.getKey().getResult();
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
		if (enhancementCount >= 3) {
			String text = CampingUtil.getRandom(overEnhanced);
			return new TextCommandResult(BotCommand.ImageEnhance, new TextFragment(text, CaseChoice.Upper))
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
			CommandResult pictureResponse = createPictureResponse(picFileId);
			if (pictureResponse != null)
				return pictureResponse;
		}

		// else == text only
		String text = replyTo.getText();
		return new TextCommandResult(BotCommand.ImageEnhance, garble(text)).setReplyTo(replyTo.getMessageId());
	}

	private CommandResult createPictureResponse(String picFileId) {
		try {
			GetFile get = new GetFile().setFileId(picFileId);
			File in = null;
			in = bot.downloadFile(bot.execute(get));
			File outImg;
			BufferedImage originalImg = ImageIO.read(in);
			int w = originalImg.getWidth();
			int h = originalImg.getHeight();
			int wRemove = w / 6;
			int hRemove = h / 6;
			BufferedImage centreImg = originalImg.getSubimage(wRemove, hRemove, w - (2 * wRemove), h - (2 * hRemove));

			String str = "M" + res.getRandomBall() + "IY" + res.getRandomFace();
			Graphics graphics = centreImg.getGraphics();
			int size = centreImg.getHeight() / 10;
			graphics.setFont(new Font("SansSerif", Font.PLAIN, size));
			graphics.drawString(str, 5, 5 + size);
			outImg = File.createTempFile(bot.getBotUsername(), ".jpg");
			boolean success = ImageIO.write(centreImg, "jpeg", outImg);
			if (success)
				return new ImageCommandResult(BotCommand.ImageEnhance, outImg, garble("ENHANCE"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private CommandResult createVideoResponse(Message replyTo) {
		ImageLink img = CampingUtil.getRandom(rickImages);
		String lyric = CampingUtil.getRandom(rickText);
		return new ImageCommandResult(BotCommand.ImageEnhance, img, garble(lyric)).setReplyTo(replyTo.getMessageId());
	}

	public TextFragment garble(String input) {
		return new TextFragment(afdText.processString(input));
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
