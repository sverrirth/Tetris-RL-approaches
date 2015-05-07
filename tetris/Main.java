package tetris;

import java.util.Random;

import cemethod.CESolve;

public class Main {
	public static void main(String[] args) {
		Tetris tet = new Tetris(10, 20);
		Random r = new Random();
		double[] opt = CESolve.solve(100, 10, 30, tet, r);
		tet.runTrial(opt, true);
	}
}
