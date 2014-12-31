package classifier;

import game.core.Game;

public interface IObserverSource {
	int getObservation(Game g);
	int getReward(Game g);
}
