package tetris;

import java.util.Arrays;

public class OrientedPiece {
	// lsb = bottom.
	private final int[] columns;
	private final int height;

	public OrientedPiece(boolean[][] a, int rotation) {
		final int r = rotation;
		if((r & 1) == 0) {
			height = a[0].length;
			columns = new int[a.length];
		} else {
			height = a.length;
			columns = new int[a[0].length];
		}
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[0].length; j++) {
				int x = transformx(i, j, width(), r);
				int y = transformy(i, j, height, r);
				columns[x] |= a[i][j] ? 1 << y : 0;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(columns);
		result = prime * result + height;
		return result;
	}

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

	private static int transformy(int x, int y, int h, int r) {
		if(r == 0) { return y; }
		if(r == 1) { return x; }
		if(r == 2) { return h - y - 1; }
		return h - x - 1;
	}

	private static int transformx(int x, int y, int w, int r) {
		if(r == 0) { return x; }
		if(r == 1) { return w - y - 1; }
		if(r == 2) { return w - x - 1; }
		return y;
	}

	public int width() {
		return columns.length;
	}

	public int heightAt(int index) {
		// This is the height from below.
		// Example:
		// ##
		// #_
		// #_
		// has height 0 in column 1 and height 2 in column 2.
		// This is encoded as []int{7, 4}.
		int c = columns[index];
		int ans = 1;
		while(c == c >> ans << ans) {
			ans++;
		}
		ans--;
		return ans;
	}

	public int getColumn(int index) {
		return columns[index];
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(height + "x" + columns.length + " piece:\n");
		for(int i = height - 1; i >= 0; i--) {
			for(int column : columns) {
				s.append((column >> i & 1) == 1 ? "X" : " ");
			}
			s.append("\n");
		}
		return s.toString();
	}
}
