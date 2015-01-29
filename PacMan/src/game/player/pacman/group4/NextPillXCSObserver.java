package game.player.pacman.group4;

import game.core.Game;

public class NextPillXCSObserver extends EnvironmentObserver {

	int directionOfNextPill;
	
	@Override
	public String getObservationFromCurrentGameState(Game game) {
		StringBuilder observation = new StringBuilder();
		directionOfNextPill = directionToNextPill(game);
		return observation.toString();
	}

	@Override
	public double getReward(Game game, long time) {
		return getSimpleReward(game, time);
	}
	


	public double getSimpleReward(Game game, long time) {
		double reward = 0;
		int actualActivePillCount = game.getPillIndicesActive().length;
		int actualActivePowerPillCount = game.getPowerPillIndicesActive().length;
		int actualPacmanNode = game.getCurPacManLoc();
		int actualPacmanDir = game.getCurPacManDir();

		// Pacman dreht sich um
		if (lastPacmanDirection == game.getReverse(actualPacmanDir)) {
			reward = reward - 2;
		} else {
			reward = reward + 0.02;
		}
		if (lastPacmanNode == actualPacmanNode) {
			reward = reward - 2;
		} else {
			reward = reward + 0.02;
		}

		if (lastPillCount == actualActivePillCount
				&& lastPowerPillCount == actualActivePowerPillCount) {
			if (actualPacmanDir == directionOfNextPill )
				reward = reward + 5;
			else
				reward = reward - 5;
		}

		if (actualActivePillCount < lastPillCount) {
			reward = reward + 15;
		}
		if (actualActivePowerPillCount < lastPowerPillCount) {
			reward = reward + 15;
		}
		lastPillCount = actualActivePillCount;
		lastPowerPillCount = actualActivePowerPillCount;
		lastPacmanNode = actualPacmanNode;
		lastPacmanDirection = actualPacmanDir;
		return reward;
	}

}
