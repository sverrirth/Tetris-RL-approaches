package tetris;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tetris piece specification.
 */
public class Piece implements Iterable<OrientedPiece> {
	public static final Piece I = new Piece(new boolean[][]{{true, true, true, true}});
	public static final Piece J = new Piece(new boolean[][]{{true, false, false}, {true, true, true}});
	public static final Piece L = new Piece(new boolean[][]{{true, true, true}, {true, false, false}});
	public static final Piece O = new Piece(new boolean[][]{{true, true}, {true, true}});
	public static final Piece S = new Piece(new boolean[][]{{false, true, true}, {true, true, false}});
	public static final Piece T = new Piece(new boolean[][]{{true, true, true}, {false, true, false}});
	public static final Piece Z = new Piece(new boolean[][]{{true, true, false}, {false, true, true}});
	public static final Piece DIAGONAL = new Piece(new boolean[][]{{true, false}, {false, true}});
	public static final Piece POINT = new Piece(new boolean[][]{{true}});
	public static final Piece TRIANGLE = new Piece(new boolean[][]{{true, true}, {true, false}});
	public static final Piece STRAIGHT = new Piece(new boolean[][]{{true, true}});
	/**
	 * The standard set of pieces for tetris.
	 */
	public static final Piece[] PIECES = new Piece[]{I, O, T, S, Z, J, L};
	public static final Piece[] SMALLPIECES = new Piece[]{O, DIAGONAL, POINT, TRIANGLE, STRAIGHT};

	/**
	 * A list of all the possible oriented versions of the piece.
	 */
	private final List<OrientedPiece> orientations;

	/**
	 * Construct a new piece which is filled at coordinates where c is true.
	 * @param c
	 */
	public Piece(boolean[][] c) {
		orientations = new ArrayList<OrientedPiece>();
		orientations.add(new OrientedPiece(c, 0));
		int i = 1;
		for(OrientedPiece next = new OrientedPiece(c, i); !next.equals(orientations.get(0));) {
			orientations.add(next);
			i++;
			next = new OrientedPiece(c, i);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<OrientedPiece> iterator() {
		return orientations.iterator();
	}
}
