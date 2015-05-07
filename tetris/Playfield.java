package tetris;

import java.util.Arrays;

/**
 * A playfield is a representation of the state of the playfield of a Tetris game.
 * It is a rectangular grid, which pieces may be dropped into.
 */
public class Playfield {
	/**
	 * Representation of the columns of the playfield.
	 * Each entry is an integer, where bit i is set
	 * whenever the i:th square of the column, counted
	 * from the bottom, is full.  
	 */
	private final int[] columns;
	/**
	 * The height of the Playfield.
	 */
	public final int height;
	/**
	 * True if the game is lost.
	 */
	private boolean terminated;
	/**
	 * The width of the Playfield.
	 */
	public final int width;

	/**
	 * Creates an empty width x height playfield.
	 * @param width
	 * @param height
	 */
	public Playfield(int width, int height) {
		if(height > 31) { throw new IllegalArgumentException("Height too large for Board."); }
		this.width = width;
		this.height = height;
		columns = new int[width];
		terminated = false;
	}

	/**
	 * Creates a copy of pf.
	 * @param pf
	 */
	public Playfield(Playfield pf) {
		width = pf.width;
		height = pf.height;
		columns = Arrays.copyOf(pf.columns, pf.columns.length);
		terminated = pf.terminated;
	}

	/**
	 * @param column
	 * @return The height of column number column.
	 */
	public int heightOf(int column) {
		int c = columns[column];
		int ans = 0;
		while(c > 0) {
			ans += 1;
			c >>= 1;
		}
		return ans;
	}

	/**
	 * @param row
	 * @param col
	 * @return True if (row, col) is full.
	 */
	protected boolean isSquareFull(int row, int col) {
		return (columns[col] & 1 << row) != 0;
	}

	/**
	 * @return True if game has ended.
	 */
	public boolean isTerminal() {
		return terminated;
	}

	/**
	 * @return The number of empty squares with a full square somewhere above it.
	 */
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

	/**
	 * Places p into the playfield, removing rows as needed.
	 * @param p The piece to be dropped.
	 * @param col The leftmost column of where the piece is to be dropped.
	 * @return The number of rows cleared.
	 */
	public int place(OrientedPiece p, int col) {
		placeWithoutClearing(p, col);
		if(terminated) { return 0; }
		return removeFull();
	}

	/**
	 * Places p with its leftmost column at col.
	 * @param p
	 * @param col
	 */
	protected void placeWithoutClearing(OrientedPiece p, int col) {
		if(col + p.width() > width || col < 0) { throw new IllegalArgumentException(
			"Piece placed outside of playfield!"); }
		int placementHeight = 0;
		for(int w = 0; w < p.width(); w++) {
			placementHeight = max(placementHeight, heightOf(col + w) - p.heightAt(w));
		}
		if(placementHeight + p.getHeight() > height) {
			terminated = true;
			return;
		}
		for(int w = 0; w < p.width(); w++) {
			int added = p.getColumn(w) << placementHeight;
			columns[col + w] |= added;
		}
	}

	/**
	 * Removes all ful rows.
	 * @return The number of rows removed.
	 */
	protected int removeFull() {
		int ans = 0;
		for(int i = height - 1; i >= 0; i--) {
			int mask = 1 << i;
			boolean rowFull = true;
			for(int col : columns) {
				if((col & mask) != mask) {
					rowFull = false;
					break;
				}
			}
			if(rowFull) {
				removeRow(i);
				ans++;
			}
		}
		return ans;
	}

	private void removeRow(int i) {
		int lowermask = (1 << i) - 1;
		int highermask = (1 << height) - 1 - lowermask - (1 << i);
		for(int c = 0; c < width; c++) {
			columns[c] = (columns[c] & highermask) >> 1 | columns[c] & lowermask;
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(height + "x" + width + " playfield:\n");
		for(int i = height - 1; i >= 0; i--) {
			for(int column : columns) {
				s.append((column >> i & 1) == 1 ? 'X' : ' ');
			}
			s.append('\n');
		}
		return s.toString();
	}

	static private int max(int a, int b) {
		return a > b ? a : b;
	}
}
