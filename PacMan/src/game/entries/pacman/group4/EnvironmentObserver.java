package game.entries.pacman.group4;

import game.core.G;
import game.core.Game;
import game.core.Game.DM;

public class EnvironmentObserver implements IEnvironmentObserver {

	int directionOfNextPill;
	int directionOfNextPowerPill;
	int directionOfNextGhost;
	boolean nextGhostEdible;

	int[] orderedGhosts = new int[G.NUM_GHOSTS];
	int[] orderedDirectionToGhost = new int[G.NUM_GHOSTS];

	private static int GHOST_DISTANCE_CRITERIUM = 9;

	private char appendBinary(boolean expression) {
		return expression ? '1' : '0';
	}

	public static final String[] binaryDirections = { "00", "01", "10", "11" };

	private String directionToBinary(int direction) {
		return binaryDirections[direction];
	}

	int lastRealPacManDir = 0;

	public void setPDir(int PDir) {
		this.lastRealPacManDir = PDir;
	}

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

		int[] directionToGhost = new int[G.NUM_GHOSTS];

		for (int j = 0; j < neighbours.length; j++) {
			observation.append(appendBinary(neighbours[j] != -1));
		}

		directionOfNextPill = directionToNextPill(game);
		directionOfNextPowerPill = directionToNextPowerPill(game);
		if(directionOfNextPill != -1){
			observation.append(directionToBinary(directionOfNextPill));
		}else{
			observation.append(directionToBinary(directionOfNextPowerPill));
		}
		
		// System.out.println("Beobachtung: " +
		// directionToBinary(directionOfNextPill));

		// Richtung in der die nächste PowerPille liegt: 3bit 000 oben, 001
		// rechts, 010 unten, 011 links, 100 keine PowerPille mehr da


		// System.out.println("Richtung PP:"+ directionOfNextPowerPill);
		if (directionOfNextPowerPill != -1)
			observation.append(directionToBinary(directionOfNextPowerPill));
		else {
			observation.append("||");
		}

		nextGhostEdible = false;
		// directionOfNextGhost

		// Richtung zu allen Geistern: 2bit 00 oben, 01 rechts, 10 unten, 11
		// links, || Fehler. für jeden Geist
		// falls der Geist edible ist folgende bit = 1
		// falls der Geist weniger als 2 Nodes entfernt ist folgende bit = 1

		// Vllt besser Richtung in der ein Edible Ghost ist.

		String[] ghostObservation = new String[G.NUM_GHOSTS];
		int[] ghostDistance = new int[G.NUM_GHOSTS];

		for (int i = 0; i < G.NUM_GHOSTS; i++) {

			int[] path = game.getGhostPath(i, game.getCurPacManLoc());
			ghostDistance[i] = path.length;

			if (path.length >= 1 && path.length < GHOST_DISTANCE_CRITERIUM) {
				directionToGhost[i] = getDirectionToNeighboringNode(game,
						path[path.length - 1]);
				ghostObservation[i] = directionToBinary(directionToGhost[i])
						+ appendBinary(game.isEdible(i));
			} else {
				ghostObservation[i] = "|||";
				ghostDistance[i] = 100000;
			}
		}

		for (int i = 0; i < 2; i++) {
			int min = 100000;
			// System.out.println("Distance: " + )
			int index = -1;
			for (int j = 0; j < G.NUM_GHOSTS; j++) {
				if (min >= ghostDistance[j]) {
					min = ghostDistance[j];
					ghostDistance[j] = 100000;
					index = j;
				}
			}
			orderedGhosts[i] = index;
			orderedDirectionToGhost[i] = directionToGhost[index];
			observation.append(ghostObservation[index]);
		}
		return observation.toString();
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
		double reward = 0;

		int actualActivePillCount = game.getPillIndicesActive().length;
		int actualActivePowerPillCount = game.getPowerPillIndicesActive().length;
		int actualScore = game.getScore();
		int actualLives = game.getLivesRemaining();
		int actualPacmanNode = game.getCurPacManLoc();
		int actualPacmanDir = game.getCurPacManDir();

		if (actualScore == 0) {
			lastScore = 0;
		}

		

