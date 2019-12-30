package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.interval.IntervalByExecutionTime;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatTimeExecutionTimeTracker extends CampingTableFormat
		implements AdvancedTableFormat<IntervalByExecutionTime> {

	public TableFormatTimeExecutionTimeTracker() {
		super(new String[] { "Thread", "Next Run", "Should Run" }, new int[] { 100, 100, 50 });
	}

	@Override
	public Object getColumnValue(IntervalByExecutionTime ett, int column) {

		switch (column) {
		case 0:
			return ett.getClass().getSimpleName();
		case 1:
			return ett.getNextExecutionTime();
		case 2:
			return ett.shouldRun();
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
			return Long.class;
		case 2:
			return Boolean.class;
		}
		throw new IllegalStateException();
	}
}
