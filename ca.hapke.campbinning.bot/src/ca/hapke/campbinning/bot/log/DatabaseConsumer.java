package ca.hapke.campbinning.bot.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.CampingSystem;
import ca.odell.glazedlists.EventList;

/**
 * @author Nathan Hapke
 */
public class DatabaseConsumer implements CalendaredEvent<Void>, AutoCloseable {
	private CampingSystem system;
	private EventLogger eventLogger;
	private Connection connection;
	private TimesProvider<Void> times;

	public DatabaseConsumer(CampingSystem system, EventLogger eventLogger) {
		this.system = system;
		this.eventLogger = eventLogger;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 15, ChronoUnit.SECONDS));
	}

	@Override
	public boolean shouldRun() {
		if (!system.isDbEnabled())
			return false;

		EventList<EventItem> dbLog = eventLogger.getDbLog();

		boolean hasEvents = false;
		if (dbLog.getReadWriteLock().readLock().tryLock()) {
			try {
				hasEvents = dbLog.size() > 0;
			} finally {
				dbLog.getReadWriteLock().readLock().unlock();
			}
		}
		return hasEvents;
	}

	@Override
	public void doWork(Void value) {
		EventList<EventItem> dbLog = eventLogger.getDbLog();
		dbLog.getReadWriteLock().writeLock().lock();

		try {
			if (dbLog.size() > 0 && system.isDbEnabled()) {
				String dbHost = system.getDbHost();
				int dbPort = system.getDbPort();
				String dbUser = system.getDbUser();
				String dbPass = system.getDbPass();

				try {
					if (connection == null || connection.isClosed()) {
						String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/camping";
						Properties props = new Properties();
						props.setProperty("user", dbUser);
						props.setProperty("password", dbPass);
						connection = DriverManager.getConnection(url, props);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					connection = null;
					return;
				}

				while (dbLog.size() > 0) {
					EventItem item = dbLog.get(0);
					try {
						long timestamp = item.d.getTime() / 1000;
						Object extraData = item.extraData;
						String columns = "\"timestamp\", \"campingUserId\", \"chatId\", \"telegramId\", \"campingType\", message";
						String values = "?,?,?,?,?,?";
						if (extraData != null) {
							columns = columns + ", \"extraData\"";
							values = values + ",?";
						}
						String sql = "INSERT INTO public.activity(" + columns + ") VALUES (" + values + ");";
						PreparedStatement ps = connection.prepareStatement(sql);
						ps.setLong(1, timestamp);
						ps.setInt(2, item.user.getCampingId());
						ps.setLong(3, item.chat.chatId);
						ps.setInt(4, item.telegramId);
						ps.setLong(5, item.command.getId());
						ps.setString(6, item.rest);
						if (extraData != null)
							setExtraData(ps, 7, extraData);
						ps.executeUpdate();
					} catch (SQLException e) {
					}
					dbLog.remove(0);
				}
			}
		} finally {
			dbLog.getReadWriteLock().writeLock().unlock();
		}
	}

	private void setExtraData(PreparedStatement ps, int i, Object extraData) throws SQLException {
		if (extraData instanceof String) {
			String val = (String) extraData;
			ps.setString(i, val);
		} else if (extraData instanceof Integer) {
			Integer val = (Integer) extraData;
			ps.setInt(i, val);
		}
	}

	@Override
	public void close() throws Exception {
		if (connection != null) {
			connection.rollback();
			connection.close();
		}
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}

}
