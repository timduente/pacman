package game.entries.pacman;

import game.controllers.PacManController;
import game.core.Game;
import gui.AbstractPlayer;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends AbstractPlayer implements PacManController {
	// Place your game logic here to play the game as Ms Pac-Man
	@Override
	public int getAction(Game game, long timeDue) {

		System.out.println("Fuck your cat!");

		return -1;
	}

	@Override
	public String getGroupName() {
		return "Falk und Tim Pacman";
	}
}