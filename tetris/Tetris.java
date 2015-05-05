package tetris;

import java.util.Random;

public class Tetris {
	private Random r;

	public Tetris() {
		r = new Random();
	}

	public static void main(String[] args) throws InterruptedException {
		Tetris tet = new Tetris();
		Board b = new Board(10, 20);
		System.out.println(b);
		for(int i = 0; i < 30; i++) {
			Thread.sleep(1000);
			Piece current = tet.getRandomPiece();
			OrientedPiece bestPiece = null;
			int bestCol = 0;
			double bestVal = -1.0 / 0.0;
			for(OrientedPiece op : current) {
				for(int c = 0; c + op.width() <= b.width; c++) {
					Board testBoard = new Board(b);
					testBoard.place(op, c);
					double val = eval(testBoard);
					if(val > bestVal) {
						bestVal = val;
						bestPiece = op;
						bestCol = c;
					}
				}
			}
			b.place(bestPiece, bestCol);
			if(b.isTerminal()) {
				System.out.println("We died.");
				b = new Board(10, 20);
			}
			System.out.println(b);
		}
	}

	private Piece getRandomPiece() {
		int n = r.nextInt(Piece.PIECES.length);
		return Piece.PIECES[n];
	}

	private static double eval(Board b) {
		if(b.isTerminal()) { return -1000000; }
		int maxh = 0;
		int h;
		for(int c = 0; c < b.width; c++) {
			h = b.heightAt(c);
			maxh = maxh < h ? h : maxh;
		}
		return -b.numberOfHoles() - maxh * maxh / 3;
	}
}
