all: smalltetris bigtetris
smalltetris: smalltetris/Game.class
smalltetris/Game.class: smalltetris/Game.java smalltetris/State.class
	javac smalltetris/Game.java
smalltetris/State.class: smalltetris/State.java
	javac smalltetris/State.java

runsmall: smalltetris
	java smalltetris.Game
runbig:
	javac -cp "./commons-math3-3.5.jar:." tetris/*.java
	javac -cp "./commons-math3-3.5.jar:." cemethod/*.java
	java -cp "./commons-math3-3.5.jar:." tetris.Main
clean:
	rm -f smalltetris/*.class tetris/*.class cemethod/*.class
