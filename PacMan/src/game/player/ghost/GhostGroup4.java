
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
	static final boolean IGNORE_NEGATIVE_CLASSIFIERS_ON_EXPORT = true; // do not export bad classifiers with low fitness
	static final boolean ENABLE_FORGETTING = true; // forget some bad classifiers
	
	IClassifierSystem[] ghostClassifiers = new IClassifierSystem[Game.NUM_GHOSTS];
	IObserverSource[] ghostObservers = new IObserverSource[Game.NUM_GHOSTS];
	IClassifierGenerator classifierGenerator = null;
	
	ExternalClassifierParser[] externalDataParsers = new ExternalClassifierParser[Game.NUM_GHOSTS];

	
	//
	//
	// - communication of agents could be done via "static" database with "global knowledge" which could be
	// achieved with "pseudo-communication" asking other databases for their best classifiers with a given observation
	//
	// -> communication currently not possible because observations are "ghost-dependent" and DO CONTAIN GHOST-DEPENDENT data
	//
	//
	// - average performance is at about 4000 - 5000 pacman points per game (2015-01-22)
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
		// learning classifier
		// 

		for (int i = 0; i < Game.NUM_GHOSTS; ++i) {
			
			IClassifierSystem system = ghostClassifiers[i];
			IObserverSource observer = ghostObservers[i];
			
			
			// forget some bad classifiers
			if(game.getLevelTime() % 300 == 0 && ENABLE_FORGETTING) {
				system.removeClassifiersWithSmallerFitness(0);
			}
			
			// performance da geister eh "nichts tun koennen"
			if(game.getLairTime(i) > 0) {
				dirs[i] = -1;
				continue;
			}
			
			// previous reward
			final int prevReward = observer.getReward(game);
			system.reward(prevReward);
			
			// observe new situation
			final int classifierObservation = observer.getObservation(game);
			
			// new action selection
			IAction classifierAction = system.getAction(classifierObservation);
			
			// put to data output
			dirs[i] = classifierAction.getActionBits();
		}

		return dirs;
	}
	
	private void doPersistency(boolean yesDoIt){
		
		if(!yesDoIt) 
			return;
		
		for (int g = 0; g < Game.NUM_GHOSTS; ++g) {
			//ghostClassifiers[g].printSomeInfoToConsole();
			externalDataParsers[g].export(ghostClassifiers[g], "./" +DATABASE_FILENAME_PREFIX + g + ".csv", IGNORE_NEGATIVE_CLASSIFIERS_ON_EXPORT);
		}
	}
	
	private boolean needPersistencyUpdate(Game g){
		return g.getLevelTime() > 100 && g.getLevelTime() % 300 == 0;
	}
	
	

	@Override
	public String getGhostGroupName() {
		return "GhostGroup4";
	}
}
