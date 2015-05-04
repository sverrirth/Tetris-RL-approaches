package tetris;

public final class State {
	
	private final boolean[][] well;
	private final int rowsRemoved;
	
	public State(boolean[][] w, int r) {
		well = w;
		rowsRemoved = r;
	}
	
	public boolean[][] getWell() {
		return well;
	}
	
	public int getRowsRemoved() {
		return rowsRemoved;
	}
	
	public String toString() { //just for debugging
		String ret = "";
		for(int i=1;i>-1;i--) {
			for(int j=0;j<5;j++) {
				if (well[j][i]) {
					ret += "X ";
				} else ret += "_ ";
			}
			if (well[5][i]) {
				ret += "X " + "\n";
			} else ret += "_ " + "\n";
		}
		return ret;
	}

}
