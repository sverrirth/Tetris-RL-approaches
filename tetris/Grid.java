package tetris;

import java.util.Arrays;

public class Grid {
	/**
	 * Representation of the columns of the playfield.
	 * Each entry is an integer, where bit i is set
	 * whenever the i:th square of the column, counted
	 * from the bottom, is full.  
	 */
	protected final int[] columns;
	/**
	 * The height of the Playfield.
	 */
	public final int height;
	/**
	 * The width of the Playfield.
	 */
	public final int width;

	public Grid(int height, int width) {
		if(height > 31) { throw new IllegalArgumentException("Height too large for Grid."); }
		this.width = width;
		this.height = height;
		columns = new int[width];
	}

	/**
	 * @param g The grid to copy.
	 */
	public Grid(Grid g) {
		height = g.height;
		width = g.width;
		columns = Arrays.copyOf(g.columns, width);
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
	 * The height of the empty space under the grid in
	 * column index.
	 * Example:
	 * ##_
	 * #_#
	 * #__
	 *  has heights 0, 2, and 1 respectively.
	 * @param column
	 * @return The height below column number column.
	 */
	public int spaceBelow(int column) {
		int c = columns[column];
		int ans = 1;
		while(c == c >> ans << ans) {
			ans++;
		}
		ans--;
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

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(height + "x" + width + " grid:\n");
		for(int i = height - 1; i >= 0; i--) {
			for(int column : columns) {
				s.append((column >> i & 1) == 1 ? 'X' : ' ');
			}
			s.append('\n');
		}
		return s.toString();
	}

	protected static int max(int a, int b) {
		return a > b ? a : b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(columns);
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) { return true; }
		if(obj == null) { return false; }
		if(getClass() != obj.getClass()) { return false; }
		Grid other = (Grid)obj;
		if(!Arrays.equals(columns, other.columns)) { return false; }
		if(height != other.height) { return false; }
		if(width != other.width) { return false; }
		return true;
	}
}
