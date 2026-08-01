package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public abstract class Stage implements IStage {
	protected final List<StageListener> listeners = new ArrayList<>();

	@Override
	public boolean add(StageListener e) {
		return listeners.add(e);
	}

	@Override
	public boolean remove(StageListener e) {
		return listeners.remove(e);
	}

	public final void complete(boolean success) {
		complete2(success);
		for (StageListener sl : listeners) {
			sl.stageComplete(success);
		}
	}

	protected abstract void complete2(boolean success);

	@Override
	public final void begin() {
		begin2();
		for (StageListener sl : listeners) {
			sl.stageBegan();
		}
	}

	protected abstract void begin2();
}
