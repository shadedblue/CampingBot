package ca.hapke.campbinning.bot.commands.response;

/**
 * @author Nathan Hapke
 */
public class SendResult {

	public final String msg;
	public final Object extraData;

	public SendResult(String msg, Object extraData) {
		this.msg = msg;
		this.extraData = extraData;
	}

}
