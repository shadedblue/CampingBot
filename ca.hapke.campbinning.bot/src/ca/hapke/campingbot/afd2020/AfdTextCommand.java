package ca.hapke.campingbot.afd2020;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TitleCommandResult;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class AfdTextCommand implements TextCommand {

	private CampingBot bot;
	private MessageProcessor processor;
	private Resources resources;
	private boolean enabled = false;
	private CampingChat chat;

	public AfdTextCommand(CampingBot bot, MessageProcessor aprilFoolsDayProcessor, CampingChat chat) {
		this.bot = bot;
		this.chat = chat;
		this.processor = aprilFoolsDayProcessor;
		resources = bot.getRes();
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, CampingChat chat,
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
			TitleCommandResult titleCmd = new TitleCommandResult(PartyEverydayCommand.PartyEverydayCommand);

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

			String processed = processor.processString(longestWord, false);
			titleCmd.add(processed);

			for (int i = 0; i < 3; i++) {
				titleCmd.add(resources.getRandomFaceEmoji());
			}

			try {
				SendResult result = titleCmd.send(bot, chat.chatId);
				out = titleCmd;
				Message outgoingMsg = result.outgoingMsg;
				EventItem ei = new EventItem(PartyEverydayCommand.PartyEverydayCommand, bot.getMeCamping(),
						outgoingMsg.getDate(), chat, outgoingMsg.getMessageId(), outgoingMsg.getText(), null);
				EventLogger.getInstance().add(ei);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		return out;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		return enabled;
	}

	public void enable(boolean on) {
		enabled = on;
	}
}
