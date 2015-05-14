package cemethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class implements the cross-entropy method for optimization.
 */
public final class CESolve {
	private LinkedBlockingQueue<Subproblem> q;
	private LinkedBlockingQueue<Perf> qq;
	private List<CEWorker> workers;
	private Random r;

	public CESolve(Random random, int threads) {
		q = new LinkedBlockingQueue<Subproblem>();
		qq = new LinkedBlockingQueue<Perf>();
		workers = new ArrayList<CEWorker>();
		for(int i = 0; i < threads; i++) {
			workers.add(new CEWorker(q, qq));
			workers.get(i).start();
		}
		r = random;
	}

	public void shutdown() {
		for(CEWorker w : workers) {
			w.interrupt();
		}
	}

	/**
	 * @param sampleSizes The size of each generation.
	 * @param kept The number of samples kept from each generation.
	 * @param generations The total number of iterations.
	 * @param prob The problem to solve.
	 * @param r The source of randomness to use.
	 * @return The final value.
	 * @throws InterruptedException In case it is interrupted while working.
	 */
	public double[] solve(CEProblem p, int sampleSizes, int kept, int generations) throws InterruptedException {
		Point sample;
		Distribution d = new NormalDistribution(p.dimension());
		List<Point> params = new ArrayList<Point>();

		for(int gen = 1; gen <= generations; gen++) {
			params.clear();
			for(int smpls = 0; smpls < sampleSizes; smpls++) {
				sample = new Point(d.sample(r));
				q.add(new Subproblem(p, sample.par, smpls));
				params.add(sample);
			}
			for(int smpls = 0; smpls < sampleSizes; smpls++) {
				Perf perf = qq.take();
				params.get(perf.index).performance = perf.performance;
			}
			Collections.sort(params);
			d = new NormalDistribution(params.subList(0, kept), 5.0 - 0.1*gen);

			System.out.println("Done with generation " + gen + ": ");
			for(Point point : params) {
				System.out.print((int)point.performance + " ");
			}
			System.out.println();
			System.out.println("New mean: " + Arrays.toString(d.getMean()));
			System.out.println("New variance: " + d.avgVar());
			System.out.println("New performance: " + (int)p.fitness(d.getMean()) + "\n");
		}
		return d.getMean();
	}
}
