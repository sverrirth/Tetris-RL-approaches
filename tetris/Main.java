package tetris;

import java.util.Random;

import cemethod.CESolve;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		Tetris tet = new Tetris(10, 20);
		Random r = new Random();
		CESolve solver = new CESolve(r);
		double[] opt;
		opt = solver.solve(tet, 100, 10, 30);
		solver.shutdown();
		tet.runTrial(opt, true);
	}
}
