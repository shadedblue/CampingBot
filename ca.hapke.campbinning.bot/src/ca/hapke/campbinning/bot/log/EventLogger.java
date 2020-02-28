package ca.hapke.campbinning.bot.log;

import ca.hapke.campbinning.bot.CommandType;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class EventLogger {
	private static EventLogger instance = new EventLogger();

	public static EventLogger getInstance() {
		return instance;
	}

	private EventList<EventItem> fullLog = GlazedLists.threadSafeList(new BasicEventList<EventItem>());
	private EventList<EventItem> uiLog = GlazedLists.threadSafeList(new BasicEventList<EventItem>());
	private EventList<EventItem> dbLog = GlazedLists.threadSafeList(new BasicEventList<EventItem>());

	private EventLogger() {
		fullLog.addListEventListener(new ListEventListener<EventItem>() {

			@Override
			public void listChanged(ListEvent<EventItem> listChanges) {
				fullLog.getReadWriteLock().readLock().lock();
				uiLog.getReadWriteLock().writeLock().lock();
				dbLog.getReadWriteLock().writeLock().lock();
				while (listChanges.next()) {
					EventItem e = fullLog.get(listChanges.getIndex());
					if (listChanges.getType() == ListEvent.INSERT) {
						CommandType command = e.command;
						uiLog.add(e);
						if (command != null && command.isForDb()) {
							dbLog.add(e);
						}
					}
				}
				dbLog.getReadWriteLock().writeLock().unlock();
				fullLog.getReadWriteLock().readLock().unlock();

				if (shouldRemove()) {
					uiLog.remove(0);
				}
				uiLog.getReadWriteLock().writeLock().unlock();
			}
		});
	}

	private boolean shouldRemove() {
		if (uiLog.size() > getThreshold())
			return true;

		return false;
	}

	private int getThreshold() {
		return 50;
	}

	public void add(EventItem e) {
		if (e != null)
			fullLog.add(e);
	}

	public EventList<EventItem> getUiLog() {
		return uiLog;
	}

	public EventList<EventItem> getDbLog() {
		return dbLog;
	}
}
