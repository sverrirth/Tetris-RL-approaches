package tetris;

import java.util.Arrays;

public class Board {
	public final int width;
	public final int height;
	private final int[] columns;
	private boolean isTerminal;

	public Board(int width, int height) {
		if(height > 31) { throw new IllegalArgumentException("Height too large for Board."); }
		this.width = width;
		this.height = height;
		columns = new int[width];
		isTerminal = false;
	}

	public Board(Board b) {
		width = b.width;
		height = b.height;
		columns = Arrays.copyOf(b.columns, b.columns.length);
		isTerminal = b.isTerminal;
	}

	static private int max(int a, int b) {
		return a > b ? a : b;
	}

	public int heightAt(int index) {
		int c = columns[index];
		int ans = 0;
		while(c > 0) {
			ans += 1;
			c >>= 1;
		}
		return ans;
	}

	public void place(OrientedPiece p, int col) {
		if(col + p.width() > width || col < 0) { throw new IllegalArgumentException("Piece placed outside of board!"); }
		int placementHeight = 0;
		for(int w = 0; w < p.width(); w++) {
			placementHeight = max(placementHeight, heightAt(col + w) - p.heightAt(w));
		}
		if(placementHeight + p.getHeight() > height) {
			isTerminal = true;
			return;
		}
		for(int w = 0; w < p.width(); w++) {
			int added = p.getColumn(w) << placementHeight;
			columns[col + w] |= added;
		}
		removeFull();
	}

	public void removeFull() {
		int mask;
		boolean rowFull;
		for(int i = 0; i < height; i++) {
			mask = 1 << i;
			rowFull = true;
			for(int col : columns) {
				if((col & mask) != mask) {
					rowFull = false;
					break;
				}
			}
			if(rowFull) {
				removeRow(i);
			}
		}
	}

	private void removeRow(int i) {
		int lowermask = (1 << i) - 1;
		int highermask = (1 << height) - 1 - lowermask - (1 << i);
		for(int c = 0; c < width; c++) {
			columns[c] = (columns[c] & highermask) >> 1 | columns[c] & lowermask;
		}
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public int numberOfHoles() {
		int ans = 0;
		for(int c : columns) {
			while(c > 0) {
				if((c & 1) == 0) {
					ans++;
				}
				c >>= 1;
			}
		}
		return ans;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(height + "x" + width + " board:\n");
		for(int i = height - 1; i >= 0; i--) {
			for(int column : columns) {
				s.append((column >> i & 1) == 1 ? "X" : ".");
			}
			s.append("\n");
		}
		return s.toString();
	}
}
