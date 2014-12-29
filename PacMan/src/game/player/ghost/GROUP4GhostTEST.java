package game.player.ghost;

import game.core.Game;
import gui.AbstractGhost;

public class GROUP4GhostTEST extends AbstractGhost {
	// 0: oben
	// 1: rechts
	// 2: unten
	// 3: links

	int[] dirs = new int[Game.NUM_GHOSTS]; // memory

	// Place your game logic here to play the game as the ghosts
	@Override
	public int[] getActions(Game game, long timeDue) {
		
		
		
		
		for(int g=0;g<Game.NUM_GHOSTS;++g){
			
			// get possible neigbouring directions/nodes
			int[] possibleNeighbours = game.getGhostNeighbours(g);
			int[] possibleDirs = game.getPossibleGhostDirs(g);
			
			double mindist = 999999999;
			int nextDir = -1; // default direction is "lastdirection"
			
			// search for next step which minimizes ghost-pacman-distance
			for(int i=0;i<possibleDirs.length;++i){
				int tryDirection = possibleDirs[i];
				double tryDist = game.getEuclideanDistance(possibleNeighbours[tryDirection], game.getCurPacManLoc());
				if(tryDist <= mindist){
					mindist = tryDist;
					nextDir = tryDirection;
				}
			}
			
			dirs[g] = nextDir;
		}

		return dirs;
	}

	@Override
	public String getGhostGroupName() {
		return "GHOSTTEST";
	}
}
