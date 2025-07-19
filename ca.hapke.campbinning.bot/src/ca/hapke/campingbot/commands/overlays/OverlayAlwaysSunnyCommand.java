package ca.hapke.campingbot.commands.overlays;

import static ca.hapke.campingbot.commands.overlays.OverlayNyandrewCommand.RESULT_WIDTH;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class OverlayAlwaysSunnyCommand extends AbstractCommand implements SlashCommand {
	private static final String OVERLAY_ALWAYS_SUNNY = "OverlayAlwaysSunny";
	private static final SlashCommandType SlashAs = new SlashCommandType(OVERLAY_ALWAYS_SUNNY, "as",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashAs };
	private CampingBot bot;

	public static final int FRAME_HOLD_LENGTH = 15;

	//@formatter:off
	public static final AlwaysSunnySprite[] RESTAURANT_SET = { 
			new AlwaysSunnySprite("sunny-restaurant-charlie.png", 1d, 0.5d),
			new AlwaysSunnySprite("sunny-restaurant-mac.png",     0d, 0.5d)
	};
	public static final AlwaysSunnySprite[][] SPRITE_SETS = {
		RESTAURANT_SET 
	};

	//@formatter:on
	public OverlayAlwaysSunnyCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public String getCommandName() {
		return OVERLAY_ALWAYS_SUNNY;
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
				int index = (int) (SPRITE_SETS.length * Math.random());
				AlwaysSunnySprite[] spriteSet = SPRITE_SETS[index];
				GetFile get = new GetFile(picFileId);
				File in = null;
				in = bot.downloadFile(bot.execute(get));
				BufferedImage originalImg = ImageIO.read(in);

				File outImg = File.createTempFile(bot.getBotUsername(), ".gif");
				overlayAlwaysSunny(originalImg, spriteSet, outImg);

				boolean outputCreated = outImg.exists();
				if (outputCreated) {
					ImageCommandResult icr = new ImageCommandResult(SlashAs, outImg);
					icr.setFileType(ImageLink.GIF);
					return icr;
				} else {
					Integer tId = message.getMessageId();
					String msg = "Failed to write image to HD: " + outImg.getAbsolutePath();
					bot.logFailure(tId, campingFromUser, chatId, SlashAs, new Exception(msg));
					System.err.println(msg);
				}
			} catch (Exception e) {
				Integer tId = message.getMessageId();
				bot.logFailure(tId, campingFromUser, chatId, SlashAs, e);
			}
		}
		return null;
	}

	public static void overlayAlwaysSunny(Image baseOriginal, AlwaysSunnySprite[] overlays, File f) throws Exception {
		OutputStream outputStream = new FileOutputStream(f);
		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.start(outputStream);
		encoder.setDelay(10000);
		// continuous
		encoder.setRepeat(0);
		encoder.setQuality(5);

		Image baseScaled = ImageCache.scaleToTileSize(baseOriginal, RESULT_WIDTH);
		int w = baseScaled.getWidth(null);
		int h = baseScaled.getHeight(null);

		for (int i = 0; i < overlays.length; i++) {
			AlwaysSunnySprite sprite = overlays[i];
			int scaledHeight = (int) (h * sprite.heightPct);
			Image overlayImage = sprite.getImage();

			Image overlayScaled = ImageCache.scaleToTileSize(overlayImage, scaledHeight);

			int hScaled = overlayScaled.getHeight(null);
			int wScaled = overlayScaled.getWidth(null);
			int yLoc = (h - hScaled);
			int x = (int) ((w - wScaled) * sprite.locationPct);
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics gfx = img.getGraphics();
			gfx.drawImage(baseScaled, 0, 0, Color.white, null);

			gfx.drawImage(overlayScaled, x, yLoc, null, null);
			gfx.dispose();
			img.flush();
			encoder.addFrame(img);
		}
		encoder.finish();
		outputStream.close();
	}
}
