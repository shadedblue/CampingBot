//package ca.hapke.campbinning.bot.commands.response;
//
//import java.util.List;
//
//import org.telegram.telegrambots.meta.api.objects.Message;
//
//import ca.hapke.campbinning.bot.BotCommand;
//import ca.hapke.campbinning.bot.CampingBotEngine;
//import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
//
///**
// * Can be used for logging to UI only, when the message(s) has already been sent... such as by Vote Initiation
// * 
// * @author Nathan Hapke
// */
//public class NoopCommandResult extends CommandResult {
//
//	private Message outgoingMsg;
//
//	public NoopCommandResult(BotCommand cmd, Message outgoingMsg, ResultFragment... fragments) {
//		super(cmd, fragments);
//		this.outgoingMsg = outgoingMsg;
//	}
//
//	public NoopCommandResult(BotCommand cmd, Message outgoingMsg, List<ResultFragment> fragments) {
//		super(cmd, fragments);
//		this.outgoingMsg = outgoingMsg;
//	}
//
//	@Override
//	public SendResult send(CampingBotEngine bot, Long chatId) {
//		MessageProcessor processor = bot.getProcessor();
//		String msg = processor.process(this.fragments);
//		return new SendResult(msg, outgoingMsg, null);
//	}
//
//}
