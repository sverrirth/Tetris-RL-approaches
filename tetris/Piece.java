package tetris;

import java.util.ArrayList;
import java.util.Iterator;

public class Piece implements Iterable<OrientedPiece> {
	private final boolean[][] columns;
	private final ArrayList<OrientedPiece> orientations;

	public static final Piece I = new Piece(new boolean[][]{{true, true, true, true}});
	public static final Piece O = new Piece(new boolean[][]{{true, true}, {true, true}});
	public static final Piece T = new Piece(new boolean[][]{{true, true, true}, {false, true, false}});
	public static final Piece S = new Piece(new boolean[][]{{false, true, true}, {true, true, false}});
	public static final Piece Z = new Piece(new boolean[][]{{true, true, false}, {false, true, true}});
	public static final Piece J = new Piece(new boolean[][]{{true, false, false}, {true, true, true}});
	public static final Piece L = new Piece(new boolean[][]{{true, true, true}, {true, false, false}});
	public static final Piece[] PIECES = new Piece[]{I, O, T, S, Z, J, L};

	public Piece(boolean[][] c) {
		// TODO: cache orientated versions of every piece.
		columns = new boolean[c.length][c[0].length];
		for(int i = 0; i < c.length; i++) {
			for(int j = 0; j < c[i].length; j++) {
				columns[i][j] = c[i][j];
			}
		}
		orientations = new ArrayList<OrientedPiece>();
		orientations.add(new OrientedPiece(columns, 0));
		int i = 1;
		for(OrientedPiece next = new OrientedPiece(columns, i); !next.equals(orientations.get(0));) {
			orientations.add(next);
			i++;
			if(i == 4) {
				break;
			}
			next = new OrientedPiece(columns, i);
		}
	}

	public boolean[][] getArr() {
		return columns;
	}

	@Override
	public Iterator<OrientedPiece> iterator() {
		return orientations.iterator();
	}
}
