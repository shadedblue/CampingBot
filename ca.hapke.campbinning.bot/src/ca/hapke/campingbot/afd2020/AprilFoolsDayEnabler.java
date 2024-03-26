package ca.hapke.campingbot.afd2020;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.afd2024.AfdTooManyDicks;

/**
 * @author Nathan Hapke
 */
public class AprilFoolsDayEnabler implements CalendaredEvent<Boolean> {

	private TimesProvider<Boolean> times;
//	private AfdMatrixPictures afdMatrix;
	private AfdTooManyDicks afdDicks;

//	public static final boolean AFD_DEBUG = true;
	public static final boolean AFD_DEBUG = false;

	public AprilFoolsDayEnabler(CampingBot bot, AfdTooManyDicks afdDicks) {
		this.afdDicks = afdDicks;
		ByTimeOfYear<Boolean> enable;
		if (AFD_DEBUG) {
			// Testing
			enable = new ByTimeOfYear<Boolean>(3, 26, 9, 37, true);
		} else {
			// Production
			enable = new ByTimeOfYear<Boolean>(4, 1, 8, 50, true);
		}

		times = new TimesProvider<>(enable);
	}

	@Override
	public TimesProvider<Boolean> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<Boolean> event, Boolean value) {
		afdDicks.enable(value);
	}

	@Override
	public boolean shouldRun() {
		return true;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}
}
