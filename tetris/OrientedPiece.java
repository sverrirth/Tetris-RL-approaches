package tetris;

import java.util.Arrays;

/**
 * A piece for which an orientation has been chosen.
 */
public class OrientedPiece {
	/**
	 * The columns of the piece, with the LSB of the column denoting the bottom
	 * of the grid.
	 */
	private final int[] columns;
	/**
	 * The height of the grid.
	 */
	private final int height;

	/**
	 * @param a The piece.
	 * @param rotation The desired orientation of the piece.
	 */
	public OrientedPiece(boolean[][] a, int rotation) {
		final int r = rotation % 4;
		if((r & 1) == 0) {
			height = a[0].length;
			columns = new int[a.length];
		} else {
			height = a.length;
			columns = new int[a[0].length];
		}
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[0].length; j++) {
				int x = transformx(i, j, columns.length, r);
				int y = transformy(i, j, height, r);
				columns[x] |= a[i][j] ? 1 << y : 0;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) { return true; }
		if(obj == null) { return false; }
		if(getClass() != obj.getClass()) { return false; }
		OrientedPiece other = (OrientedPiece)obj;
		if(height != other.height) { return false; }
		if(!Arrays.equals(columns, other.columns)) { return false; }
		return true;
	}

	/**
	 * @param index
	 * @return The column at index.
	 */
	public int getColumn(int index) {
		return columns[index];
	}

	/**
	 * @return The height of this piece.
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int prime = 31;
		int result = prime + Arrays.hashCode(columns);
		result = prime * result + height;
		return result;
	}

	/**
	 * The height of the empty space under the piece in
	 * column index.
	 * Example:
	 * ##
	 * #_
	 * #_
	 *  has height 0 in column 0 and height 2 in column 1.
	 * @param index
	 * @return The height of column index.
	 */
	public int heightAt(int index) {
		int c = columns[index];
		int ans = 1;
		while(c == c >> ans << ans) {
			ans++;
		}
		ans--;
		return ans;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(height + "x" + columns.length + " piece:\n");
		for(int i = height - 1; i >= 0; i--) {
			for(int column : columns) {
				s.append((column >> i & 1) == 1 ? 'X' : ' ');
			}
			s.append('\n');
		}
		return s.toString();
	}

	/**
	 * @return The width of this piece.
	 */
	public int width() {
		return columns.length;
	}

	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param r
	 * @return The resulting x coordinate when rotating the coordinate
	 * (x, y) r times counter-clockwise, assuming the width of the grid is w.
	 */
	private static int transformx(int x, int y, int w, int r) {
		if(r == 0) { return x; }
		if(r == 1) { return w - y - 1; }
		if(r == 2) { return w - x - 1; }
		return y;
	}

	/**
	 * @param x
	 * @param y
	 * @param h
	 * @param r
	 * @return The resulting y coordinate when rotation the coordinate
	 * (x, y) r times counter-clockwise, assuming the height of the grid is h.
	 */
	private static int transformy(int x, int y, int h, int r) {
		if(r == 0) { return y; }
		if(r == 1) { return x; }
		if(r == 2) { return h - y - 1; }
		return h - x - 1;
	}
}
