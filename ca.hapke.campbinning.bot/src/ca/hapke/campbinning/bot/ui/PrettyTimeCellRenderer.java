package ca.hapke.campbinning.bot.ui;

import java.time.ZonedDateTime;

import javax.swing.table.DefaultTableCellRenderer;

import ca.hapke.campbinning.bot.util.TimeFormatter;

/**
 * @author Nathan Hapke
 */
public class PrettyTimeCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1713959501255857947L;
	private TimeFormatter formatter = new TimeFormatter(2, ", ", true, true);

	@Override
	public void setValue(Object value) {
		if (value instanceof Long) {
			Long time = (Long) value;
			if (time == 0)
				setText("");
			else {
				String result = formatter.toPrettyString(time);
				setText(result);
			}
		} else if (value instanceof ZonedDateTime) {
			ZonedDateTime time = (ZonedDateTime) value;

			String result = formatter.toPrettyString(time);
			setText(result);
		} else {
			setText("");
		}
	}
}
