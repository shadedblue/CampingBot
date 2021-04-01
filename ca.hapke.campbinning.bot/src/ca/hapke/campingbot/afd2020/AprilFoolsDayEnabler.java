package ca.hapke.campingbot.afd2020;

import java.time.temporal.ChronoUnit;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
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

//	public static final boolean AFD_DEBUG = true;
	public static final boolean AFD_DEBUG = false;
	public static final ByFrequency<Void> BETWEEN_IMAGES;
	public static final ByFrequency<Void> ROUND_LENGTH;
	static {
		if (AFD_DEBUG) {
			BETWEEN_IMAGES = new ByFrequency<Void>(null, 10, ChronoUnit.SECONDS);
			ROUND_LENGTH = new ByFrequency<Void>(null, 2, ChronoUnit.MINUTES);
		} else {
			BETWEEN_IMAGES = new ByFrequency<Void>(null, 30, ChronoUnit.SECONDS);
			ROUND_LENGTH = new ByFrequency<Void>(null, 45, ChronoUnit.MINUTES);
		}
	}

	public AprilFoolsDayEnabler(CampingBot bot, AfdHotPotato hotPotato) {
		this.hotPotato = hotPotato;
		this.beginningPics = new AybBeginningImages(bot, hotPotato.getTopicChanger());
		ByTimeOfYear<Boolean> enable;
		if (AFD_DEBUG) {
			// Testing
			enable = new ByTimeOfYear<Boolean>(3, 28, 16, 17, true);
		} else {
			// Production
			enable = new ByTimeOfYear<Boolean>(4, 1, 16, 20, true);
		}

		times = new TimesProvider<>(enable);
	}

	@Override
	public TimesProvider<Boolean> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<Boolean> event, Boolean value) {
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
