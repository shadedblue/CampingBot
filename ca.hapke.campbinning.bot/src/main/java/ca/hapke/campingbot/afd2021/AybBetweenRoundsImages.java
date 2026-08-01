//package ca.hapke.campingbot.afd2021;
//
//import java.util.List;
//import java.util.Map;
//
//import ca.hapke.calendaring.event.StartupMode;
//import ca.hapke.calendaring.timing.ByFrequency;
//import ca.hapke.campingbot.CampingBot;
//import ca.hapke.campingbot.afd2020.AprilFoolsDayEnabler;
//import ca.hapke.campingbot.users.CampingUser;
//import ca.hapke.campingbot.util.ImageLink;
//
///**
// * @author Nathan Hapke
// */
//public class AybBetweenRoundsImages extends AfdImagesStage<Void> {
//
//	private CampingUser killed;
//
//	public AybBetweenRoundsImages(CampingBot bot, CampingUser killed) {
//		super(bot);
//		this.killed = killed;
//	}
//
//	@Override
//	public StartupMode getStartupMode() {
//		return StartupMode.Always;
//	}
//
//	@Override
//	protected ByFrequency<Void> getFrequency() {
//		return AprilFoolsDayEnabler.BETWEEN_IMAGES;
//	}
//
//	@Override
//	protected void populateImages(List<ImageLink> images, Map<ImageLink, String> captionMap) {
//		images.add(getAybImgUrl("k1", 1));
//		images.add(getAybImgUrl("k1", killed.getTelegramId()));
//		images.add(getAybImgUrl("k1", 2));
//	}
//
//}
