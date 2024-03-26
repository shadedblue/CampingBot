package ca.hapke.campingbot.afd2021;

import java.util.function.Consumer;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.afd2024.AfdTooManyDicks;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.response.TitleCommandResult;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class AybTopicChanger {
	private static final String[] CAPTIONS = { "ALL YOUR BROS", "BROS, WHAT YOU SAY?", "MAKE YOUR TIME BROS",
			"HA HA HA HA BROS", "MMMM ZIG BROS", "SET UP US THE BROS" };

	private CampingBot bot;
	private Resources res;

	public AybTopicChanger(CampingBot bot, Resources res) {
		this.bot = bot;
		this.res = res;
	}

	public Consumer<CampingChat> createTopicChanger() {
		String text = CollectionUtil.getRandom(CAPTIONS);

		return chat -> {
			TitleCommandResult title = new TitleCommandResult(AfdTooManyDicks.TooManyDicksCommand);
			for (int i = 0; i < 3; i++) {
				title.add(res.getRandomFaceEmoji());
			}
			title.add(text);
			for (int i = 0; i < 3; i++) {
				title.add(res.getRandomBallEmoji());
			}
			title.sendAndLog(bot, chat);
		};
	}
}
