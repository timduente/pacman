package classifier.ghosts;

import java.util.Random;

import game.core.Game;
import classifier.IObserverSource;

public class SampleObserver implements IObserverSource {

	Random rnd = new Random();
	
	@Override
	public long getObservation(Game g) {
		
		// TODO: festlegen, welche bits welche bedeutung haben
		
		return rnd.nextLong(); // einfach mal random
	}

}
