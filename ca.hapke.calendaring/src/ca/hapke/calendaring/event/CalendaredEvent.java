package ca.hapke.calendaring.event;

import java.time.Instant;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public abstract class CalendaredEvent {
	public abstract List<Instant> getTimes();
}
