package game.player.ghost;

import game.core.Game;
import gui.AbstractGhost;
import classifier.IAction;
import classifier.IClassifierSystem;
import classifier.IObserverSource;
import classifier.ghosts.GhostObserver;
import classifier.system.zcs.ZCSSystem;

public class GROUP4GhostTEST extends AbstractGhost {

	IClassifierSystem[] ghostClassifiers = new IClassifierSystem[Game.NUM_GHOSTS];
	IObserverSource[] ghostObservers = new IObserverSource[Game.NUM_GHOSTS];

	public GROUP4GhostTEST() {

		// load classifier system with some sample data
		// one instance per ghost
		for (int i = 0; i < Game.NUM_GHOSTS; ++i) {
			GhostObserver o = new GhostObserver(i);
			ghostObservers[i] = o;
			
			ghostClassifiers[i] = new ZCSSystem();
			ghostClassifiers[i].addData(o);
			
		}
	}

	// 0: oben
	// 1: rechts
	// 2: unten
	// 3: links
	int[] dirs = new int[Game.NUM_GHOSTS]; // memory

	// Place your game logic here to play the game as the ghosts
	@Override
	public int[] getActions(Game game, long timeDue) {

		for (int g = 0; g < Game.NUM_GHOSTS; ++g) {

			// get possible neigbouring directions/nodes
			int[] possibleNeighbours = game.getGhostNeighbours(g);
			int[] possibleDirs = game.getPossibleGhostDirs(g);

			double mindist = 999999999;
			int nextDir = -1; // default direction is "lastdirection"

			// search for next step which minimizes ghost-pacman-distance
			for (int i = 0; i < possibleDirs.length; ++i) {
				int tryDirection = possibleDirs[i];
				double tryDist = game.getEuclideanDistance(possibleNeighbours[tryDirection], game.getCurPacManLoc());
				if (tryDist <= mindist) {
					mindist = tryDist;
					nextDir = tryDirection;
				}
			}

			dirs[g] = nextDir;
		}

		
		//
		// learning classifier
		// 

		for (int i = 0; i < Game.NUM_GHOSTS; ++i) {
			
			// observation and previous reward
			final int prevReward = ghostObservers[i].getReward(game);
			final int classifierObservation = ghostObservers[i].getObservation(game);
			
			// new action selection
			IAction classifierAction = ghostClassifiers[i].getAction(classifierObservation, prevReward);
			
			// put to data output
			dirs[i] = classifierAction.getActionBits();
			
			System.out.println("\t zcsghost" + i + "# rew: " + prevReward + " # obs: " + classifierObservation + " # action: " + dirs[i]);
		}

		return dirs;
	}

	@Override
	public String getGhostGroupName() {
		return "GHOSTTEST";
	}
}
