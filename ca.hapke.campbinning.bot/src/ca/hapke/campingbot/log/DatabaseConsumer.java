package ca.hapke.campingbot.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.inline.HideItMessage;
import ca.hapke.campingbot.log.DatabaseQuery.ColumnType;
import ca.hapke.campingbot.users.CampingUser;
import ca.odell.glazedlists.EventList;

/**
 * @author Nathan Hapke
 */
public class DatabaseConsumer implements CalendaredEvent<Void>, AutoCloseable {
	public static final String SCHEMA = "public";

	private static DatabaseConsumer instance;
	private CampingSystem system;
	private EventLogger eventLogger;
	private Connection connection;
	private TimesProvider<Void> times;
	private EntityManager manager;

	public static DatabaseConsumer init(CampingSystem system, EventLogger eventLogger) {
		instance = new DatabaseConsumer(system, eventLogger);
		return instance;
	}

	public static DatabaseConsumer getInstance() {
		return instance;
	}

	private DatabaseConsumer(CampingSystem system, EventLogger eventLogger) {
		this.system = system;
		this.eventLogger = eventLogger;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 15, ChronoUnit.SECONDS));
	}

	private Connection getConnection() {
		String dbHost = system.getDbHost();
		int dbPort = system.getDbPort();
		String dbUser = system.getDbUser();
		String dbPass = system.getDbPass();
		String db = system.getDbDb();
		String dbDriver = system.getDbDriver();

		try {
			if (system.isDbEnabled() && (connection == null || connection.isClosed())) {

				String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + db;
				Properties props = new Properties();
				props.setProperty("user", dbUser);
				props.setProperty("password", dbPass);
				connection = DriverManager.getConnection(url, props);
			}
		} catch (SQLException e) {
			connection = null;
			e.printStackTrace();
		}

		try {
			if (system.isDbEnabled() && manager == null) {
				Map<String, String> persistenceMap = new HashMap<String, String>();
				String url2 = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + db;
				persistenceMap.put("javax.persistence.jdbc.url", url2);
				persistenceMap.put("javax.persistence.jdbc.user", dbUser);
				persistenceMap.put("javax.persistence.jdbc.password", dbPass);
				persistenceMap.put("javax.persistence.jdbc.driver", dbDriver);
				persistenceMap.put("javax.persistence.schema-generation.database.action", "update");
				persistenceMap.put("hibernate.hbm2ddl.auto", "update");
				persistenceMap.put("hibernate.show_sql", "true");

				// EntityManagerFactory emf = Persistence.createEntityManagerFactory(CampingPersistence.UNIT_NAME,
//						persistenceMap);
				CampingPersistenceUnitInfo unitInfo = new CampingPersistenceUnitInfo();

				unitInfo.add(CampingUser.class.getName());
				unitInfo.add(HideItMessage.class.getName());

				String chatClass = CampingChat.class.getName();
				unitInfo.add(chatClass);
				unitInfo.add(chatClass + ".activeUserIds");

				String categoriedClass = CategoriedPersistence.class.getName();
				unitInfo.add(categoriedClass);
				unitInfo.add(categoriedClass + "." + CategoriedPersistence.VALUES);

				EntityManagerFactory emf = new EntityManagerFactoryBuilderImpl(
						new PersistenceUnitInfoDescriptor(unitInfo), persistenceMap).build();
//				PersistenceProvider eclipseLinkProvider = new PersistenceProvider();
//				EntityManagerFactory emf = eclipseLinkProvider.createContainerEntityManagerFactory(unitInfo,
//						persistenceMap);

				manager = emf.createEntityManager();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	public EntityManager getManager() {
		if (manager == null)
			getConnection();
		return manager;
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
	public void doWork(ByCalendar<Void> event, Void value) {
		EventList<EventItem> dbLog = eventLogger.getDbLog();
		dbLog.getReadWriteLock().writeLock().lock();

		try {
			if (dbLog.size() > 0 && system.isDbEnabled()) {
				Connection connection = getConnection();
				if (connection == null)
					return;

				while (dbLog.size() > 0) {
					EventItem item = dbLog.get(0);
					try {
						long timestamp = item.d.getTime() / 1000;
						DatabaseQuery query = new DatabaseQuery("activity");
						long campingId = -1;
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
						query.add("campingUserId", ColumnType.Long, campingId);
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

		if (manager != null) {
			manager.getTransaction().rollback();
			manager.close();
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

	public void updatePersistence(Object e) {
		EntityManager mgr = getManager();
		mgr.getTransaction().begin();
		mgr.merge(e);
		mgr.getTransaction().commit();
	}

	public void addPersistence(Object e) {
		EntityManager mgr = getManager();
		mgr.getTransaction().begin();
		mgr.persist(e);
		mgr.getTransaction().commit();
	}
}
