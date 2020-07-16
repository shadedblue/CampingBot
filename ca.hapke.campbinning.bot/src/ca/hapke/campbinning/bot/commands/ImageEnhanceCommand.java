package ca.hapke.campbinning.bot.commands;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.games.Animation;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;

/**
 * @author Nathan Hapke
 */
public class ImageEnhanceCommand extends AbstractCommand {

	private CampingBot bot;

	public ImageEnhanceCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public String getCommandName() {
		return "enhance";
	}

	public CommandResult enhanceCommand(Message message) {
		Message replyTo = message.getReplyToMessage();
		if (replyTo == null)
			return null;

		String fileId = getFileId(replyTo);
		GetFile get = new GetFile().setFileId(fileId);
		File in = null, outImg = null;
		try {
			in = bot.downloadFile(bot.execute(get));
			BufferedImage originalImg = ImageIO.read(in);
			int w = originalImg.getWidth();
			int h = originalImg.getHeight();
			int wRemove = w / 6;
			int hRemove = h / 6;
			BufferedImage centreImg = originalImg.getSubimage(wRemove, hRemove, w - (2 * wRemove), h - (2 * hRemove));

			Resources res = bot.getRes();
			String str = "M" + res.getRandomBall() + "IY" + res.getRandomFace();
			Graphics graphics = centreImg.getGraphics();
			int size = centreImg.getHeight() / 10;
			graphics.setFont(new Font("SansSerif", Font.PLAIN, size));
			graphics.drawString(str, 5, 5 + size);
			outImg = File.createTempFile(bot.getBotUsername(), ".jpg");
			boolean success = ImageIO.write(centreImg, "jpeg", outImg);
			if (success)
				return new ImageCommandResult(BotCommand.ImageEnhance, outImg, new TextFragment("ENHANCE"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getFileId(Message replyTo) {
		Animation ani = replyTo.getAnimation();
		if (ani != null)
			return ani.getFileId();
		List<PhotoSize> pics = replyTo.getPhoto();
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

}