		int ghostDistance = game.getGhostPath(orderedGhosts[0], lastPacmanNode).length;
		// System.out.println("GeistDist: " + ghostDistance);
		if (ghostDistance < GHOST_DISTANCE_CRITERIUM && ghostDistance != 0) {
			if (!game.isEdible(orderedGhosts[0])
					&& (actualPacmanDir == orderedDirectionToGhost[0] || game
							.getCurPacManDir() == 4)) {
				// System.out.println("PacmanDir " + actualPacmanDir +
				// " DirToGhost: " + orderedDirectionToGhost[0]);
				reward = reward - 30;
			} else if (!game.isEdible(orderedGhosts[0])
					&& actualPacmanDir != orderedDirectionToGhost[0]
					&& game.getCurPacManDir() != 4) {
				reward = reward + 15;
			} else if (game.isEdible(orderedGhosts[0])
					&& actualPacmanDir == orderedDirectionToGhost[0]) {
				reward = reward + 15;
			} else {

			}
		}
		int ghostDistance2 = game.getGhostPath(orderedGhosts[1], lastPacmanNode).length;
		if (ghostDistance2 < GHOST_DISTANCE_CRITERIUM && ghostDistance2 != 0) {
			if (!game.isEdible(orderedGhosts[1])
					&& (actualPacmanDir == orderedDirectionToGhost[1] || game
							.getCurPacManDir() == 4)) {
				// System.out.println("PacmanDir " + actualPacmanDir +
				// " DirToGhost: " + orderedDirectionToGhost[0]);
				reward = reward - 30;
			} else if (!game.isEdible(orderedGhosts[1])
					&& actualPacmanDir != orderedDirectionToGhost[1]
					&& game.getCurPacManDir() != 4) {
				reward = reward + 15;
			} else if (game.isEdible(orderedGhosts[1])
					&& actualPacmanDir == orderedDirectionToGhost[1]) {
				reward = reward + 15;
			} else {

			}
		}

		// Pacman dreht sich um. Ständiger Richtungswechsel ist unproduktiv
		if (lastPacmanDirection == game.getReverse(actualPacmanDir)) {
			reward = reward - 15;
//			if (lastPacmanNode == actualPacmanNode) {
//				System.out.println("DER FALL TRITT AUF");
//				reward = reward - 8;
//			}
		} else {
			reward = reward + 2;
		}

		if (lastPillCount == actualActivePillCount
				&& lastPowerPillCount == actualActivePowerPillCount) {
			if (actualPacmanDir == directionOfNextPill
					&& lastPacmanDirection != game.getReverse(actualPacmanDir))
				reward = reward + 15;
			else if (lastPacmanDirection != game.getReverse(actualPacmanDir))
				reward = reward - 5;

		}

		if (actualActivePillCount < lastPillCount
				&& actualPacmanDir == directionOfNextPill) {
			reward = reward + 15;
		}

		if (actualActivePowerPillCount < lastPowerPillCount) {
			reward = reward + 15;
		}

		if (actualPacmanDir != lastRealPacManDir) {
//			System.out.println("PacManDir = : " + actualPacmanDir + "!= " + lastRealPacManDir +"= gewollte Aktion");
			if (reward > 0)
				reward = -2;
		} else {
			reward = reward + 1;
		}

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

	protected int directionToNextPill(Game game) {
		int currentPosition = game.getCurPacManLoc();
		int[] allNodesWithPills = game.getPillIndices();

		double minDistanceToPill = 9999999;
		int nextNode = -1;
		for (int i = 0; i < allNodesWithPills.length; i++) {
			int pillIndexForNode = game.getPillIndex(allNodesWithPills[i]);
			if (!game.checkPill(pillIndexForNode)) {
				allNodesWithPills[i] = -1;
			} else {

				int distance = game.getManhattenDistance(currentPosition,
						allNodesWithPills[i]);
				if (minDistanceToPill > distance) {
					minDistanceToPill = distance;
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
			// BUGFIX Pathfunktion liefert unter bestimmten Umständen nicht den
			// vollständigen Pfad.
			if (path.length >= 1 && path[0] != nextNode) {
				return getDirectionToNeighboringNode(game, nextNode);
			}
		}
		return -1;
	}

	protected int getDirectionToNeighboringNode(Game game, int nodeTo) {
		int[] pacmanNeighbors = game.getPacManNeighbours();
		for (int i = 0; i < pacmanNeighbors.length; i++) {
			if (pacmanNeighbors[i] == nodeTo) {
				return i;
			}
		}
		return -1;
	}

	protected int directionToNextPowerPill(Game game) {
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
				return getDirectionToNeighboringNode(game, path[1]);
			}
			if (path.length >= 1 && path[0] != nextNode) {
				return getDirectionToNeighboringNode(game, nextNode);
			}
		}
		return -1;
	}
}
