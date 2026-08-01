package ca.hapke.campingbot.response;

import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Nathan Hapke
 */
public class SendResult {

	public final String msg;
	public final Message outgoingMsg;
	public final Object extraData;

	public SendResult(String msg, Message outgoingMsg, Object extraData) {
		this.msg = msg;
		this.outgoingMsg = outgoingMsg;
		this.extraData = extraData;
	}

}
