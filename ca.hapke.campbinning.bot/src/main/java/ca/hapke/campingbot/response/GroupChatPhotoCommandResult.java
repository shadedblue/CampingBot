//package ca.hapke.campingbot.response;
//
//import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
//import org.telegram.telegrambots.meta.api.objects.InputFile;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import ca.hapke.campingbot.api.CampingBotEngine;
//import ca.hapke.campingbot.commands.api.CommandType;
//import ca.hapke.campingbot.processors.MessageProcessor;
//import ca.hapke.campingbot.response.ImageCommandResult.SendMode;
//import ca.hapke.campingbot.util.ImageLink;
//
///**
// * @author Nathan Hapke
// */
//public class GroupChatPhotoCommandResult extends CommandResult {
//
//	private ImageLink image;
//	private ImageCommandResult.SendMode mode;
//
//	public GroupChatPhotoCommandResult(CommandType cmd, ImageLink image) {
//		super(cmd);
//		this.image = image;
//		this.mode = SendMode.Url;
//	}
//
//	@Override
//	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
//		MessageProcessor processor = bot.getProcessor();
//
//		String url = "";
//		try {
//			Boolean outMsg = null;
//			switch (mode) {
//			case Url:
//				url = processor.processImageUrl(image.url);
//				SetChatPhoto p = new SetChatPhoto();
//				p.setPhoto(new InputFile(url));
//				p.setChatId(Long.toString(chatId));
//				outMsg = bot.execute(p);
//				break;
//			case File:
//				break;
//			}
//
//			if (outMsg == null || outMsg == Boolean.FALSE)
//				return null;
//			else
//				return new SendResult(url, null, Long.toString(chatId));
//		} catch (TelegramApiException e) {
//			return new SendResult(e.getMessage(), null, url);
//		}
//	}
//}
