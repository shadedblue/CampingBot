package ca.hapke.campingbot.afd2021;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class AybBeginningImages extends AfdImagesStage<Void> {

	private static final int FIRST_IMAGE = 1;
//	private static final int FIRST_IMAGE = 8;
	private static final int LAST_IMAGE = 8;

	public AybBeginningImages(CampingBot bot) {
		super(bot);
	}

	@Override
	protected ByFrequency<Void> getFrequency() {
		return new ByFrequency<Void>(null, 5, ChronoUnit.SECONDS);
	}

	@Override
	protected void populateImages(List<ImageLink> images, Map<ImageLink, String> captionMap) {
		for (int i = FIRST_IMAGE; i <= LAST_IMAGE; i++) {
			images.add(getAybImgUrl("b", i));
		}
	}
}
