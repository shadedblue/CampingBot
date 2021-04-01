package ca.hapke.campingbot.afd2021;

import java.util.List;
import java.util.Map;

import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.afd2020.AprilFoolsDayEnabler;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class AybEndGameImages extends AfdImagesStage<Void> {
	private static final String CATEGORY = "f";
	private static final int FIRST_IMAGE = 0;
	private static final int LAST_IMAGE = 10;
	private CampingUser winner;

	public AybEndGameImages(CampingBot bot, CampingUser winner) {
		super(bot);
		this.winner = winner;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Always;
	}

	@Override
	protected ByFrequency<Void> getFrequency() {
		return AprilFoolsDayEnabler.BETWEEN_IMAGES;
	}

	@Override
	protected void populateImages(List<ImageLink> images, Map<ImageLink, String> captionMap) {
		for (int i = FIRST_IMAGE; i < LAST_IMAGE; i++) {
			images.add(getAybImgUrl(CATEGORY, i));
		}
		images.add(getAybImgUrl(CATEGORY, winner.getTelegramId()));
		images.add(getAybImgUrl(CATEGORY, LAST_IMAGE));
		images.add(new ImageLink("http://www.hapke.ca/images/afd21/ayb-f-allballs.gif", ImageLink.GIF));
		images.add(new ImageLink("http://www.hapke.ca/images/42069.jpg", ImageLink.STATIC));
	}
}
