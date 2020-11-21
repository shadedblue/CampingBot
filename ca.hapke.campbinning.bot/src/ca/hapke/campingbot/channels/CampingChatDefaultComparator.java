package ca.hapke.campingbot.channels;

import java.util.Comparator;

/**
 * @author Nathan Hapke
 */
public class CampingChatDefaultComparator implements Comparator<CampingChat> {

	@Override
	public int compare(CampingChat a, CampingChat b) {
		return (int) ((a.chatId - b.chatId) % 0xFFFFFFFF);
	}

}
