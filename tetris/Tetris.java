package tetris;

import java.util.Random;

public class Tetris implements cemethod.CEProblem {
	private final int h;
	private final int w;
	private final Random r;

	public Tetris(int w, int h, Random r) {
		this.r = r;
		this.w = w;
		this.h = h;
	}

	@Override
	public int dimension() {
		return w + 5;
	}

	@Override
	public double fitness(double[] v) {
		double perf = 0;
		for(int i = 0; i < 30; i++) {
			perf += runTrial(v, false);
		}
		perf /= 30;
		return perf;
	}

	private Piece getRandomPiece() {
		int n = r.nextInt(Piece.PIECES.length);
		return Piece.PIECES[n];
	}

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

		int lines = 0;
		while(!b.isTerminal()) {
			Piece current = getRandomPiece();
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

	public static double eval(Playfield b, double[] par, int[] mem) {
		if(b.isTerminal()) { return -1.0 / 0.0; }
		double ans = 0;
		b.symmetricMixedFeatures(mem);
		for(int c = 0; c < par.length; c++) {
			ans += par[c] * mem[c];
		}
		return ans;
	}
}
