package tetris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.math3.random.MersenneTwister;

import cemethod.CESolver;
import cemethod.CEWorker;
import cemethod.Perf;
import cemethod.Subproblem;

/**
 * Main runs a training session for Tetris. 
 */
public final class Main {
	
	static final int nFeatures = 15;
	
	private Main() {
	}

	/**
	 * @param args None.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		// To benchmark: give all RNGs an explicit seed
		// and change number of threads to 1.

		// Solver parameters:
		
		System.out.println("--- LATEST VERSION ---");
		
		int width = 10;
		int trainingHeight = 14;
		int evaluationHeight = 20;
		int threads = 8;
		int maxGenerations = 1000;
		double initialNoise = 7.0;
		double noiseStep = -0.1;
		int generationSize = 100;
		int elitistSize = 10;
		
		boolean proportional = true;
		
		Tetris training = new Tetris(width, trainingHeight, new Random(), 50, nFeatures);
		Tetris evaluation = new Tetris(width, evaluationHeight, new Random(), 1000, nFeatures);

		if(args.length > 0) {
			double[] par = new double[evaluation.dimension()];
			for(int i = 0; i < par.length; i++) {
				par[i] = Double.longBitsToDouble(Long.parseLong(args[i + 1]));
			}
			if(args[0].equals("test")) {
				thoroughEvaluation(evaluation, par, threads);
				return;
			} else if(args[0].equals("show")) {
				evaluation.runTrial(par, true);
				return;
			} else {
				System.out.println("Unknown arguments.");
				return;
			}
		}

		// Solver setup.
		CESolver solver = new CESolver(threads, new MersenneTwister());
		solver.setMaxGenerations(maxGenerations);
		solver.setSamples(generationSize);
		solver.setProblem(training);
		solver.setElitists(elitistSize);
		solver.setInitialNoise(initialNoise);
		solver.setNoiseStep(noiseStep);
		solver.setProportional(proportional);

		// Run solver.
		long startTime = System.nanoTime();
		double[] opt = solver.solve();
		solver.shutdown();
		System.out.println("Trained in " + (System.nanoTime() - startTime) / 1000000 / 1000.0 + " seconds.");
		System.out.println("Perf on training problem: " +
			(int)new Tetris(width, trainingHeight, new Random(), 1000, nFeatures).fitness(opt));
		System.out.println("To test the fitness of these parameters, run \n" +
			"java -cp \"./commons-math3-3.5.jar:.\" tetris.Main test " + parametersToString(opt));
		System.out.println("To see a sample game, use \"show\" instead of \"test\"");
	}

	private static void thoroughEvaluation(Tetris evaluation, double[] opt, int threads) {
		LinkedBlockingQueue<Subproblem> q = new LinkedBlockingQueue<Subproblem>();
		LinkedBlockingQueue<Perf> qq = new LinkedBlockingQueue<Perf>();
		ArrayList<CEWorker> workers = new ArrayList<CEWorker>();
		Perf perf;
		for(int i = 0; i < threads; i++) {
			workers.add(new CEWorker(q, qq));
			workers.get(i).start();
		}
		for(int i = 0; i < 20; i++) {
			q.add(new Subproblem(evaluation, opt, i));
		}
		for(int i = 0; i < 20; i++) {
			try {
				perf = qq.take();
				System.out.println("Perf: " + (int)perf.performance);
			} catch(InterruptedException e) {
				// This code is most disgusting.
				e.printStackTrace();
			}
		}
		for(CEWorker w : workers) {
			w.interrupt();
		}
	}

	private static String parametersToString(double[] p) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < p.length; i++) {
			sb.append(Double.doubleToRawLongBits(p[i]) + "");
			if(i + 1 < p.length) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}
}