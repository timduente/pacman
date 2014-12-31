package game.player.ghost;


import game.core.Game;
import gui.AbstractGhost;
import classifier.IAction;
import classifier.IClassifierSystem;
import classifier.IObserverSource;
import classifier.ghosts.GhostAction;
import classifier.ghosts.SampleGhostDataSource;
import classifier.ghosts.SampleObserver;
import classifier.system.zcs.ZCSSystem;

public class GROUP4GhostTEST extends AbstractGhost {
	
	
	IClassifierSystem classifier = null;
	IObserverSource myGameObserver = new SampleObserver();
	
	
	public GROUP4GhostTEST() {
		
		// load classifier system with some sample data
		classifier = new ZCSSystem();
		classifier.addData(new SampleGhostDataSource());
	}
	
	
	
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
		
		
		
		// classifier selection
		long classifierObservation = myGameObserver.getObservation(game);
		IAction classifierAction = classifier.getAction(classifierObservation);
		
		final int[] classifierDirections = classifierAction.getActionBits();
		final int border = Math.min(classifierAction.getActionBits().length, Game.NUM_GHOSTS);
		String debugStr = "";
		for(int i = 0; i<border;++i){
			dirs[i] = classifierDirections[i];
			debugStr += dirs[i] + "; ";
		}
			
		System.out.println("ZCS-ghostaction: " + debugStr);
		
		return dirs;
	}

	@Override
	public String getGhostGroupName() {
		return "GHOSTTEST";
	}
}
