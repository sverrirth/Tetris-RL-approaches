package tetris;

import java.util.Random;

public class Tetris implements cemethod.CEProblem {
	public class Result {
		public final int lines;
		public final int pieces;

		public Result(int lines, int pieces) {
			this.lines = lines;
			this.pieces = pieces;
		}
	}
	private final int h;
	private final Random r;

	private final int w;

	public Tetris(int w, int h) {
		r = new Random();
		this.w = w;
		this.h = h;
	}

	@Override
	public int dimension() {
		return 22;
	}

	@Override
	public double fitness(double[] v) {
		double perf = 0;
		for(int i = 0; i < 30; i++) {
			perf += runTrial(v, false).pieces;
		}
		perf /= 30;
		return perf;
	}

	private Piece getRandomPiece() {
		int n = r.nextInt(Piece.PIECES.length);
		return Piece.PIECES[n];
	}

	public Result runTrial(double[] param, boolean display) {
		Playfield b;
		if(display) {
			b = new SwingPlayfield(w, h);
		} else {
			b = new Playfield(w, h);
		}
		int lines = 0;
		int placed = 0;
		while(!b.isTerminal()) {
			Piece current = getRandomPiece();
			OrientedPiece bestPiece = null;
			int bestCol = 0;
			double bestVal = 0;
			for(OrientedPiece op : current) {
				for(int c = 0; c + op.width() <= b.width; c++) {
					Playfield testBoard = new Playfield(b);
					testBoard.place(op, c);
					double val = eval(testBoard, param);
					if(val > bestVal || bestPiece == null) {
						bestVal = val;
						bestPiece = op;
						bestCol = c;
					}
				}
			}
			lines += b.place(bestPiece, bestCol);
			placed++;
		}
		return new Result(lines, placed);
	}

	private static int abs(int x) {
		return x > 0 ? x : -x;
	}

	public static double eval(Playfield b, double[] par) {
		if(b.isTerminal()) { return -1.0 / 0.0; }
		int maxh = 0;
		int hi = 0;
		int lasth = 0;
		double ans = 0;
		for(int c = 0; c < b.width; c++) {
			hi = b.heightOf(c);
			if(c > 0) {
				ans += par[c + b.width] * abs(hi - lasth);
			}
			ans += par[c] * hi;
			maxh = maxh < hi ? hi : maxh;
			lasth = hi;
		}
		ans += par[b.width] * maxh;
		ans += par[2 * b.width] * b.numberOfHoles();
		return ans;
	}
}
