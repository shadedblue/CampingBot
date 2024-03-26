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

	public static final boolean AFD_DEBUG = true;
//	public static final boolean AFD_DEBUG = false;

	public AprilFoolsDayEnabler(CampingBot bot, AfdTooManyDicks afdDicks) {
		this.afdDicks = afdDicks;
		ByTimeOfYear<Boolean> enable, disable;
		if (AFD_DEBUG) {
			// Testing
			enable = new ByTimeOfYear<Boolean>(3, 25, 16, 4, true);
			int min2 = enable.min + 16;
			int h2 = enable.h;
			if (min2 >= 60) {
				min2 -= 60;
				h2++;
			}
			disable = new ByTimeOfYear<Boolean>(enable.month, enable.d, h2, min2, false);
		} else {
			// Production
			enable = new ByTimeOfYear<Boolean>(4, 1, 9, 0, true);
			disable = new ByTimeOfYear<Boolean>(enable.month, enable.d, enable.h + 8, enable.min, false);
		}

		times = new TimesProvider<>(enable, disable);
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
