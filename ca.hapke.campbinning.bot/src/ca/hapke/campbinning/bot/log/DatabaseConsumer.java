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
import ca.hapke.campbinning.bot.log.DatabaseQuery.ColumnType;
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
						DatabaseQuery query = new DatabaseQuery("public.activity");
						int campingId = -1;
						long chatId = -1;
						long commandTypeId = -1;
						int tId = -1;
						if (item.user != null) {
							campingId = item.user.getCampingId();
						}
						if (item.chat != null) {
							chatId = item.chat.chatId;
						}
						if (item.telegramId != null) {
							tId = item.telegramId;
						}
						if (item.command != null) {
							commandTypeId = item.command.getId();
						}
						query.add("timestamp", ColumnType.Long, timestamp);
						query.add("campingUserId", ColumnType.Integer, campingId);
						query.add("chatId", ColumnType.Long, chatId);
						query.add("telegramId", ColumnType.Integer, tId);
						query.add("campingType", ColumnType.Long, commandTypeId);
						query.add("message", ColumnType.String, item.rest);
						query.add("extraData", item.extraData);

						PreparedStatement ps = query.createPreparedStatement(connection);

						ps.executeUpdate();
					} catch (SQLException e) {
						EventLogger.getInstance().add(new EventItem(e.toString()));
					}
					dbLog.remove(0);
				}
			}
		} finally {
			dbLog.getReadWriteLock().writeLock().unlock();
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
