package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.interval.ExecutionTimeTracker;
import ca.hapke.campbinning.bot.interval.IntervalBySeconds;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatSecondsExecutionTimeTracker extends CampingTableFormat
		implements AdvancedTableFormat<ExecutionTimeTracker<IntervalBySeconds>> {

	public TableFormatSecondsExecutionTimeTracker() {
		super(new String[] { "Thread", "Interval", "Last Update", "Should Run" }, new int[] { 100, 50, 100, 50 });
	}

	@Override
	public Object getColumnValue(ExecutionTimeTracker<IntervalBySeconds> ett, int column) {

		switch (column) {
		case 0:
			return ett.getItem().getClass().getSimpleName();
		case 1:
			return ett.getItem().getSeconds();
		case 2:
			return ett.getTime();
		case 3:
			return ett.getItem().shouldRun();
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
			return Integer.class;
		case 2:
			return Long.class;
		case 3:
			return Boolean.class;
		}
		throw new IllegalStateException();
	}
}
