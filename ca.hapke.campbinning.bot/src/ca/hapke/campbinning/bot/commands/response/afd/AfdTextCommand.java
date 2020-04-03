package ca.hapke.campbinning.bot.commands.response.afd;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.commands.TextCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.SendResult;
import ca.hapke.campbinning.bot.commands.response.TitleCommandResult;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class AfdTextCommand implements TextCommand {

	private CampingBot bot;
	private MessageProcessor processor;
	private Resources resources;
	private boolean enabled = false;
	private CampingChat chat;

	public AfdTextCommand(CampingBot bot, AprilFoolsDayProcessor aprilFoolsDayProcessor, CampingChat chat) {
		this.bot = bot;
		this.chat = chat;
		this.processor = aprilFoolsDayProcessor.getSillyPipe();
		resources = bot.getRes();
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		String incoming = message.getText();
		CommandResult out = null;

//		String result = processor.processString(incoming);
////		System.out.println(result);
//		try {
//			out = new TextCommandResult(BotCommand.Talk, new TextFragment(result));
//			out.send(bot, AprilFoolsDayEnabler.TARGET_CHAT);
//		} catch (TelegramApiException e) {
//			out = null;
//		}

		if (Math.random() < 0.1) {
			TitleCommandResult titleCmd = new TitleCommandResult(BotCommand.PartyEveryday);

			for (int i = 0; i < 3; i++) {
				titleCmd.add(resources.getRandomBallEmoji());
			}

			String[] words = incoming.split(" ");
			String longestWord = words[0];
			int longest = longestWord.length();
			for (int i = 1; i < words.length; i++) {
				String word = words[i];
				int length = word.length();
				if (length > longest) {
					longest = length;
					longestWord = word;
				}
			}

			String processed = processor.processString(longestWord);
			titleCmd.add(processed);

			for (int i = 0; i < 3; i++) {
				titleCmd.add(resources.getRandomFaceEmoji());
			}

			try {
				SendResult result = titleCmd.send(bot, chat.chatId);
				out = titleCmd;
				Message outgoingMsg = result.outgoingMsg;
				EventItem ei = new EventItem(BotCommand.PartyEveryday, bot.getMeCamping(), outgoingMsg.getDate(), chat,
						outgoingMsg.getMessageId(), outgoingMsg.getText(), null);
				EventLogger.getInstance().add(ei);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		return out;
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		return enabled;
	}

	public void enable(boolean on) {
		enabled = on;
	}
}
