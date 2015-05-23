package tetris;

/**
 * A playfield is a representation of the state of the playfield of a Tetris game.
 * It is a grid which pieces may be dropped into.
 */
public class Playfield extends Grid {
	/**
	 * True if the game is lost.
	 */
	private boolean terminated;

	/**
	 * Creates an empty width x height playfield.
	 * @param width
	 * @param height
	 */
	public Playfield(int width, int height) {
		super(width, height);
		if(height > 30) { throw new IllegalArgumentException("Playfield does not support such big playfields."); }
		terminated = false;
		if(DEBUG) {
			checkInvariants();
		}
	}

	/**
	 * Copies pf into this Playfield.
	 * @param pf
	 */
	public void setTo(Playfield pf) {
		super.setTo(pf);
		terminated = pf.terminated;
		if(DEBUG) {
			checkInvariants();
		}
	}

	/**
	 * @return True if game has ended.
	 */
	public boolean isTerminal() {
		return terminated;
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
		int r = removeFull();
		if(DEBUG) {
			checkInvariants();
		}
		return r;
	}

	/**
	 * Places p with its leftmost column at col.
	 * @param p
	 * @param col
	 */
	protected void placeWithoutClearing(OrientedPiece p, int col) {
		if(col + p.width > width || col < 0) { throw new IllegalArgumentException("Piece placed outside of playfield!"); }
		int placementHeight = 0;
		for(int w = 0; w < p.width; w++) {
			placementHeight = max(placementHeight, heightOf[col + w] - p.heightBelow[w]);
		}
		for(int w = 0; w < p.width; w++) {
			heightOf[col + w] = placementHeight + p.heightOf[w];
			columns[col + w] |= p.columns[w] << placementHeight;
		}
		if(placementHeight + p.height > height) {
			terminated = true;
		}
		nFull += p.nFull;
		if(DEBUG) {
			checkInvariants();
		}
	}

	/**
	 * Removes all full rows.
	 * @return The number of rows removed.
	 */
	protected int removeFull() {
		int ans = 0;
		int fullRows = ~0;
		for(int c : columns) {
			fullRows &= c;
		}
		int index = 0;
		while(fullRows > 0) {
			if((fullRows & 1) == 1) {
				removeRow(index);
				ans++;
			} else {
				index++;
			}
			fullRows >>= 1;
		}
		if(DEBUG) {
			checkInvariants();
		}
		return ans;
	}

	private void removeRow(int i) {
		if(DEBUG) {
			for(int c = 0; c < width; c++) {
				if(!isSquareFull(i, c)) { throw new RuntimeException("Faulty call to removeRow()."); }
			}
		}
		int lowermask = (1 << i) - 1;
		int highermask = ~((1 << i + 1) - 1);
		for(int c = 0; c < width; c++) {
			columns[c] = (columns[c] & highermask) >> 1 | columns[c] & lowermask;
			heightOf[c] = calculateHeight(c);
		}
		nFull -= width;
		if(DEBUG) {
			checkInvariants();
		}
	}

	// None of the methods below mutate the playfield.

	private static int min(int a, int b) {
		return a < b ? a : b;
	}

	private int wellsum() {
		int ans = 0;
		int m = heightOf[1] - heightOf[0];
		ans += m > 1 ? m : 0;
		for(int c = 2; c < width; c++) {
			m = min(heightOf[c - 2], heightOf[c]) - heightOf[c - 1];
			ans += m > 1 ? m : 0;
		}
		m = heightOf[width - 1] - heightOf[width - 2];
		ans += m > 1 ? m : 0;
		return ans;
	}

	private int coltrans() {
		int ans = 0;
		for(int x : columns) {
			// x & ~x << 1 is 1 exactly where a 1 changes to a 0.
			// ~x & x << 1 is 1 exactly where a 0 changes to a 1.
			ans += Integer.bitCount(x & ~x << 1 | ~x & x << 1);
		}
		return ans;
	}

	private int rowtrans() {
		int ans = 0;
		for(int c = 1; c < width; c++) {
			ans += Integer.bitCount(columns[c] ^ columns[c - 1]);
		}
		return ans;
	}

	private int holeSums() {
		int ans = 0;
		for(int c : columns) {
			int nHoles = 0;
			while(c > 0) {
				if((c & 1) == 1) {
					ans += nHoles;
				} else {
					nHoles++;
				}
				c >>= 1;
			}
		}
		return ans;
	}

	public void bertsekasFeatures(int[] target) {
		int maxh = heightOf[0];
		target[2 * width] = -nFull;
		target[0] = heightOf[0];
		for(int w = 1; w < width; w++) {
			int h = heightOf[w];
			target[w] = h;
			target[2 * width] += h;
			target[width + w] = abs(h - target[w - 1]);
			maxh = h > maxh ? h : maxh;
		}
		target[width] = maxh;
	}

	// only for even widths.
	public void symmetricBertsekasFeatures(int[] target) {
		int maxh = 0;
		int h;
		int h2;
		target[width] = -nFull;
		for(int w = 0; w < width / 2; w++) {
			h = heightOf[w];
			h2 = heightOf[width - w - 1];
			target[w] = h + h2;
			maxh = maxh > h ? maxh : h;
			maxh = maxh > h2 ? maxh : h2;
			target[width] += h + h2;
		}
		for(int w = 0; w < width / 2 - 1; w++) {
			target[width / 2 + w] =
				abs(heightOf[w + 1] - heightOf[w]) + abs(heightOf[width - w - 1] - heightOf[width - w - 2]);
		}
		target[width - 1] = abs(heightOf[width / 2] - heightOf[width / 2 - 1]);
		target[width + 1] = maxh;
	}

	public void mixedFeatures(int[] target) {
		bertsekasFeatures(target);
		target[2 * width + 1] = holeSums();
		target[2 * width + 2] = coltrans();
		target[2 * width + 3] = rowtrans();
		target[2 * width + 4] = wellsum();
	}

	// Only for even widths.
	public void symmetricMixedFeatures(int[] target) {
		symmetricBertsekasFeatures(target);
		//target[width + 2] = holeSums();
		target[width + 2] = coltrans();
		target[width + 3] = rowtrans();
		target[width + 4] = wellsum();
	}

	public void smallFeatures(int[] target) {
		target[0] = -nFull;
		target[1] = 0;
		int maxh = 0;
		for(int h : heightOf) {
			maxh = 0;
			maxh = h > maxh ? h : maxh;
			target[0] += h;
			target[1] += h;
		}
		target[2] = maxh;
		target[3] = heightOf[0] + heightOf[width - 1];
		for(int i = 0; i < width - 1; i++) {
			target[3] = abs(heightOf[i] - heightOf[i + 1]);
		}
		target[4] = holeSums();
		target[5] = coltrans();
		target[6] = rowtrans();
		target[7] = wellsum();
	}

	private static int abs(int x) {
		return x > 0 ? x : -x;
	}

	@Override
	public void checkInvariants() {
		if(isTerminal()) { return; }
		super.checkInvariants();
	}
}
