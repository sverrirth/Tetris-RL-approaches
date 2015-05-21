package tetris;

import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A specification of the Tetris problem. Capable of estimating the fitness
 * of AI parameters and simulating games.
 */
public class Tetris implements cemethod.CEProblem {
	/**
	 * height
	 */
	private final int h;
	/**
	 * width
	 */
	private final int w;
	/**
	 * Source of randomness for seed to Mersenne twister.
	 */
	private final Random r;
	private int trials;

	/**
	 * @param w width of tetris playfield.
	 * @param h height of tetris playfield.
	 * @param r source of randomness for piece generation.
	 */
	public Tetris(int w, int h, Random r, int trials) {
		this.r = r;
		this.w = w;
		this.h = h;
		this.trials = trials;
	}

	/* (non-Javadoc)
	 * @see cemethod.CEProblem#dimension()
	 */
	@Override
	public int dimension() {
		return w + 5;
	}

	/* (non-Javadoc)
	 * @see cemethod.CEProblem#fitness(double[])
	 */
	@Override
	public double fitness(double[] v) {
		double perf = 0;
		for(int i = 0; i < trials; i++) {
			perf += runTrial(v, false);
		}
		perf /= trials;
		return perf;
	}

	private static Piece getRandomPiece(RandomGenerator rng) {
		int n = rng.nextInt(Piece.PIECES.length);
		return Piece.PIECES[n];
	}

	/**
	 * Simulates a single game of tetris.
	 * @param param The AI weights to use.
	 * @param display If true, the game is displayed using Swing.
	 * @return The number of lines cleared.
	 */
	public int runTrial(double[] param, boolean display) {
		Playfield b;
		if(display) {
			b = new SwingPlayfield(w, h);
		} else {
			b = new Playfield(w, h);
		}

		// Scratch memory:
		int[] mem = new int[dimension()];
		Playfield tmp = new Playfield(w, h);
		RandomGenerator rng = new MersenneTwister(r.nextLong());

		int lines = 0;
		while(!b.isTerminal()) {
			Piece current = getRandomPiece(rng);
			OrientedPiece bestPiece = null;
			int bestCol = 0;
			double bestVal = 0;
			for(OrientedPiece op : current) {
				for(int c = 0; c + op.width <= b.width; c++) {
					tmp.setTo(b);
					tmp.place(op, c);
					double val = eval(tmp, param, mem);
					if(val > bestVal || bestPiece == null) {
						bestVal = val;
						bestPiece = op;
						bestCol = c;
					}
				}
			}
			lines += b.place(bestPiece, bestCol);
		}
		return lines;
	}

	private static double eval(Playfield b, double[] par, int[] mem) {
		if(b.isTerminal()) { return -1.0 / 0.0; }
		double ans = 0;
		b.symmetricMixedFeatures(mem);
		for(int c = 0; c < par.length; c++) {
			ans += par[c] * mem[c];
		}
		return ans;
	}
}
