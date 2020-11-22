package ca.hapke.campingbot.util;

import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Nathan Hapke
 */
public abstract class StagedJob<T extends JobDetails> extends StatusThread {
	protected AbstractQueue<T> jobs = new ConcurrentLinkedQueue<>();

	public StagedJob(T first) {
		jobs.add(first);
	}

	@Override
	protected void doWork() {
		while (jobs.size() > 0 && !kill) {
			JobDetails job = jobs.poll();
			int step = 0;
			int attempt = 0;
			boolean abortJob = false;
			while (step < job.getNumSteps() && !abortJob) {
				int attempts = job.getNumAttempts(step);
				boolean success = job.doStep(step, attempt);
				if (job.shouldAbort())
					break;

				try {
					Thread.sleep(job.getDelay(step));
				} catch (InterruptedException e) {
				}

				if (success) {
					step++;
					attempt = 0;
				} else {
					attempt++;
					if (attempts > 0 && attempt >= attempts) {
						if (job.isRequireCompletion(step)) {
							abortJob = true;
						} else {
							step++;
							attempt = 0;
						}
					}
				}
			}
		}
	}

	public boolean add(T e) {
		return jobs.add(e);
	}

}
