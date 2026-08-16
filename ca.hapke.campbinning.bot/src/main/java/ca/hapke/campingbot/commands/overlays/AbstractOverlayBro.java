package ca.hapke.campingbot.commands.overlays;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.commands.EnhanceCommand;
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
public abstract class AbstractOverlayBro extends SlashCommand {

	protected final CampingBot bot;
	protected final String overlayFilename;
	protected final SlashCommandType[] SLASH_COMMANDS;
	private SlashCommandType slashBro;
	private Image overlay;

	public AbstractOverlayBro(CampingBot bot, String overlayFilename, SlashCommandType slashBro) {
		this.bot = bot;
		this.overlayFilename = overlayFilename;
		this.slashBro = slashBro;
		SLASH_COMMANDS = new SlashCommandType[] { slashBro };
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		Message replyTo = message.getReplyToMessage();
		String picFileId = EnhanceCommand.getPictureFileId(replyTo);
		if (picFileId != null) {
			try {
				Image overlay = getOverlay();
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
					return new ImageCommandResult(slashBro, outImg);
				else {
					Integer tId = message.getMessageId();
					String msg = "Failed to write image to HD: " + outImg.getAbsolutePath();
					bot.logFailure(tId, campingFromUser, chatId, slashBro, new Exception(msg));
					System.err.println(msg);
				}
			} catch (Exception e) {
				Integer tId = message.getMessageId();
				bot.logFailure(tId, campingFromUser, chatId, slashBro, e);
			}
			return null;
		}
		return null;
	}

	private Image getOverlay() {
		if (overlay == null) {
			ImageCache cache = ImageCache.getInstance();
			Sprite s = cache.getImage(CampingSystem.getInstance().getAssetsFolder(), overlayFilename);
			overlay = s.getFrame(0);
		}
		return overlay;
	}

	public BufferedImage overlayImage(Image base, Image overlay) {
		int h = base.getHeight(null);
		int w = base.getWidth(null);

		int targetHeight = h / 2;
		int targetWidth = w / 2;
		int targetSize = Math.min(targetHeight, targetWidth);

		Image overlayScaled = ImageCache.scaleToTileSize(overlay, targetSize);
		int overlayW = overlayScaled.getWidth(null);
		int overlayH = overlayScaled.getHeight(null);

		BufferedImage resultImg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics gfx = resultImg.getGraphics();
		gfx.drawImage(base, 0, 0, Color.white, null);
		Point resultLocation = getResultLocation(w, h, overlayW, overlayH);
		gfx.drawImage(overlayScaled, resultLocation.x, resultLocation.y, null, null);
		gfx.dispose();
		return resultImg;
	}

	protected abstract Point getResultLocation(int w, int h, int overlayW, int overlayH);

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public String getCommandName() {
		return slashBro.slashCommand;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
	}
}
