
package game.player.ghost;

import game.core.Game;
import game.player.ghost.group4.ExternalClassifierParser;
import game.player.ghost.group4.GhostObserver;
import game.player.ghost.group4.IAction;
import game.player.ghost.group4.IClassifierGenerator;
import game.player.ghost.group4.IClassifierSystem;
import game.player.ghost.group4.IObserverSource;
import game.player.ghost.group4.system.ZCSSystem;
import gui.AbstractGhost;

public class GhostGroup4 extends AbstractGhost {

	static final String DATABASE_FILENAME_PREFIX = "ghostdata";
	
	
	IClassifierSystem[] ghostClassifiers = new IClassifierSystem[Game.NUM_GHOSTS];
	IObserverSource[] ghostObservers = new IObserverSource[Game.NUM_GHOSTS];
	IClassifierGenerator classifierGenerator = null;
	
	ExternalClassifierParser[] externalDataParsers = new ExternalClassifierParser[Game.NUM_GHOSTS];
	

	
	//
	//
	// - realisiert als classifier system (learning noch ausarbeiten)
	// - TODO: idee fuer kommunikation: die classifier der einzel-geister untereinander austauschen und gute waehlen!
	//
	//
	//
	//
	
	
	
	public GhostGroup4() {

		// load classifier system with some sample data
		// one instance per ghost
		for (int i = 0; i < Game.NUM_GHOSTS; ++i) {
			GhostObserver o = new GhostObserver(i);
			ghostObservers[i] = o;
			
			ghostClassifiers[i] = new ZCSSystem(o);
			ghostClassifiers[i].addData(o);
			
			// try load some external data
			externalDataParsers[i] = new ExternalClassifierParser("./" +DATABASE_FILENAME_PREFIX + i + ".csv");
			ghostClassifiers[i].addData(externalDataParsers[i]);
			
			
			// egal welcher, funktion unabhaengig von observation-funktionalitaet
			classifierGenerator = o; 
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

		
		//
		// datenpersistenz
		//
		doPersistency(needPersistencyUpdate(game));

		
		//
		// ALT
		//
		
//		for (int g = 0; g < Game.NUM_GHOSTS; ++g) {
//
//			// get possible neigbouring directions/nodes
//			int[] possibleNeighbours = game.getGhostNeighbours(g);
//			int[] possibleDirs = game.getPossibleGhostDirs(g);
//
//			double mindist = 999999999;
//			int nextDir = -1; // default direction is "lastdirection"
//
//			// search for next step which minimizes ghost-pacman-distance
//			for (int i = 0; i < possibleDirs.length; ++i) {
//				int tryDirection = possibleDirs[i];
//				double tryDist = game.getEuclideanDistance(possibleNeighbours[tryDirection], game.getCurPacManLoc());
//				if (tryDist <= mindist) {
//					mindist = tryDist;
//					nextDir = tryDirection;
//				}
//			}
//
//			dirs[g] = nextDir;
//		}

		
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
			
			if(i==0)
				System.out.println("\t zcsghost" + i + "# rew: " + prevReward + " # obs: " + classifierObservation + " # action: " + dirs[i]);
		}

		return dirs;
	}
	
	private void doPersistency(boolean yesDoIt){
		
		if(!yesDoIt) 
			return;
		
		for (int g = 0; g < Game.NUM_GHOSTS; ++g) {
			externalDataParsers[g].export(ghostClassifiers[g], "./" +DATABASE_FILENAME_PREFIX + g + ".csv");
		}
		
	}
	
	private boolean needPersistencyUpdate(Game g){
		return g.getLevelTime() > 100 && (g.getLevelTime() % 100) == 0;
	}
	
	

	@Override
	public String getGhostGroupName() {
		return "GhostGroup4";
	}
}
