//package ca.hapke.campingbot.afd2021;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//
//import ca.hapke.calendaring.timing.ByFrequency;
//import ca.hapke.campingbot.CampingBot;
//import ca.hapke.campingbot.afd2020.AprilFoolsDayEnabler;
//import ca.hapke.campingbot.channels.CampingChat;
//import ca.hapke.campingbot.util.ImageLink;
//
///**
// * @author Nathan Hapke
// */
//public class AybBeginningImages extends AfdImagesStage<Void> {
//	private static final int FIRST_IMAGE;
//	private static final int LAST_IMAGE = 8;
//
//	static {
//		if (AprilFoolsDayEnabler.AFD_DEBUG) {
//			FIRST_IMAGE = 8;
//		} else {
//			FIRST_IMAGE = 1;
//		}
//	}
//	private Consumer<CampingChat> topicChanger;
//
//	public AybBeginningImages(CampingBot bot, AybTopicChanger topicChanger) {
//		super(bot);
//		this.topicChanger = topicChanger.createTopicChanger();
//	}
//
//	@Override
//	protected ByFrequency<Void> getFrequency() {
//		return AprilFoolsDayEnabler.BETWEEN_IMAGES;
//	}
//
//	@Override
//	protected void populateImages(List<ImageLink> images, Map<ImageLink, String> captionMap) {
//		for (int i = FIRST_IMAGE; i <= LAST_IMAGE; i++) {
//			images.add(getAybImgUrl("b", i));
//		}
//	}
//
//	@Override
//	protected void doStep(CampingChat chat, int step) {
//		if (step == 0) {
//			topicChanger.accept(chat);
//		}
//		super.doStep(chat, step);
//	}
//
//}
