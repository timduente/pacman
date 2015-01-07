package game.entries.pacman.group4;

import game.core.G;
import game.core.Game;

public class EnvironmentObserver implements IEnvironmentObserver {

	public String getObservationFromCurrentGameState(Game game) {
		/*
		 * Ideen für Informationen, die codiert werden sollten: (x) -
		 * benachbarte Nodes vorhanden? (oben rechts unten links) 4 bit (x) -
		 * Richtung in der die nächste Pille liegt. 2bit 00 oben 01 rechts 10
		 * unten 11 links (x) - Richtung in der die nächste Power Pille liegt.
		 * 2bit (/) - Abstand zum nächsten Geist? (/) - Abstände zu allen
		 * Geistern? - Abstände zu allen Geistern in Form mehrerer Abstufungen.
		 * - Vllt eher Richtungen in denen Geister sind. 4bit (oben rechts unten
		 * links) (x) - Richtungen zu allen Geistern? (indirekt, Geist ist
		 * essbar oder nicht ) - Pacman hat ne Pille gefressen? Power Pille?
		 */

		StringBuilder observation = new StringBuilder();

		int[] neighbours = game.getPacManNeighbours();

		// Erste 4 Bit Richtung in die der Pacman gehen kann. Und kein nicht
		// essbarer Geist auf dem Feld
		// for (int j = 0; j < neighbours.length; j++) {
		// boolean ghostOnPosition = false;
		// for (int i = 0; i < G.NUM_GHOSTS; i++) {
		// if (neighbours[j] != -1
		// && neighbours[j] == game.getCurGhostLoc(i)
		// && !game.isEdible(i)) {
		// ghostOnPosition = true;
		// }
		// }
		// observation.append(appendBinary(neighbours[j] != -1
		// && !ghostOnPosition));
		// }

		// for (int j = 0; j < neighbours.length; j++) {
		// observation.append(appendBinary(neighbours[j] != -1));
		// }

		// Letzte Richtung des Pacman:
		// observation.append(directionToBinary(game.getCurPacManDir()));

		// 4bit 1 fall auf Nachbarfeld eine aktive Pille liegt: oder ein
		// essbarer Geist dort ist.
		// for (int i = 0; i < neighbours.length; i++) {
		// observation.append(appendBinary(neighbours[i] != -1
		// && game.getPillIndex(neighbours[i]) != -1
		// && game.checkPill(game.getPillIndex(neighbours[i]))));
		// }

		// int directionOfNextPill = directionToNextPill(game);
		//
		// for (int j = 0; j < neighbours.length; j++) {
		// boolean edibleGhostOnPosition = false;
		// for (int i = 0; i < G.NUM_GHOSTS; i++) {
		// if (neighbours[j] != -1
		// && neighbours[j] == game.getCurGhostLoc(i)
		// && game.isEdible(i)) {
		// edibleGhostOnPosition = true;
		// }
		//
		// }
		//
		// observation
		// .append(appendBinary((neighbours[j] != -1
		// && game.getPillIndex(neighbours[j]) != -1 && game
		// .checkPill(game.getPillIndex(neighbours[j])))
		// || (neighbours[j] != -1
		// && game.getPowerPillIndex(neighbours[j]) != -1 &&
		// game.checkPowerPill(game
		// .getPowerPillIndex(neighbours[j])))
		// || edibleGhostOnPosition ));
		// }

		// Richtung in der die nächste Pille liegt: 3bit 000 oben, 001 rechts,
		// 010 unten, 011 links, 100 keine Pille mehr da

		int directionOfNextPill = directionToNextPill(game);
		observation.append(directionToBinary(directionOfNextPill).substring(1));
		System.out.println("Beobachtung: " + directionToBinary(directionOfNextPill));

		// Richtung in der die nächste PowerPille liegt: 3bit 000 oben, 001
		// rechts, 010 unten, 011 links, 100 keine PowerPille mehr da

		int directionOfNextPowerPill = directionToNextPowerPill(game);
		// observation.append(directionToBinary(directionOfNextPowerPill));

		// Richtung zu allen Geistern: 3bit 000 oben, 001 rechts, 010 unten, 011
		// links, 100 für jeden Geist
		// falls der Geist edible ist folgende bit = 1
		// falls der Geist weniger als 2 Nodes entfernt ist folgende bit = 1

		// Vllt besser Richtung in der ein Edible Ghost ist.

		// for(int i = 0; i< G.NUM_GHOSTS; i++){

		// System.out.println("ghost " + i +" at: " + game.getCurGhostLoc(i));
		// if(game.getCurGhostLoc(i))

		// int[] path = game.getPath(game.getCurGhostLoc(i),
		// game.getCurPacManLoc());
		// obiger Aufruf endet in Endlosschleife.

		// int[] path = game.getGhostPath(i, game.getCurPacManLoc());
		//
		// if( path.length > 1){

		// for(int j = 0; j< path.length; j++){
		// System.out.println("Path @ " + j + " : " + path[j] );
		//
		// }
		//
		// for(int j = 0; j< neighbours.length; j++){
		// System.out.println("Nachbar " + j + " : " + neighbours[j]);
		// }
		// observation.append(directionToBinary(getDirectionToNeighboringNode(game,
		// path[path.length - 1])));
		// observation.append(appendBinary(path.length < 3));
		// observation.append(appendBinary(game.isEdible(i))); // <- evtl.
		// Berechnen ob der Pacman den Geist noch kriegen kann.
		// }else{
		// observation.append(directionToBinary(-1));
		// observation.append("00");
		// }
		// }

		return observation.toString();

	}

	private char appendBinary(boolean expression) {
		return expression ? '1' : '0';
	}

