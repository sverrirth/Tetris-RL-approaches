all: smalltetris bigtetris
smalltetris: smalltetris/Game.class
smalltetris/Game.class: smalltetris/Game.java smalltetris/State.class
	javac smalltetris/Game.java
smalltetris/State.class: smalltetris/State.java
	javac smalltetris/State.java
bigtetris: tetris/Tetris.class
tetris/Tetris.class: tetris/Tetris.java tetris/Board.class tetris/OrientedPiece.class tetris/Piece.class 
	javac tetris/Tetris.java
tetris/Board.class: tetris/Board.java
	javac tetris/Board.java
tetris/Piece.class: tetris/Piece.java
	javac tetris/Piece.java
tetris/OrientedPiece.class: tetris/OrientedPiece.java
	javac tetris/OrientedPiece.java

runsmall: smalltetris
	java smalltetris.Game
runbig: bigtetris
	java tetris.Tetris
clean:
	rm -f smalltetris/*.class tetris/*.class
