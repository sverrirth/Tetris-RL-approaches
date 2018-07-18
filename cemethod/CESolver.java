package cemethod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.math3.random.RandomGenerator;

import tetris.CompareMethods;
import tetris.Tetris;

/**
 * This class implements the cross-entropy method for optimization.
 */
public final class CESolver {
	
	public static final boolean VERBOSE = false;
	
	private LinkedBlockingQueue<Subproblem> q;
	private LinkedBlockingQueue<Perf> qq;
	private List<CEWorker> workers;
	
	private GeneralNormalDistribution d;
	
	private int nSamples;
	private int nElitists;
	private int maxGenerations;
	private boolean proportional;
	private boolean semiprop;
	
	public final static String proportionalFile = "comparisonResults/PROPORTIONAL/";
	public final static String semiPropFile = "comparisonResults/SEMIPROP/";
	public final static String elitismFile = "comparisonResults/ELITISM/";
	
	private CEProblemTemplate problem;
	private RandomGenerator r;
	
	private double noiseStep;
	private double initialNoise;
	
	public int[] bestSamples;

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
		return nSamples;
	}

	/**
	 * @param samples the samples to set
	 */
	public void setSamples(int samples) {
		this.nSamples = samples;
	}

	/**
	 * @return the number of elite samples per generation
	 */
	public int getElitists() {
		return nElitists;
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
		this.nElitists = elitists;
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
	 * @return the problem
	 */
	public CEProblemTemplate getProblem() {
		return problem;
	}

	/**
	 * @param problem the problem to set
	 */
	public void setProblem(CEProblemTemplate problem) {
		this.problem = problem;
	}
	
	public void setProportional(boolean proportional) {
		this.proportional = proportional;
	}
	
	public void setSemiProp(boolean semiprop) {
		this.semiprop = semiprop;
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
	public double[] solve() throws InterruptedException, IOException {
		double[] best = null;
		d = new GeneralNormalDistribution(problem.dimension(), r);
		int save = nElitists / 2;
		double dist = 1.0 / 0.0;
		double[] oldMean = d.getMeans();
		double[] mean;
		List<Point> params = new ArrayList<Point>();
		for(int i = 0; i < nSamples; i++) {
			params.add(new Point(d.sample()));
		}
		
		String fileName = "";
		
		if(proportional) {
			
			fileName = proportionalFile;
			
		} else if (semiprop) {
			
			fileName = semiPropFile;
			
		} else {
			
			fileName = elitismFile;
			
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		
		fileName += nSamples + "S_" + nElitists + "E_" + dateFormat.format(new Date());
				
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		writer.newLine();
		writer.append("nSamples " + nSamples + ", nElitists " + nElitists + 
				", initialNoise " + initialNoise + ", noiseStep " + noiseStep + 
				", height " + CompareMethods.height + ", nFeatures = " + problem.dimension() + 
				", nTrials " + problem.getNTrials() +
				": " );
		writer.newLine();
		writer.flush();

		for(int gen = 1; gen <= maxGenerations; gen++) {
			
			System.out.println("Generation " + gen + " processing...");
			
			for(int i = save; i < nSamples; i++) {
				params.set(i, new Point(d.sample()));
			}
			for(int i = 0; i < nSamples; i++) {
				q.add(new Subproblem(problem, params.get(i).par, i));
			}
			for(int i = 0; i < nSamples; i++) {
				Perf perf = qq.take();
				params.get(perf.index).performance = perf.performance;
			}

			Collections.sort(params);
			double noise = initialNoise + noiseStep * gen;
			
			List<Point> elites = params.subList(0, nElitists);
			System.out.println("Standard deviation of scores: " + scoreDeviation(elites));
			d.fitTo(elites, noise > 0 ? noise : 0, proportional);
			mean = d.getMeans();
			dist = l2(mean, oldMean);
			oldMean = mean;
			print(gen, params, dist);
			best = params.get(0).par;
			
			writer.append(gen + ". Score " + Double.toString(params.get(0).performance) + "; " + params.get(0).toString().replace("[", "{").replace("]", "}") + ". ");
			writer.newLine();
			writer.flush();
			
			System.out.println("Best parameters found: " + (params.get(0).toString()).replace("[", "{").replace("]", "}"));
			System.out.println("----------------------------------------");
		
			if(gen % 200 == 0) {
				
				Tetris tetris = new Tetris(10, 20, new Random(), 100, params.get(0).par.length);
				
				tetris.setFeatureSubset(problem.getFeatureSubset());
				
				ArrayList<Integer> results = new ArrayList<Integer>();
				
				for(int i=0; i<100; i++) {
				
					results.add(tetris.runTrial(params.get(0).par, false));
				
				}
				
				writer.append("Average performance now of best params: " + results.stream().mapToInt(val -> val).average().orElse(0.0));
				writer.newLine();
				writer.flush();
				
			}
			
		}
		
		writer.close();
		
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
		if(VERBOSE) {
			for(Point point : params) {
				System.out.print((int)point.performance + " ");
			}
			System.out.println();
			System.out.println("Mean moved: " + dist);
			System.out.println("New mean: " + Arrays.toString(d.getMeans()));
			System.out.println("New variance: " + d.avgVar());
			System.out.println();
		
		}
	}

	private static double l2(double[] a, double[] b) {
		double ans = 0;
		for(int i = 0; i < a.length; i++) {
			ans += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(ans / a.length);
	}
	
	
}
