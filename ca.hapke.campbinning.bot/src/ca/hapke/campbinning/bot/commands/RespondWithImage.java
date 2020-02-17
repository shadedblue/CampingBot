package ca.hapke.campbinning.bot.commands;

import java.util.ArrayList;
import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * TODO remove
 * 
 * @author Nathan Hapke
 */
public class RespondWithImage {

	protected CampingBot bot;
	protected final List<ImageLink> images = new ArrayList<>();

	public RespondWithImage(CampingBot bot) {
		this.bot = bot;
	}

	public CommandResult sendImage(BotCommand commandType, Long chatId, String caption) {

		ImageLink image = CampingUtil.getRandom(images);

		return new ImageCommandResult(commandType, image, new TextFragment(caption));
	}

	public boolean add(ImageLink e) {
		return images.add(e);
	}

}