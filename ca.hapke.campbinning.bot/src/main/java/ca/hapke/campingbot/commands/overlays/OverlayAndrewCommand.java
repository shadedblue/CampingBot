package ca.hapke.campingbot.commands.overlays;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.Sprite;

/**
 * @author Nathan Hapke
 */
public class OverlayAndrewCommand extends SlashCommand {
	private static final String OVERLAY_ANDREW = "OverlayAndrew";
	private static final SlashCommandType SlashAndrew = new SlashCommandType(OVERLAY_ANDREW, "andrew",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashAndrew };
	private static final String OVERLAY_FILENAME = "andrew.png";
	private CampingBot bot;
	private Image andrew;

	public OverlayAndrewCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		Message replyTo = message.getReplyToMessage();
		String picFileId = EnhanceCommand.getPictureFileId(replyTo);
		if (picFileId != null) {
			try {
				Image overlay = getAndrew();
				if (overlay == null) {
					return null;
				}
				GetFile get = new GetFile(picFileId);
				File in = null;
				in = bot.downloadFile(bot.execute(get));
				BufferedImage originalImg = ImageIO.read(in);

				BufferedImage overlayedImage = overlayImage(originalImg, overlay);
				File outImg = File.createTempFile(bot.getBotUsername(), ".png");
				boolean success = ImageIO.write(overlayedImage, "png", outImg);
				if (success)
					return new ImageCommandResult(SlashAndrew, outImg);
				else {
					Integer tId = message.getMessageId();
					String msg = "Failed to write image to HD: " + outImg.getAbsolutePath();
					bot.logFailure(tId, campingFromUser, chatId, SlashAndrew, new Exception(msg));
					System.err.println(msg);
				}
			} catch (Exception e) {
				Integer tId = message.getMessageId();
				bot.logFailure(tId, campingFromUser, chatId, SlashAndrew, e);
			}
			return null;
		}
		return null;
	}

	private Image getAndrew() {
		if (andrew == null) {
			ImageCache cache = ImageCache.getInstance();
			Sprite overlay = cache.getImage(CampingSystem.getInstance().getAssetsFolder(), OVERLAY_FILENAME);
			andrew = overlay.getFrame(0);
		}
		return andrew;
	}

	@Override
	public String getCommandName() {
		return OVERLAY_ANDREW;
	}

	public static BufferedImage overlayImage(Image base, Image overlay) {
		int h = base.getHeight(null);
		int w = base.getWidth(null);

		int targetHeight = h / 2;
		int targetWidth = w / 2;
		int targetSize = Math.min(targetHeight, targetWidth);

		Image overlayScaled = ImageCache.scaleToTileSize(overlay, targetSize);

		BufferedImage resultImg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics gfx = resultImg.getGraphics();
		gfx.drawImage(base, 0, 0, Color.white, null);
		gfx.drawImage(overlayScaled, 0, (h * 5) / 6 - targetSize, null, null);
		gfx.dispose();
		return resultImg;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
	}
}
