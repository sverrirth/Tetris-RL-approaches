package tetris;

import java.util.Random;

import cemethod.CESolve;

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
		// To benchmark: give Random an explicit seed
		// and change number of threads to 1.
		int width = 10;
		int trainingHeight = 10;
		int evaluationHeight = 20;
		int threads = 8;
		int maxGenerations = 30;
		double minVariance = 5;
		int generationSize = 200;
		int elitistSize = 15;

		Tetris training = new Tetris(width, trainingHeight, new Random());
		Tetris evaluation = new Tetris(width, evaluationHeight, new Random());
		CESolve solver = new CESolve(new Random(), threads);
		long startTime = System.nanoTime();
		double[] opt = solver.solve(training, generationSize, elitistSize, maxGenerations, minVariance);
		solver.shutdown();
		System.out.println("Trained in " + (System.nanoTime() - startTime) / 1000000 / 1000.0 + " seconds.");
		System.out.println("Perf: " + (int)evaluation.fitness(opt));
		evaluation.runTrial(opt, true);
	}
}
