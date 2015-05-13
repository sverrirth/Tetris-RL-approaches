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
		Subproblem prob;
		Perf perf;
		while(!isInterrupted()) {
			try {
				prob = q.take();
			} catch(InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
				continue;
			}
			perf = new Perf();
			perf.index = prob.index;
			perf.performance = prob.problem.fitness(prob.parameters);
			try {
				qq.put(perf);
			} catch(InterruptedException e) {
				throw new RuntimeException("Thread interrupted while working");
			}
		}
	}
}
