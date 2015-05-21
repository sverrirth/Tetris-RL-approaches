package tetris;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.math3.random.MersenneTwister;

import cemethod.CESolve;
import cemethod.CEWorker;
import cemethod.Perf;
import cemethod.Subproblem;

/**
 * Main runs a training session for Tetris. 
 */
public final class Main {
	private Main() {
	}

	/**
	 * @param args None.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// To benchmark: give all RNGs an explicit seed
		// and change number of threads to 1.

		// Solver parameters:
		int width = 10;
		int trainingHeight = 14;
		int evaluationHeight = 20;
		int threads = 4;
		int maxGenerations = 1000;
		double minVariance = 0.3;
		double initialNoise = 7.0;
		double noiseStep = -0.1;
		int generationSize = 100;
		int elitistSize = 10;
		Tetris training = new Tetris(width, trainingHeight, new Random(), 50);
		Tetris evaluation = new Tetris(width, evaluationHeight, new Random(), 1000);

		// Solver setup.
		CESolve solver = new CESolve(threads, new MersenneTwister());
		solver.setMaxGenerations(maxGenerations);
		solver.setMinVariance(minVariance);
		solver.setSamples(generationSize);
		solver.setProblem(training);
		solver.setElitists(elitistSize);
		solver.setInitialNoise(initialNoise);
		solver.setNoiseStep(noiseStep);

		// Run solver.
		long startTime = System.nanoTime();
		double[] opt = solver.solve();
		solver.shutdown();
		System.out.println("Trained in " + (System.nanoTime() - startTime) / 1000000 / 1000.0 + " seconds.");
		writeParameters(opt);
		System.out.println("Perf on training problem: " +
			(int)new Tetris(width, trainingHeight, new Random(), 1000).fitness(opt));
		thoroughEvaluation(evaluation, opt, threads);
		evaluation.runTrial(opt, true);
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
					System.out.println("Perf: " + perf.performance);
				} catch(InterruptedException e) {
					// This code is most disgusting.
					e.printStackTrace();
				}
		}
		for(CEWorker w : workers) {
			w.interrupt();
		}
	}

	private static void writeParameters(double[] p) {
		for(double x : p) {
			System.out.print(Double.doubleToRawLongBits(x) + " ");
		}
		System.out.println();
	}
}
