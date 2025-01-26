package ca.hapke.campingbot.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.ImageLink;
import ca.hapke.campingbot.util.Sprite;

public class OverlayNyandrewCommand extends AbstractCommand implements TextCommand, SlashCommand {

	private static final String OVERLAY_NYANDREW = "OverlayNyandrew";
	private static final SlashCommandType SlashNyandrew = new SlashCommandType(OVERLAY_NYANDREW, "nyandrew",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashNyandrew };
	private static final String[] OVERLAY_FILENAMES = { "nyandrew1.png", "nyandrew2.png" };
	private CampingBot bot;
	private Image[] overlays;

	private static final int FRAME_HOLD_LENGTH = 3;
	private static final int RESULT_WIDTH = 300;
	private static final int ANDREW_WIDTH = RESULT_WIDTH;
	private static final int STEP_DELTA = 10;

	public OverlayNyandrewCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		return textCommand(campingFromUser, message.getEntities(), chatId, message);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {

		Message replyTo = message.getReplyToMessage();
		String picFileId = EnhanceCommand.getPictureFileId(replyTo);
		if (picFileId != null) {
			try {
				Image[] overlays = getOverlays();
				if (overlays == null) {
					return null;
				}
				GetFile get = new GetFile(picFileId);
				File in = null;
				in = bot.downloadFile(bot.execute(get));
				BufferedImage originalImg = ImageIO.read(in);

				File outImg = File.createTempFile(bot.getBotUsername(), ".gif");
				overlayNyandrew(originalImg, overlays, outImg);

				boolean outputCreated = outImg.exists();
				if (outputCreated) {
					ImageCommandResult icr = new ImageCommandResult(SlashNyandrew, outImg);
					icr.setFileType(ImageLink.GIF);
					return icr;
				} else {
					Integer tId = message.getMessageId();
					String msg = "Failed to write image to HD: " + outImg.getAbsolutePath();
					bot.logFailure(tId, campingFromUser, chatId, SlashNyandrew, new Exception(msg));
					System.err.println(msg);
				}
			} catch (Exception e) {
				Integer tId = message.getMessageId();
				bot.logFailure(tId, campingFromUser, chatId, SlashNyandrew, e);
			}
			return null;
		}
		return null;
	}

	private Image[] getOverlays() {
		if (overlays == null) {
			ImageCache cache = ImageCache.getInstance();
			int qty = OVERLAY_FILENAMES.length;
			Image[] newOverlays = new Image[qty];
			for (int i = 0; i < qty; i++) {
				String filename = OVERLAY_FILENAMES[i];

				Sprite overlay = cache.getImage(CampingSystem.getInstance().getAssetsFolder(), filename);
				newOverlays[i] = overlay.getFrame(0);
			}
			this.overlays = newOverlays;
		}
		return overlays;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		String msgLower = msg.toLowerCase().trim();
		return msgLower.startsWith("/" + SlashNyandrew.slashCommand);
	}

	@Override
	public String getCommandName() {
		return OVERLAY_NYANDREW;
	}

	public static void overlayNyandrew(Image baseOriginal, Image[] overlays, File f) throws Exception {
		OutputStream outputStream = new FileOutputStream(f);
		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.start(outputStream);
		encoder.setDelay(40);
		// continuous
		encoder.setRepeat(0);
		encoder.setQuality(5);

		Image baseScaled = ImageCache.scaleToTileSize(baseOriginal, RESULT_WIDTH);
		int w = baseScaled.getWidth(null);
		int h = baseScaled.getHeight(null);

		Image[] overlaysScaled = new Image[overlays.length];
		int hAndrew = 0;
		for (int i = 0; i < overlays.length; i++) {
			Image img = overlays[i];
			Image scaled = ImageCache.scaleToTileSize(img, ANDREW_WIDTH);
			overlaysScaled[i] = scaled;
			hAndrew = scaled.getHeight(null);
		}

		int x = -ANDREW_WIDTH + 10;
		int yLoc = (h - hAndrew) / 2;
		int overlayIndex = 0;
		int frameIndex = 0;

		while (x < w) {
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics gfx = img.getGraphics();
			gfx.drawImage(baseScaled, 0, 0, Color.white, null);
			Image overlayScaled = overlaysScaled[overlayIndex];
			frameIndex++;
			if (frameIndex >= FRAME_HOLD_LENGTH) {
				frameIndex = 0;
				overlayIndex++;
				if (overlayIndex >= overlaysScaled.length) {
					overlayIndex = 0;
				}
			}
			gfx.drawImage(overlayScaled, x, yLoc, null, null);
			gfx.dispose();
			img.flush();
			encoder.addFrame(img);
			x += STEP_DELTA;
		}
		encoder.finish();
		outputStream.close();
	}

}
