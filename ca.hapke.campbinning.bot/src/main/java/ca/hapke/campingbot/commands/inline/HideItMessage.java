package ca.hapke.campingbot.commands.inline;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Nathan Hapke
 */
@Entity
@Table(name = HideItMessage.HIDE_IT_TABLE)
public class HideItMessage implements Serializable {
	private static final long serialVersionUID = 8483491596290846747L;
	public static final String HIDE_IT_TABLE = "HideItMessage";
	@Id
	private int messageId;
	private String clearText;

	public HideItMessage() {
		this.messageId = -1;
		this.clearText = null;
	}

	public HideItMessage(int messageId, String clearText) {
		this.messageId = messageId;
		this.clearText = clearText;
	}

	public int getMessageId() {
		return messageId;
	}

	public String getClearText() {
		return clearText;
	}

	public void setMessageId(int messageId) {
		if (this.messageId == -1)
			this.messageId = messageId;
	}

	public void setClearText(String clearText) {
		if (this.clearText == null)
			this.clearText = clearText;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Hide[");
		builder.append(messageId);
		builder.append("=>");
		if (clearText != null) {
			builder.append(clearText);
		}
		builder.append("]");
		return builder.toString();
	}

}
