package ca.hapke.campingbot.afd2021;

import java.util.function.Consumer;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.response.TitleCommandResult;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class AybTopicChanger {
//	private static final int MAX_ICON = 3;
//	private static final ImageLink[] ICONS;
	private static final String[] CAPTIONS = { "ALL YOUR BROS", "BROS, WHAT YOU SAY?", "MAKE YOUR TIME BROS",
			"HA HA HA HA BROS", "MMMM ZIG BROS", "SET UP US THE BROS" };

//	static {
//		ICONS = new ImageLink[MAX_ICON];
//		for (int i = 1; i <= MAX_ICON; i++) {
//			ICONS[i - 1] = AfdImagesStage.getAybImgUrl("icon", i);
//		}
//	}

	private CampingBot bot;
	private Resources res;

	public AybTopicChanger(CampingBot bot, Resources res) {
		this.bot = bot;
		this.res = res;
	}

	public Consumer<CampingChat> createTopicChanger() {
//		ImageLink img = CollectionUtil.getRandom(ICONS);
		String text = CollectionUtil.getRandom(CAPTIONS);

		return chat -> {
			TitleCommandResult title = new TitleCommandResult(AfdHotPotato.HotPotatoCommand);
			for (int i = 0; i < 3; i++) {
				title.add(res.getRandomFaceEmoji());
			}
			title.add(text);
			for (int i = 0; i < 3; i++) {
				title.add(res.getRandomBallEmoji());
			}
			title.sendAndLog(bot, chat);

//			GroupChatPhotoCommandResult iconSet = new GroupChatPhotoCommandResult(AfdHotPotato.HotPotatoCommand, img);
//			iconSet.sendAndLog(bot, chat);
		};
	}
}
