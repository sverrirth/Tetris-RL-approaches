all: smalltetris
smalltetris: smalltetris/Game.class
smalltetris/Game.class: smalltetris/Game.java smalltetris/State.class
	javac smalltetris/Game.java
smalltetris/State.class: smalltetris/State.java
	javac smalltetris/State.java
run: smalltetris
	java smalltetris.Game
clean:
	rm -f smalltetris/*.class
