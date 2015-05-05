package tetris;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tetris {
	private Random r;

	public class Result {
		public final int lines;
		public final int pieces;

		public Result(int lines, int pieces) {
			this.lines = lines;
			this.pieces = pieces;
		}
	}

	public Tetris() {
		r = new Random();
	}

	public static void main(String[] args) {
		Tetris tet = new Tetris();
		tet.experiment(100, 10);
	}

	public void experiment(int sampleSizes, int kept) {
		AIParameters sample;
		Distribution d = new Distribution(21);
		for(int gen = 0; gen < 20; gen++) {
			List<AIParameters> params = new ArrayList<AIParameters>();
			for(int smpls = 0; smpls < sampleSizes; smpls++) {
				sample = d.sample(r);
				sample.estimatedPerformance = performanceByPieceCount(sample, 30);
				params.add(sample);
			}
			Collections.sort(params);
			d = new Distribution(params.subList(0, kept), 2);

			System.out.println("Done with generation " + gen + ": ");
			for(AIParameters p : params) {
				System.out.print((int)p.estimatedPerformance + " ");
			}
			System.out.println();
			System.out.println("New mean: " + d.getMean());
			System.out.println("New variance: " + d.avgVar());
			System.out.println("New performance: " + (int)performanceByLineCount(d.getMean(), 30) + "\n");
		}
		System.out.println("We converged to " + d.getMean());
	}

	private double performanceByLineCount(AIParameters sample, int cnt) {
		double perf = 0;
		for(int i = 0; i < cnt; i++) {
			perf += runTrial(sample).lines;
		}
		perf /= cnt;
		return perf;
	}

	private double performanceByPieceCount(AIParameters sample, int cnt) {
		double perf = 0;
		for(int i = 0; i < cnt; i++) {
			perf += runTrial(sample).pieces;
		}
		perf /= cnt;
		return perf;
	}

	public Result runTrial(AIParameters param) {
		Board b = new Board(10, 20);
		int lines = 0;
		int placed = 0;
		while(!b.isTerminal()) {
			Piece current = getRandomPiece();
			OrientedPiece bestPiece = null;
			int bestCol = 0;
			double bestVal = 0;
			for(OrientedPiece op : current) {
				for(int c = 0; c + op.width() <= b.width; c++) {
					Board testBoard = new Board(b);
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

	private Piece getRandomPiece() {
		int n = r.nextInt(Piece.PIECES.length);
		return Piece.PIECES[n];
	}

	private static double eval(Board b, AIParameters params) {
		if(b.isTerminal()) { return -1.0 / 0.0; }
		int maxh = 0;
		int h = 0;
		int lasth = 0;
		double ans = 0;
		for(int c = 0; c < b.width; c++) {
			h = b.heightAt(c);
			if(c > 0) {
				ans += params.par[c + b.width] * abs(h - lasth);
			}
			ans += params.par[c] * h;
			maxh = maxh < h ? h : maxh;
			lasth = h;
		}
		ans += params.par[b.width] * maxh;
		ans += params.par[2 * b.width] * b.numberOfHoles();
		return ans;
	}

	private static int abs(int x) {
		return x > 0 ? x : -x;
	}
}
