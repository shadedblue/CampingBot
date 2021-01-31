package ca.hapke.campingbot.afd2020;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.afd2021.AfdAybGameOver;
import ca.hapke.campingbot.afd2021.AfdHotPotato;
import ca.hapke.campingbot.afd2021.AybBeginningImages;
import ca.hapke.campingbot.afd2021.StageListener;

/**
 * @author Nathan Hapke
 */
public class AprilFoolsDayEnabler implements CalendaredEvent<Boolean> {

	private TimesProvider<Boolean> times;
//	private AfdTextCommand afdText;
//	private AfdMatrixPictures afdMatrix;
//	private MessageProcessor afdp;
	private AybBeginningImages beginningPics;
	protected AfdHotPotato hotPotato;
	protected AfdAybGameOver gameOver = new AfdAybGameOver();

	public AprilFoolsDayEnabler(AybBeginningImages afdPics, AfdHotPotato hotPotato) {
		this.beginningPics = afdPics;
		// Production
//		ByTimeOfYear<Boolean> enable = new ByTimeOfYear<Boolean>(4, 1, 7, 0, true);
//		ByTimeOfYear<Boolean> disable = new ByTimeOfYear<Boolean>(4, 1, 16, 20, false);
		this.hotPotato = hotPotato;

		// Testing
		ByTimeOfYear<Boolean> enable = new ByTimeOfYear<Boolean>(1, 30, 20, 26, true);

		times = new TimesProvider<>(enable);
	}

	@Override
	public TimesProvider<Boolean> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(Boolean value) {
		hotPotato.add(new StageListener() {
			@Override
			public void stageComplete(boolean success) {
				gameOver.begin();
			}

			@Override
			public void stageBegan() {
			}
		});

		beginningPics.add(new StageListener() {
			@Override
			public void stageComplete(boolean success) {
				hotPotato.begin();
			}

			@Override
			public void stageBegan() {
			}
		});

		beginningPics.begin();
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
