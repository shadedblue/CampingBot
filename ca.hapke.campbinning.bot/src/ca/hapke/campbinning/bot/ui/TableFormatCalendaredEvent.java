package ca.hapke.campbinning.bot.ui;

import java.time.ZonedDateTime;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatCalendaredEvent extends CampingTableFormat implements AdvancedTableFormat<CalendaredEvent> {

	public TableFormatCalendaredEvent() {
		super(new String[] { "Thread", "Next", "Prev", "Run?" }, new int[] { 125, 75, 75, 25 });
	}

	@Override
	public Object getColumnValue(CalendaredEvent e, int column) {

		switch (column) {
		case 0:
			return e.getClass().getSimpleName();
		case 1:
			return e.getTimeProvider().getNearestFuture().getFuture();
		case 2:
			return e.getTimeProvider().getLastExecTime();
		case 3:
			return e.shouldRun();
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
		case 2:
			return ZonedDateTime.class;
		case 3:
			return Boolean.class;
		}
		throw new IllegalStateException();
	}
}
