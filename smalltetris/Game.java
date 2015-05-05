package smalltetris;

import java.util.Arrays;

public class Game {

	private static final int width = 6;
	private int rowsHeight = 0;
	private static double[] stateValues = new double[1 << width * 2];

	private boolean[][] well = new boolean[width][2];

	private static final double gamma = 0.80;
	private static final double alpha = 0.02;
	private static final double reward = -100;

	private static final int totalGames = 100;

	private static final boolean debug = false;

	public Game(boolean[][] w) {
		well = w;
	}

	public Game() {
	}

	enum Block {
		SQUARE, TRIANGLE, DIAGONAL, STRAIGHT, SINGLE;

		private int rotation = 0; //for Triangles, Diagonals and Straights 

		public void rotate(int r) {
			rotation = r;
		}

		public int getRotation() {
			return rotation;
		}

	}

	public void updateWell(boolean[][] w) {
		well = w;
	}

	public void updateRows(int moreRows) {
		rowsHeight += moreRows;
	}

	public boolean[][] getWell() {
		return well;
	}

	public State addBlock(Block b, int loc) {
		boolean[][] newWell = new boolean[width][2];
		switch(b) {
		case SINGLE:
			if(well[loc][1]) {
				newWell = removeBottomRow(well);
				newWell[loc][1] = true;
				return new State(newWell, 1);
			} else if(well[loc][0]) {
				newWell = copyWell(well);
				newWell[loc][1] = true;
				return new State(clean(newWell), 0);
			} else {
				newWell = copyWell(well);
				newWell[loc][0] = true;
				return new State(clean(newWell), 0);
			}
		case STRAIGHT:
			if(b.getRotation() == 0) {   //Vertical
				if(well[loc][1]) {
					newWell[loc][0] = true;
					newWell[loc][1] = true;
					return new State(newWell, 2);
				} else if(well[loc][0]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc][1] = true;
					wellTest[loc][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][0] = true;
						newWell[loc][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc][1] = true;
					return new State(clean(newWell), 0);
				}
			} else { //Horizontal, here loc should be between 0 and 4
				if(well[loc][1] || well[loc + 1][1]) {
					newWell = removeBottomRow(well);
					newWell[loc][1] = true;
					newWell[loc + 1][1] = true;
					return new State(newWell, 1);
				} else if(well[loc][0] || well[loc + 1][0]) {
					newWell = copyWell(well);
					newWell[loc][1] = true;
					newWell[loc + 1][1] = true;
					return new State(clean(newWell), 0);
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					return new State(clean(newWell), 0);
				}
			}
		case DIAGONAL: //loc should be between 0 and 4
			if(b.getRotation() == 0) { //Southwest and northeast
				if(well[loc][1]) {
					newWell[loc][0] = true;
					newWell[loc + 1][1] = true;
					return new State(newWell, 2);
				} else if(well[loc][0] || well[loc + 1][1]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc + 1][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][0] = true;
						newWell[loc + 1][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc + 1][1] = true;
					return new State(clean(newWell), 0);
				}
			} else { //Northwest and southeast
				if(well[loc + 1][1]) {
					newWell[loc + 1][0] = true;
					newWell[loc][1] = true;
					return new State(newWell, 2);
				} else if(well[loc + 1][0] || well[loc][1]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc + 1][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][1] = true;
						newWell[loc + 1][0] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][1] = true;
					newWell[loc + 1][0] = true;
					return new State(clean(newWell), 0);
				}
			}
		case TRIANGLE: //loc should be between 0 and 4
			if(b.getRotation() == 0) { //SOUTHEAST
				if(well[loc][1] || well[loc + 1][1]) {
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					newWell[loc + 1][1] = true;
					return new State(newWell, 2);
				} else if(well[loc][0] || well[loc + 1][0]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc][1] = true;
					wellTest[loc + 1][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc + 1][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][0] = true;
						newWell[loc + 1][0] = true;
						newWell[loc + 1][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					newWell[loc + 1][1] = true;
					return new State(clean(newWell), 1);
				}
			} else if(b.getRotation() == 1) { //SOUTHWEST
				if(well[loc][1] || well[loc + 1][1]) {
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					newWell[loc][1] = true;
					return new State(newWell, 2);
				} else if(well[loc][0] || well[loc + 1][0]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc][1] = true;
					wellTest[loc + 1][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][0] = true;
						newWell[loc + 1][0] = true;
						newWell[loc][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					newWell[loc][1] = true;
					return new State(clean(newWell), 1);
				}
			} else if(b.getRotation() == 2) { //NORTHWEST
				if(well[loc][1]) {
					newWell[loc][0] = true;
					newWell[loc][1] = true;
					newWell[loc + 1][1] = true;
					return new State(newWell, 2);
				} else if(well[loc][0] || well[loc + 1][1]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc][1] = true;
						wellTest[loc + 1][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc][0] = true;
						newWell[loc][1] = true;
						newWell[loc + 1][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc][0] = true;
					newWell[loc][1] = true;
					newWell[loc + 1][1] = true;
					return new State(clean(newWell), 0);
				}
			} else { //NORTHEAST
				if(well[loc + 1][1]) {
					newWell[loc + 1][0] = true;
					newWell[loc + 1][1] = true;
					newWell[loc][1] = true;
					return new State(newWell, 2);
				} else if(well[loc + 1][0] || well[loc][1]) {
					boolean[][] wellTest = copyWell(well);
					wellTest[loc + 1][1] = true;
					if(isTopRowFull(wellTest)) {
						wellTest = clean(wellTest);
						wellTest[loc][1] = true;
						wellTest[loc + 1][1] = true;
						return new State(wellTest, 0);
					} else {
						newWell = removeBottomRow(well);
						newWell[loc + 1][0] = true;
						newWell[loc + 1][1] = true;
						newWell[loc][1] = true;
						return new State(clean(newWell), 1);
					}
				} else {
					newWell = copyWell(well);
					newWell[loc + 1][0] = true;
					newWell[loc + 1][1] = true;
					newWell[loc][1] = true;
					return new State(clean(newWell), 0);
				}
			}
		case SQUARE: //loc should be between 0 and 4
			if(well[loc][1] || well[loc + 1][1]) {
				newWell[loc][0] = true;
				newWell[loc + 1][0] = true;
				newWell[loc][1] = true;
				newWell[loc + 1][1] = true;
				return new State(newWell, 2);
			} else if(well[loc][0] || well[loc + 1][0]) {
				boolean[][] wellTest = copyWell(well);
				wellTest[loc][1] = true;
				wellTest[loc + 1][1] = true;
				if(isTopRowFull(wellTest)) {
					wellTest = clean(wellTest);
					wellTest[loc][1] = true;
					wellTest[loc + 1][1] = true;
					return new State(wellTest, 0);
				} else {
					newWell = removeBottomRow(well);
					newWell[loc][0] = true;
					newWell[loc + 1][0] = true;
					newWell[loc][1] = true;
					newWell[loc + 1][1] = true;
					return new State(clean(newWell), 1);
				}
			} else {
				newWell = copyWell(well);
				newWell[loc][0] = true;
				newWell[loc + 1][0] = true;
				newWell[loc][1] = true;
				newWell[loc + 1][1] = true;
				return new State(clean(newWell), 0);
			}
		default: //this case won't happen
			return new State(newWell, 0);
		}
	}

	public static boolean isTopRowFull(boolean[][] w) {
		boolean ret = true;
		for(int i = 0; i < width; i++) {
			if(!w[i][1]) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	static public boolean[][] copyWell(boolean[][] w) {
		boolean[][] ret = new boolean[width][2];
		for(int i = 0; i < width; i++) {
			ret[i][0] = w[i][0];
			ret[i][1] = w[i][1];
		}
		return ret;
	}

	static public boolean[][] removeBottomRow(boolean[][] w) {
		boolean[][] ret = new boolean[width][2];
		for(int i = 0; i < width; i++) {
			ret[i][0] = w[i][1];
		}
		return ret;
	}

	static public boolean[][] removeTopRow(boolean[][] w) {
		boolean[][] ret = new boolean[width][2];
		for(int i = 0; i < width; i++) {
			ret[i][0] = w[i][0];
		}
		return ret;
	}

	static public boolean[][] clean(boolean[][] w) {
		boolean bottomRowFlag = true;
		boolean topRowFlag = true;
		for(int i = 0; i < width; i++) {
			if(!w[i][0]) {
				bottomRowFlag = false;
				break;
			}
		}
		for(int i = 0; i < width; i++) {
			if(!w[i][1]) {
				topRowFlag = false;
				break;
			}
		}
		if(bottomRowFlag && topRowFlag) {
			return new boolean[width][2];
		} else if(bottomRowFlag) {
			return removeBottomRow(w);
		} else if(topRowFlag) {
			return removeTopRow(w);
		} else {
			return w;
		}
	}

	public static int wellToInt(boolean[][] b) {
		int ret = 0;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < 2; j++) {
				if(b[i][j]) {
					ret |= 1 << 2 * i + j;
				}
			}
		}
		return ret;
	}

	public State[] tryAll(Block b) {
		State[] ret = new State[0];
		switch(b) {
		case SINGLE:
			ret = new State[6];
			for(int i = 0; i < 6; i++) {
				ret[i] = addBlock(b, i);
			}
			break;
		case STRAIGHT:
			ret = new State[11];
			b.rotate(0);
			for(int i = 0; i < 6; i++) {
				ret[i] = addBlock(b, i);
			}
			b.rotate(1);
			for(int i = 0; i < 5; i++) {
				ret[i + 6] = addBlock(b, i);
			}
			break;
		case DIAGONAL:
			ret = new State[10];
			b.rotate(0);
			for(int i = 0; i < 5; i++) {
				ret[i] = addBlock(b, i);
			}
			b.rotate(1);
			for(int i = 0; i < 5; i++) {
				ret[i + 5] = addBlock(b, i);
			}
			break;
		case TRIANGLE:
			ret = new State[20];
			b.rotate(0);
			for(int i = 0; i < 5; i++) {
				ret[i] = addBlock(b, i);
			}
			b.rotate(1);
			for(int i = 0; i < 5; i++) {
				ret[i + 5] = addBlock(b, i);
			}
			b.rotate(2);
			for(int i = 0; i < 5; i++) {
				ret[i + 10] = addBlock(b, i);
			}
			b.rotate(3);
			for(int i = 0; i < 5; i++) {
				ret[i + 15] = addBlock(b, i);
			}
			break;
		case SQUARE:
			ret = new State[5];
			for(int i = 0; i < 5; i++) {
				ret[i] = addBlock(b, i);
			}
			break;
		default: //do nothing
		}
		return ret;
	}

	public static Block getRandomBlock() {
		int rand = (int)(Math.random() * 5);
		if(rand == 0) { return Block.SINGLE; }
		if(rand == 1) { return Block.STRAIGHT; }
		if(rand == 2) { return Block.DIAGONAL; }
		if(rand == 3) { return Block.TRIANGLE; }
		return Block.SQUARE;
	}

	@Override
	public String toString() {
		String ret = "";
		for(int i = 1; i > -1; i--) {
			for(int j = 0; j < width; j++) {
				if(well[j][i]) {
					ret += "X ";
				} else {
					ret += "_ ";
				}
			}
			ret += "\n";
		}
		return ret;
	}

	public static void main(String[] args) throws InterruptedException {
		int[] totalScore = new int[totalGames];
		for(int gameNo = 0; gameNo < totalGames; gameNo++) {
			Game g = new Game();
			for(int blockNo = 0; blockNo < 10000; blockNo++) {
				Block currentBlock = getRandomBlock();
				State[] nextStates = g.tryAll(currentBlock);
				double[] values = new double[nextStates.length];
				for(int option = 0; option < nextStates.length; option++) {
					State currentState = nextStates[option];
					boolean[][] currentWell = currentState.getWell();
					int rowsRemoved = currentState.getRowsRemoved();
					double thisValue = stateValues[wellToInt(currentWell)];
					values[option] = reward * rowsRemoved + gamma * thisValue;
					//System.out.println("Value of option " + wellToInt(currentWell) + 
					//		" is " + thisValue + " and the rows removed will be " + rowsRemoved);
				}
				int bestOption = 0;
				for(int i = 0; i < values.length; i++) {
					if(values[i] > values[bestOption]) {
						bestOption = i;
					}
				}
				State nextState = nextStates[bestOption];
				boolean[][] nextWell = nextState.getWell();
				int rowsRemovedNow = nextState.getRowsRemoved();
				int wellIndex = wellToInt(g.getWell());
				double updatedValue =
					stateValues[wellIndex] * (1 - alpha) +
						(reward * rowsRemovedNow + gamma * stateValues[wellToInt(nextWell)]) * alpha;
				stateValues[wellIndex] = updatedValue;
				g.updateWell(nextWell);
				g.updateRows(rowsRemovedNow);
				if(debug) {
					System.out.println("\n" + "Block no: " + blockNo + ". Current block type:");
					System.out.println(currentBlock + "\n");
					System.out.println("\nGame number " + gameNo + ". The updated value of state " + wellIndex +
						" is " + updatedValue);
					System.out.println("Rows height now is " + g.rowsHeight + " and the height increased by " +
						rowsRemovedNow);
					System.out.println(g);
					Thread.sleep(2000);
				}
			}
			System.out.println(gameNo);
			totalScore[gameNo] = g.rowsHeight;
		}
		System.out.println(Arrays.toString(totalScore));
	}
}