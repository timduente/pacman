package game.player.ghost.group4;

import game.core.Game;

public interface IObserverSource {
	int getObservation(Game g);
	int getReward(Game g);
}
