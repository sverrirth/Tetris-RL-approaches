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
		terminated = false;
	}

	/**
	 * Creates a copy of pf.
	 * @param pf
	 */
	public Playfield(Playfield pf) {
		super(pf);
		terminated = pf.terminated;
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
		if(col + p.width > width || col < 0) { throw new IllegalArgumentException("Piece placed outside of playfield!"); }
		int placementHeight = 0;
		for(int w = 0; w < p.width; w++) {
			placementHeight = max(placementHeight, heightOf(col + w) - p.spaceBelow(w));
		}
		if(placementHeight + p.getHeight() > height) {
			terminated = true;
			return;
		}
		for(int w = 0; w < p.width; w++) {
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

	protected void removeRow(int i) {
		int lowermask = (1 << i) - 1;
		int highermask = (1 << height) - 1 - lowermask - (1 << i);
		for(int c = 0; c < width; c++) {
			columns[c] = (columns[c] & highermask) >> 1 | columns[c] & lowermask;
		}
	}
}
