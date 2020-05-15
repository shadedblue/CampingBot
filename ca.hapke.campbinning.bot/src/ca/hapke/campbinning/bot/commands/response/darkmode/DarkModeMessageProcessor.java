//package ca.hapke.campbinning.bot.commands.response.darkmode;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import ca.hapke.calendaring.event.CalendaredEvent;
//import ca.hapke.calendaring.event.StartupMode;
//import ca.hapke.calendaring.timing.ByCalendar;
//import ca.hapke.calendaring.timing.ByTimeOfDay;
//import ca.hapke.calendaring.timing.TimesProvider;
//import ca.hapke.campbinning.bot.category.CategoriedItems;
//import ca.hapke.campbinning.bot.category.HasCategories;
//import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
//import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
//import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
//import ca.hapke.campbinning.bot.log.EventItem;
//import ca.hapke.campbinning.bot.log.EventLogger;
//import ca.hapke.campbinning.bot.util.CampingUtil;
//
///**
// * @author Nathan Hapke
// */
//public class DarkModeMessageProcessor extends MessageProcessor
//		implements HasCategories<String>, CalendaredEvent<Boolean> {
//
//	private static final String SUNSET = "sunset";
//	private DarkModeSwitch mode = DarkModeSwitch.Light;
//	private TimesProvider<Boolean> times;
//	private CategoriedItems<String> sunsetPhrases = new CategoriedItems<>(SUNSET);
//
//	public DarkModeMessageProcessor() {
//		sunsetPhrases.putAll(SUNSET, "dark mode enabled!\n(sshhh... be quiet... nighty nite time)",
//				"dark mode enabled!\n(sshhh... be quiet... the chilrens are sleping)");
//
//		List<ByCalendar<Boolean>> targets = new ArrayList<>();
//		targets.add(new ByTimeOfDay<Boolean>(20, 0, true));
//		targets.add(new ByTimeOfDay<Boolean>(6, 30, false));
//
//		times = new TimesProvider<>(targets);
//	}
//
//	@Override
//	protected String internalProcessStringFragment(String input) {
//		if (input == null)
//			return null;
//
//		String output = input;
//		switch (mode) {
//		case Light:
//			// noop
//			break;
//		case Sunset:
//		case Dark:
//			output = output.toLowerCase();
//			break;
//
//		}
//
//		return output;
//	}
//
//	@Override
//	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
//
//		switch (mode) {
//		case Dark:
//			break;
//		case Light:
//			break;
//		case Sunset:
//			int length = fragments.length;
//			fragments = Arrays.copyOf(fragments, length + 1);
//			String sunsetText = "\n\n" + CampingUtil.getRandom(sunsetPhrases.getList(SUNSET));
//			fragments[length] = new TextFragment(sunsetText);
//			break;
//
//		}
//
//		return fragments;
//	}
//
//	@Override
//	protected String internalAfterStringAssembled(String input) {
//		switch (mode) {
//		case Dark:
//			break;
//		case Light:
//			// noop
//			break;
//		case Sunset:
//			mode = DarkModeSwitch.Dark;
//			EventLogger.getInstance().add(new EventItem("Changing dark mode to: " + mode.toString()));
//			break;
//		}
//
//		return input;
//	}
//
//	@Override
//	protected String internalProcessImageUrl(String url) {
//
//		switch (mode) {
//		case Light:
//			// NOOP
//			break;
//		case Dark:
//		case Sunset:
//			try {
//				int dotIndex = url.lastIndexOf('.');
//				String ext = url.substring(dotIndex + 1);
//				String preDot = url.substring(0, dotIndex);
//				url = preDot + "-dark." + ext;
//			} catch (Exception e) {
//
//			}
//			break;
//		}
//		return url;
//	}
//
//	@Override
//	public List<String> getCategoryNames() {
//		return sunsetPhrases.getCategoryNames();
//	}
//
//	@Override
//	public void addItem(String category, String value) {
//		sunsetPhrases.put(category, value);
//	}
//
//	@Override
//	public String getContainerName() {
//		return SUNSET;
//	}
//
//	@Override
//	public TimesProvider<Boolean> getTimeProvider() {
//		return times;
//	}
//
//	@Override
//	public void doWork(Boolean value) {
//		if (value)
//			mode = DarkModeSwitch.Sunset;
//		else
//			mode = DarkModeSwitch.Light;
//
//		EventLogger.getInstance().add(new EventItem("Changing dark mode to: " + mode.toString()));
//	}
//
//	@Override
//	public boolean shouldRun() {
//		return true;
//	}
//
//	@Override
//	public StartupMode getStartupMode() {
//		return StartupMode.Always;
//	}
//
//}
