package tetris;

import java.util.Random;

import cemethod.CESolve;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		// To benchmark: give Random an explicit seed
		// and change number of threads to 1.
		long startTime = System.nanoTime();
		Tetris tet = new Tetris(10, 20, new Random());
		CESolve solver = new CESolve(new Random(), 8);
		double[] opt = solver.solve(tet, 100, 10, 10);
		solver.shutdown();
		System.out.println("Trained in " + (System.nanoTime() - startTime) / 1000000 / 1000.0 + " seconds.");
		tet.runTrial(opt, true);
	}
}
