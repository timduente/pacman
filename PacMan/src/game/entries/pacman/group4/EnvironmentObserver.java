package game.entries.pacman.group4;

import game.core.G;
import game.core.Game;

public class EnvironmentObserver implements IEnvironmentObserver{
	
	public String getObservationFromCurrentGameState(Game game){
		/*Ideen für Informationen, die codiert werden sollten:
		 * (x) - benachbarte Nodes vorhanden? (oben rechts unten links)  4 bit
		 * (x) - Richtung in der die nächste Pille liegt. 2bit 00 oben 01 rechts 10 unten 11 links
		 * (x) - Richtung in der die nächste Power Pille liegt. 2bit
		 * (/) - Abstand zum nächsten Geist?
		 * (/) - Abstände zu allen Geistern?
		 * - Abstände zu allen Geistern in Form mehrerer Abstufungen.
		 * - Vllt eher Richtungen in denen Geister sind. 4bit (oben rechts unten links) 
		 * (x) - Richtungen zu allen Geistern?
		 * (indirekt, Geist ist essbar oder nicht ) - Pacman hat ne Pille gefressen? Power Pille?
		 * 
		 */
		
		StringBuilder observation = new StringBuilder();
		
		int[] neighbours = game.getPacManNeighbours(); 
		
		// Erste 4 Bit Richtung in die der Pacman gehen kann.
		observation.append(appendBinary(neighbours[0] != -1));
		observation.append(appendBinary(neighbours[1] != -1));
		observation.append(appendBinary(neighbours[2] != -1));
		observation.append(appendBinary(neighbours[3] != -1));	
		
		
		
		// Richtung in der die nächste Pille liegt: 3bit 000 oben, 001 rechts, 010 unten, 011 links, 100 keine Pille mehr da
		
		int directionOfNextPill = directionToNextPill(game);
		observation.append(directionToBinary(directionOfNextPill));
		
		// Richtung in der die nächste PowerPille liegt: 3bit 000 oben, 001 rechts, 010 unten, 011 links, 100 keine PowerPille mehr da
		
		int directionOfNextPowerPill = directionToNextPowerPill(game);
		observation.append(directionToBinary(directionOfNextPowerPill));
		
		// Richtung zu allen Geistern: 3bit 000 oben, 001 rechts, 010 unten, 011 links, 100 für jeden Geist
		// falls der Geist edible ist folgende bit = 1
		// falls der Geist weniger als 2 Nodes entfernt ist folgende bit = 1
		
		
		//Vllt besser Richtung in der ein Edible Ghost ist.
		
		for(int i = 0; i< G.NUM_GHOSTS; i++){
//			System.out.println("ghost " +  i +" at: " + game.getCurGhostLoc(i));
//			if(game.getCurGhostLoc(i))

			
			//int[] path = game.getPath(game.getCurGhostLoc(i), game.getCurPacManLoc());
			//obiger Aufruf endet in Endlosschleife.
			
			int[] path = game.getGhostPath(i, game.getCurPacManLoc());
			
			if( path.length > 1){
				
//				for(int j = 0; j< path.length; j++){
//					System.out.println("Path @ " + j + " : " + path[j] );
//					
//				}
//				
//				for(int j = 0; j< neighbours.length; j++){
//					System.out.println("Nachbar " + j + " : " + neighbours[j]);
//				}
				observation.append(directionToBinary(getDirectionToNeighboringNode(game, path[path.length - 1])));
				observation.append(appendBinary(path.length < 3));
				observation.append(appendBinary(game.isEdible(i))); // <- evtl. Berechnen ob der Pacman den Geist noch kriegen kann.
			}else{
				observation.append(directionToBinary(-1));
				observation.append("00");
			}
		}
		
		
		
		return observation.toString();
		
	}
	
	private char appendBinary(boolean expression){
		return expression ? '1' : '0';
	}
	
	public static final String[] binaryDirections = { "100", "000", "001", "010", "011"}; 
	
	private String directionToBinary(int direction){
		return binaryDirections[direction + 1];
	}

	
	/*
	 * (non-Javadoc)
	 * @see game.entries.pacman.group4.IEnvironmentObserver#getReward(game.core.Game, long)
	 * 
	 * Für die Berechnung des Rewards sollte Zustände gemacht werden:
	 * - Anzahl der Pillen (<- Pille weniger heißt Reward)
	 * - Anzahl der Powerpillen
	 * - Abgelaufenen Zeit
	 * - remaining lives
	 * - score
	 * 
	 */
	
	int lastPillCount;
	int lastPowerPillCount;
	int lastScore;
	
	
	@Override
	public int getReward(Game game, long time) {
		int reward = 0;
		
		int actualActivePillCount = game.getPillIndicesActive().length;
		int actualActivePowerPillCount = game.getPowerPillIndicesActive().length;
		int actualScore = game.getScore();
		
		if(actualActivePillCount < lastPillCount){
			reward = reward + 10;
		}
		
		if(actualActivePowerPillCount < lastPowerPillCount){
			reward = reward + 10;
		}
		
		reward = reward + lastScore - actualScore;
		//möglichst hoher score
		
		lastPillCount = actualActivePillCount;
		lastPowerPillCount = actualActivePowerPillCount;
		lastScore = actualScore;
			
		return reward;
	}
	
	/*
	 * Methoden die die entsprechenden Funktionalitäten bereitstellen.
	 * 
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
				return getDirectionToNeighboringNode(game, path[1]);
			}
		}
		return -1;
	}
	
	private int getDirectionToNeighboringNode(Game game, int nodeTo){
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
		}
		return -1;
	}
	
	

}
