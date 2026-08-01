//package ca.hapke.campingbot.response;
//
//import java.util.List;
//
//import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
//import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
//import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
//import org.telegram.telegrambots.meta.api.objects.InputFile;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import ca.hapke.campingbot.BotConstants;
//import ca.hapke.campingbot.api.CampingBotEngine;
//import ca.hapke.campingbot.commands.api.CommandType;
//import ca.hapke.campingbot.processors.MessageProcessor;
//import ca.hapke.campingbot.response.fragments.ResultFragment;
//import ca.hapke.campingbot.util.ImageLink;
//import lombok.NonNull;
//
///**
// * @author Nathan Hapke
// */
//public class EditMediaCommandResult extends CommandResult {
//	private Message message;
//	private ImageLink image;
//
//	public EditMediaCommandResult(CommandType cmd, ImageLink image, Message message) {
//		super(cmd);
//		this.image = image;
//		this.message = message;
//	}
//
//	public EditMediaCommandResult(CommandType cmd, ImageLink image, Message message, ResultFragment... fragments) {
//		super(cmd, fragments);
//		this.image = image;
//		this.message = message;
//	}
//
//	public EditMediaCommandResult(CommandType cmd, ImageLink image, Message message, List<ResultFragment> fragments) {
//		super(cmd, fragments);
//		this.image = image;
//		this.message = message;
//	}
//
//	@Override
//	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
//		MessageProcessor processor = bot.getProcessor();
//		String caption = processor.process(true, this.fragments);
//		
//		EditMessageMedia send = new EditMessageMedia();
//		String url  = processor.processImageUrl(image.url);
//		InputMedia media;
//		EditMessageMedia edit = new EditMessageMedia(chatId, message.getMessageId(), null, media, this.keyboard);
//		
//		switch (image.type) {
//		case ImageLink.STATIC:
//			SendPhoto p = new SendPhoto();
//			p.setPhoto(new InputFile(url));
//			outMsg = completePhoto(bot, chatId, caption, p);
//			break;
//		case ImageLink.GIF:
//			SendAnimation ani = new SendAnimation();
//			ani.setChatId(Long.toString(chatId));
//			ani.setAnimation(new InputFile(url));
//			if (caption != null && caption.length() > 0) {
//				ani.setCaption(caption);
//				ani.setParseMode(BotConstants.MARKDOWN);
//			}
//			if (replyTo != null)
//				ani.setReplyToMessageId(replyTo);
//			if (keyboard != null)
//				ani.setReplyMarkup(keyboard);
//			outMsg = bot.execute(ani);
//			break;
//
//			
//		return null;
//	}
//
//}
