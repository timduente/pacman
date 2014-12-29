package game.player.pacman;


import game.core.Game;
import gui.AbstractPlayer;


public class GROUP4PacmanTEST extends AbstractPlayer {
	@Override
	public int getAction(Game game, long timeDue) {
		
		
		
		
		
		// 0: oben
		// 1: rechts
		// 2: unten
		// 3: links
		return 3;
	}

	@Override
	public String getGroupName() {
		return "PACMANTEST";
	}
}