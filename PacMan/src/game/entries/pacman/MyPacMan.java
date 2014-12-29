package game.entries.pacman;

import java.io.IOException;

import game.controllers.PacManController;
import game.core.Game;
import game.entries.pacman.group4.ActionConditionMemory;
import game.entries.pacman.group4.IMemory;
import game.entries.pacman.group4.XCSObject;
import gui.AbstractPlayer;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends AbstractPlayer implements PacManController {
	// Place your game logic here to play the game as Ms Pac-Man
	
	public MyPacMan (){
		super();
		IMemory memory = new ActionConditionMemory();
		memory.addClassifier(new XCSObject("1", "0", 0.0, 0.0, 20));
		
		try {
			memory.writeMemoryToFile("test.txt");
			memory.readMemoryFromFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(memory.getMatchings("1") != null){
			System.out.println("läuft");
		}else{
			System.out.println("läuft nicht");
		}
	}
	
	@Override
	public int getAction(Game game, long timeDue) {

		int nextDirection = nextPill(game.getCurPacManLoc(), game);
		if (nextDirection != -1) {
			System.out.println("Next Direction : " + nextDirection);
			return nextDirection;
		}

		return -1;
	}

	int nextPill(int currentPosition, Game game) {
		int[] allNodesWithPills = game.getPillIndices();

		double minDistanceToPill = 9999999;
		int nextNode = -1;
		for (int i = 0; i < allNodesWithPills.length; i++) {
			int pillIndexForNode = game.getPillIndex(allNodesWithPills[i]);
			if (!game.checkPill(pillIndexForNode)) {
				allNodesWithPills[i] = -1;
			} else {

				if (minDistanceToPill > game.getManhattenDistance(
						currentPosition, allNodesWithPills[i])) {
					minDistanceToPill = game.getManhattenDistance(
							currentPosition, allNodesWithPills[i]);
					nextNode = allNodesWithPills[i];

				}
			}
		}
		int[] path = null;
		if (nextNode != -1) {
			path = game.getPath(currentPosition, nextNode);
			if (path.length >= 2) {
				System.out.println("nächste Node: " + path[1]);
				int[] pacmanNeighbors = game.getPacManNeighbours();
				for (int i = 0; i < pacmanNeighbors.length; i++) {
					if (pacmanNeighbors[i] == path[1]) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public String getGroupName() {
		return "Falk und Tim Pacman";
	}
}