package ca.hapke.campingbot.util;

import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Nathan Hapke
 */
public class StagedJob<T extends JobDetails> extends StatusThread {
	protected AbstractQueue<T> jobs = new ConcurrentLinkedQueue<>();
	private int step;
	private int steps;
	private int attempt;
	private int attempts;

	public StagedJob(T first) {
		jobs.add(first);
	}

	@Override
	protected void doWork() {
		while (jobs.size() > 0 && !kill) {
			JobDetails job = jobs.poll();
			step = 0;
			attempt = 0;
			boolean abortJob = false;
			steps = job.getNumSteps();
			while (step < steps && !abortJob) {
				attempts = job.getNumAttempts(step);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(jobs.size() + " jobs\n");

		builder.append("1: Step " + step + "/" + steps + " Attempt " + attempt + "/" + attempts);
		int n = 1;
		for (T t : jobs) {
			if (n >= 2) {
				builder.append(n + ": Steps " + t.getNumSteps());
			}
			n++;
		}
		return builder.toString();
	}

}
