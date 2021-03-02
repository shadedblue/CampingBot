package ca.hapke.campingbot.afd2020;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class AprilFoolsDayEnabler implements CalendaredEvent<Boolean> {

	private TimesProvider<Boolean> times;
	private AfdTextCommand afdText;
	private AfdMatrixPictures afdMatrix;
	private MessageProcessor afdp;

	public AprilFoolsDayEnabler(AfdTextCommand afdText, AfdMatrixPictures afdMatrix, MessageProcessor afdp) {
		this.afdText = afdText;
		this.afdMatrix = afdMatrix;
		this.afdp = afdp;
		ByTimeOfYear<Boolean> enable = new ByTimeOfYear<Boolean>(4, 1, 7, 0, true);
		ByTimeOfYear<Boolean> disable = new ByTimeOfYear<Boolean>(4, 1, 16, 20, false);

//		ByTimeOfYear<Boolean> enable = new ByTimeOfYear<Boolean>(3, 29, 13, 34, true);
//		ByTimeOfYear<Boolean> disable = new ByTimeOfYear<Boolean>(3, 29, 13, 35, false);

		times = new TimesProvider<>(enable, disable);
	}

	@Override
	public TimesProvider<Boolean> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(Boolean value) {
		afdText.enable(value);
		afdMatrix.enable(value);
		afdp.setEnabled(value);
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
