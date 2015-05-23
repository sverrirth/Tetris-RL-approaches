package cemethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * This class implements the cross-entropy method for optimization.
 */
public final class CESolver {
	private LinkedBlockingQueue<Subproblem> q;
	private LinkedBlockingQueue<Perf> qq;
	private List<CEWorker> workers;
	private GeneralNormalDistribution d;
	private int samples;
	private int elitists;
	private int maxGenerations;
	private double minVariance;
	private CEProblem problem;
	private RandomGenerator r;
	private double noiseStep;
	private double initialNoise;

	/**
	 * @param threads The number of threads to use when solving problems.
	 */
	public CESolver(int threads, RandomGenerator r) {
		q = new LinkedBlockingQueue<Subproblem>();
		qq = new LinkedBlockingQueue<Perf>();
		workers = new ArrayList<CEWorker>();
		for(int i = 0; i < threads; i++) {
			workers.add(new CEWorker(q, qq));
			workers.get(i).start();
		}
		this.r = r;
	}

	/**
	 * @return the samples
	 */
	public int getSamples() {
		return samples;
	}

	/**
	 * @param samples the samples to set
	 */
	public void setSamples(int samples) {
		this.samples = samples;
	}

	/**
	 * @return the number of elite samples per generation
	 */
	public int getElitists() {
		return elitists;
	}

	/**
	 * @return the noiseStep
	 */
	public double getNoiseStep() {
		return noiseStep;
	}

	/**
	 * @param noiseStep the noiseStep to set
	 */
	public void setNoiseStep(double noiseStep) {
		this.noiseStep = noiseStep;
	}

	/**
	 * @return the initialNoise
	 */
	public double getInitialNoise() {
		return initialNoise;
	}

	/**
	 * @param initialNoise the initialNoise to set
	 */
	public void setInitialNoise(double initialNoise) {
		this.initialNoise = initialNoise;
	}

	/**
	 * @param elitists the number of elite samples per generation
	 */
	public void setElitists(int elitists) {
		this.elitists = elitists;
	}

	/**
	 * @return the maxGenerations
	 */
	public int getMaxGenerations() {
		return maxGenerations;
	}

	/**
	 * @param maxGenerations the maxGenerations to set
	 */
	public void setMaxGenerations(int maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @return the minVariance
	 */
	public double getMinVariance() {
		return minVariance;
	}

	/**
	 * @param minVariance the minVariance to set
	 */
	public void setMinVariance(double minVariance) {
		this.minVariance = minVariance;
	}

	/**
	 * @return the problem
	 */
	public CEProblem getProblem() {
		return problem;
	}

	/**
	 * @param problem the problem to set
	 */
	public void setProblem(CEProblem problem) {
		this.problem = problem;
	}

	/**
	 * Sets the seed to n. Useful with threads = 1 for deterministic execution.
	 * @param n
	 */
	public void seed(long n) {
		r.setSeed(n);
	}

	public void shutdown() {
		for(CEWorker w : workers) {
			w.interrupt();
		}
	}

	/**
	 * @return The found maximum value.
	 * @throws InterruptedException In case it is interrupted while working.
	 */
	public double[] solve() throws InterruptedException {
		double[] best = null;
		d = new GeneralNormalDistribution(problem.dimension(), r);
		int save = elitists / 2;
		double dist = 1.0 / 0.0;
		double[] oldMean = d.getMeans();
		double[] mean;
		List<Point> params = new ArrayList<Point>();
		for(int i = 0; i < samples; i++) {
			params.add(new Point(d.sample()));
		}

		for(int gen = 1; gen <= maxGenerations && d.avgVar() > minVariance; gen++) {
			for(int i = save; i < samples; i++) {
				params.set(i, new Point(d.sample()));
			}
			for(int i = 0; i < samples; i++) {
				q.add(new Subproblem(problem, params.get(i).par, i));
			}
			for(int i = 0; i < samples; i++) {
				Perf perf = qq.take();
				params.get(perf.index).performance = perf.performance;
			}

			Collections.sort(params);
			double noise = initialNoise + noiseStep * gen;
			List<Point> elites = params.subList(0, elitists);
			System.out.println("Standard deviation of scores: " + scoreDeviation(elites));
			d.fitTo(elites, noise > 0 ? noise : 0);
			mean = d.getMeans();
			dist = l2(mean, oldMean);
			oldMean = mean;
			print(gen, params, dist);
			best = params.get(0).par;
		}
		return best;
	}

	private static double scoreDeviation(List<Point> l) {
		double mean = 0;
		for(Point p : l) {
			mean += p.performance;
		}
		mean /= l.size();
		double var = 0;
		for(Point p : l) {
			var += (p.performance - mean) * (p.performance - mean);
		}
		var /= l.size();
		return Math.sqrt(var);
	}

	private void print(int gen, List<Point> params, double dist) {
		System.out.println("Done with generation " + gen + ": ");
		for(Point point : params) {
			System.out.print((int)point.performance + " ");
		}
		System.out.println();
		System.out.println("Mean moved: " + dist);
		System.out.println("New mean: " + Arrays.toString(d.getMeans()));
		System.out.println("New variance: " + d.avgVar());
		System.out.println();
	}

	private static double l2(double[] a, double[] b) {
		double ans = 0;
		for(int i = 0; i < a.length; i++) {
			ans += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(ans / a.length);
	}
}
