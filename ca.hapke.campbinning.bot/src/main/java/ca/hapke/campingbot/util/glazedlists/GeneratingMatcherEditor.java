package ca.hapke.campingbot.util.glazedlists;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.SetMatcherEditor;

public class GeneratingMatcherEditor<T> extends AbstractMatcherEditor<T> {
	public class GenerateMatcherThread extends Thread {
		@Override
		public void run() {
			Matcher<T> matcher = null;
			switch (mode) {
			case Custom: {
				sourceList.getReadWriteLock().readLock().lock();
				try {
					Set<T> result = new HashSet<>(sourceList.size());
					for (T t : sourceList) {
						if (p.test(t)) {
							result.add(t);
						}
					}
					matcher = new SetMatcher<>(result);
				} finally {
					sourceList.getReadWriteLock().readLock().unlock();
				}
				fireChanged(matcher);
				break;
			}
			case LastN: {
				sourceList.getReadWriteLock().readLock().lock();
				try {
					@SuppressWarnings("unchecked")
					T[] items = (T[]) new Object[n];
					int i = 0;
					int j = sourceList.size() - 1;
					while (i < n && j >= 0) {
						T t = sourceList.get(j);
						if (t != null) {
							items[i] = t;
							i++;
						}
						j--;
					}
					matcher = new SetMatcher<>(items);
				} finally {
					sourceList.getReadWriteLock().readLock().unlock();
				}
				fireChanged(matcher);
				break;
			}
			case All:
			}
			if (matcher == null) {
				// all + default;
				fireMatchAll();
			}
			regenerating = false;
			dirty = false;
		}
	}

	public enum TogglingMode {
		All, LastN, Custom;
	}

	private EventList<T> sourceList;
	private TogglingMode mode = TogglingMode.All;
	private int n = 0;
	private Predicate<T> p;
	private Map<EventList<?>, ListEventListener<?>> reliesOn = new ConcurrentHashMap<>();
	private boolean regenerating = false;
	private GenerateMatcherThread gmt;
	private boolean dirty = false;
	private boolean enabled = true;

	public GeneratingMatcherEditor(EventList<T> sourceList) {
		this.sourceList = sourceList;
		addReliesOn(sourceList);
	}

	public <X> void addReliesOn(EventList<X> clientList) {
		ListEventListener<X> listener = new ListEventListener<X>() {
			@Override
			public void listChanged(ListEvent<X> listChanges) {
				setDirty(false);
			}
		};
		clientList.addListEventListener(listener);
		reliesOn.put(clientList, listener);
	}

	public void setModeAll(boolean join) {
		this.mode = TogglingMode.All;
		setDirty(join);
	}

	public void setModeLastN(int n, boolean join) {
		if (n >= 0) {
			this.n = n;
			this.mode = TogglingMode.LastN;
		} else {
			this.mode = TogglingMode.All;
		}
		setDirty(join);
	}

	public void setModeCustom(Predicate<T> p, boolean join) {
		if (p != null) {
			this.p = p;
			this.mode = TogglingMode.Custom;
		} else {
			this.mode = TogglingMode.All;
		}
		setDirty(join);
	}

	protected void setDirty(boolean join) {
		if (dirty) {
			return;
		}
		dirty = true;

		if (enabled) {
			generateMatcher(join);
		}
	}

	private void generateMatcher(boolean join) {
		if (!regenerating) {
			regenerating = true;
			gmt = new GenerateMatcherThread();
			gmt.start();

			if (join) {
				join();
			}
		}
	}

	/**
	 * When regenerating, join until it finishes
	 */
	public void join() {
		if (gmt != null && regenerating && enabled) {
			try {
				gmt.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled && dirty) {
			generateMatcher(true);
		}
	}

	public boolean isRegenerating() {
		return regenerating;
	}

	public GenerateMatcherThread getGmt() {
		return gmt;
	}

	@Override
	public String toString() {
		return "GeneratingMatcherEditor [mode=" + mode + "]";
	}

	/**
	 * From {@link SetMatcherEditor}
	 */
	private static class SetMatcher<E> implements Matcher<E> {
		private final Set<E> matchSet;

		private SetMatcher(Set<E> matches) {
			this.matchSet = matches;
		}

		@SafeVarargs
		private SetMatcher(E... matches) {
			this.matchSet = new HashSet<>(matches.length);
			for (E e : matches) {
				if (e != null) {
					matchSet.add(e);
				}
			}
		}

		@Override
		public boolean matches(E item) {
			return this.matchSet.contains(item);
		}

		@Override
		public String toString() {
			return "SetMatcher [" + matchSet + "]";
		}
	}
}