	public static final String[] binaryDirections = { "100", "000", "001",
			"010", "011" };

	private String directionToBinary(int direction) {
		if (direction > 3) {
			return binaryDirections[0];
		}
		return binaryDirections[direction + 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * game.entries.pacman.group4.IEnvironmentObserver#getReward(game.core.Game,
	 * long)
	 * 
	 * Für die Berechnung des Rewards sollte Zustände gemacht werden: - Anzahl
	 * der Pillen (<- Pille weniger heißt Reward) - Anzahl der Powerpillen -
	 * Abgelaufenen Zeit - remaining lives - score
	 */

	int lastPillCount;
	int lastPowerPillCount;
	int lastScore;
	int lastLives = 3;
	int lastPacmanNode;
	int lastPacmanDirection;
	int lastDistance;

	@Override
	public double getReward(Game game, long time) {
		double reward = 1;

		int actualActivePillCount = game.getPillIndicesActive().length;
		int actualActivePowerPillCount = game.getPowerPillIndicesActive().length;
		int actualScore = game.getScore();
		int actualLives = game.getLivesRemaining();
		int actualPacmanNode = game.getCurPacManLoc();
		int actualPacmanDir = game.getCurPacManDir();

		if (actualScore == 0) {
			lastScore = 0;
		}

		 //Pacman dreht sich um
		 if (lastPacmanDirection != actualPacmanDir
		 && ((actualPacmanDir + lastPacmanDirection) == 4 || (actualPacmanDir
		 + lastPacmanDirection) == 2)){
			 reward = reward - 2; //<-- kein negativer Reward mehr. 
		 } 
		 else{
			 reward = reward + 0.01;
		 }
		//
		 if (lastPacmanNode == actualPacmanNode) {
		   reward = reward - 2;
		 }
		 else{
			 reward = reward + 0.01 ;
		 }
		//
		// // System.out.println("lives: " + actualLives);
		// if (lastLives < actualLives) {
		// reward = reward - 20;
		//
		// }

		 if(actualActivePillCount < lastPillCount){
		 reward = reward + 15;
		 }
		
		 if(actualActivePowerPillCount < lastPowerPillCount){
		 reward = reward + 15;
		 }

		//reward = reward + actualScore - lastScore;

		int currentPosition = game.getCurPacManLoc();
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

//		int actualDistance = game.getPath(game.getCurPacManLoc(), nextNode).length;
//		if (actualActivePillCount < lastPillCount) {
//			lastDistance = actualDistance;
//		}

//		if (actualDistance < lastDistance) {
//			reward = reward + 100;
//		}
		
		

		//lastDistance = actualDistance;

		lastPillCount = actualActivePillCount;
		lastPowerPillCount = actualActivePowerPillCount;
		lastScore = actualScore;
		lastLives = actualLives;
		lastPacmanNode = actualPacmanNode;
		lastPacmanDirection = actualPacmanDir;

		return reward;
	}

	/*
	 * Methoden die die entsprechenden Funktionalitäten bereitstellen.
	 */

	private int directionToNextPill(Game game) {

		int currentPosition = game.getCurPacManLoc();
		int[] allNodesWithPills = game.getPillIndices(); 

		double minDistanceToPill = 9999999;
		int nextNode = -1;
		for (int i = 0; i < allNodesWithPills.length; i++) {
			int pillIndexForNode = game.getPillIndex(allNodesWithPills[i]);
			if (!game.checkPill(pillIndexForNode)) {
				allNodesWithPills[i] = -1;
			} else {

				int distance = game.getManhattenDistance(
						currentPosition, allNodesWithPills[i]);
				if (minDistanceToPill > distance ) {
					minDistanceToPill = distance;
					nextNode = allNodesWithPills[i];

				}
			}
		}
		int[] path = null;
		if (nextNode != -1) {
//			System.out.println("nächste Node: " + nextNode);
//			System.out.println("PacmanPosition " + game.getCurPacManLoc());
//			int[] n = game.getPacManNeighbours();
//			for(int i =0; i< n.length; i++){
//				System.out.println("Nachbar an i=" + i + ": " + n[i]);
//			}

			path = game.getPath(currentPosition, nextNode);
//			for(int i = 0; i< path.length; i++){
//				System.out.println("Pathnode "+ i + " " +path[i]);
//			}
			if (path.length >= 2) {
				return getDirectionToNeighboringNode(game, path[1]);
			}
			//BUGFIX Pathfunktion liefert unter bestimmten Umständen nicht den vollständigen Pfad.
			if(path.length >= 1 && path[0] != nextNode){
				return getDirectionToNeighboringNode(game, nextNode);
			}
		}
		return -1;
	}

	private int getDirectionToNeighboringNode(Game game, int nodeTo) {
		int[] pacmanNeighbors = game.getPacManNeighbours();
		for (int i = 0; i < pacmanNeighbors.length; i++) {
			if (pacmanNeighbors[i] == nodeTo) {
				return i;
			}
		}
		return -1;
	}

	private int directionToNextPowerPill(Game game) {

		int currentPosition = game.getCurPacManLoc();
		int[] allNodesWithPills = game.getPowerPillIndices();

		double minDistanceToPill = 9999999;
		int nextNode = -1;
		for (int i = 0; i < allNodesWithPills.length; i++) {
			int pillIndexForNode = game.getPowerPillIndex(allNodesWithPills[i]);
			if (!game.checkPowerPill(pillIndexForNode)) {
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
				getDirectionToNeighboringNode(game, path[1]);
			}
			if(path.length >= 1 && path[0] != nextNode){
				return getDirectionToNeighboringNode(game, nextNode);
			}
		}
		return -1;
	}

}
