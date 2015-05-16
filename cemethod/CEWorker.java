package cemethod;

import java.util.concurrent.LinkedBlockingQueue;

class CEWorker extends Thread {
	LinkedBlockingQueue<Subproblem> q;
	LinkedBlockingQueue<Perf> qq;

	CEWorker(LinkedBlockingQueue<Subproblem> input, LinkedBlockingQueue<Perf> output) {
		q = input;
		qq = output;
	}

	@Override
	public void run() {
		while(!isInterrupted()) {
			doone();
		}
	}

	public void doone() {
		Subproblem prob;
		Perf perf;
		try {
			prob = q.take();
		} catch(InterruptedException e) {
			interrupt();
			return;
		}
		perf = new Perf();
		perf.index = prob.index;
		perf.performance = prob.problem.fitness(prob.parameters);
		try {
			qq.put(perf);
		} catch(InterruptedException e) {
			interrupt();
			throw new RuntimeException("Thread interrupted while working.");
		}
	}
}
