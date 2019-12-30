package ca.hapke.campbinning.bot.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * @author Nathan Hapke
 */
public class PrettyTimeCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1713959501255857947L;
	private PrettyTime formatter = new PrettyTime();

	public void setValue(Object value) {
		if (value instanceof Long) {
			Long time = (Long) value;
			if (time == 0)
				setText("");
			else {
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime t = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());

				Duration duration = Duration.between(t, now);

				String result;
				long sec = Math.abs(duration.getSeconds());
				if (sec < 60) {
					result = sec + " seconds";
				} else {
					result = formatter.format(new Date(time));
				}
				setText(result);
			}
		} else {
			setText("");
		}
	}
}
