package game.entries.ghosts;

import game.controllers.GhostController;
import game.core.Game;
import gui.AbstractGhost;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends AbstractGhost implements GhostController 
{
	//Place your game logic here to play the game as the ghosts
	@Override
	public int[] getActions(Game game,long timeDue)
	{		
		return null;
	}
	
	
	
	@Override
	public String getGhostGroupName() {
		return "Falk und Tim Ghost";
	}
}