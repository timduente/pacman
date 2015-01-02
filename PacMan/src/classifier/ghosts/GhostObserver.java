package classifier.ghosts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.core.Game;
import classifier.IAction;
import classifier.IObservation;
import classifier.IObserverSource;
import classifier.IZCSClassifierDataSource;
import classifier.system.zcs.ZCSEntry;
import classifier.system.zcs.ZCSObservation;

//
// data source interface hier auch, damit inhalt der observation-objekte
// moeglichst an einer stelle beisammen ist
//
public class GhostObserver implements IObserverSource, IZCSClassifierDataSource {

	private static final int NUM_BITS = 32;
	Random rnd = new Random();
	double previousPacmanDist = Double.MAX_VALUE;
	int ghostID = -1;

	public GhostObserver(int ghostID) {
		this.ghostID = ghostID;
	}

	//
	// observation dependent
	//

	//
	// # belegung observation bits:
	// [0] is LSB (gewoehnlich rechts)
	// ...
	// [32/64] is MSB (gewoehnlich links)
	//
	//
	// # semantik [index] [1 means <...> ]
	// [0] ghost-y > pacman-y
	// [1] ghost-y < pacman-y
	// [2] ghost-x > pacman-x
	// [3] ghost-x < pacman-x
	//
	// [4] ghosts-are-edible
	//

	@Override
	public int getObservation(Game g) {

		boolean[] bitmap = new boolean[NUM_BITS];
		for (int i = 0; i < NUM_BITS; ++i) {
			bitmap[i] = false; // default
		}

		final int myX = g.getX(g.getCurGhostLoc(ghostID));
		final int myY = g.getY(g.getCurGhostLoc(ghostID));
		final int pacmanX = g.getX(g.getCurPacManLoc());
		final int pacmanY = g.getY(g.getCurPacManLoc());

		//
		// setup bitmasks
		//

		bitmap[0] = myY > pacmanY;
		bitmap[1] = myY < pacmanY;
		bitmap[2] = myX > pacmanX;
		bitmap[3] = myX < pacmanX;

		bitmap[4] = g.getEdibleTime(ghostID) > 0; // isEdible

		//
		// convert binary bitmask to datatype
		//
		int result = 0x00000000; // TEST: rnd.nextInt();
		for (int i = 0; i < NUM_BITS; ++i) {
			if (bitmap[i]) {
				result = result | (1 << i); //
			}
		}

		return result;
	}

	private static final int IS_EDIBLE_BITS = 1 << 4;
	private static final int IS_NOT_EDIBLE_BITS = 1 << 4;

	@Override
	public List<ZCSEntry> getSomeData() {

		final int DEFAULT_MAX_FITNESS = 50;

		// action:
		// 0: oben
		// 1: rechts
		// 2: unten
		// 3: links

		List<ZCSEntry> data = new ArrayList<ZCSEntry>();

//		// some sample data
//		final int wildcardHopefullyAll1 = 0xFFFFFFFF;
//		IObservation tst0o = new ZCSObservation(0, wildcardHopefullyAll1);
//		IAction tst0a = new GhostAction(-1);
//		ZCSEntry everything = new ZCSEntry(tst0o, tst0a, DEFAULT_MAX_FITNESS);
//		data.add(everything);

		//
		// basic directions for moving towards pacman when not edible
		//
		// action:
		// 0: oben
		// 1: rechts
		// 2: unten
		// 3: links

		// TODO: annahme: koordinatenursprung (0,0) ist unten links und y nach oben steigen und x nach rechts steigend

		// move down
		final int obsBitsDown = 1 << 0;
		ZCSObservation obsDown = new ZCSObservation(obsBitsDown); // observation: posy
		obsDown.setWldCard(0xFFFFFFFF).setWldBit(0, false).setWldBit(4, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsDown, new GhostAction(2), DEFAULT_MAX_FITNESS));

		// move up
		final int obsBitsUp = 1 << 1;
		ZCSObservation obsUp = new ZCSObservation(obsBitsUp); // observation: posy
		obsUp.setWldCard(0xFFFFFFFF).setWldBit(1, false).setWldBit(4, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsUp, new GhostAction(0), DEFAULT_MAX_FITNESS));

		// move left
		final int obsBitsLeft = 1 << 2;
		ZCSObservation obsLeft = new ZCSObservation(obsBitsLeft); // observation: posy
		obsLeft.setWldCard(0xFFFFFFFF).setWldBit(2, false).setWldBit(4, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsLeft, new GhostAction(0), DEFAULT_MAX_FITNESS));

		// move right
		final int obsBitsRight = 1 << 3;
		ZCSObservation obsRight = new ZCSObservation(obsBitsRight); // observation: posy
		obsRight.setWldCard(0xFFFFFFFF).setWldBit(3, false).setWldBit(4, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsRight, new GhostAction(0), DEFAULT_MAX_FITNESS));

		return data;
	}

	//
	// reward
	//

	@Override
	public int getReward(Game g) {

		//
		// negative rewards possible
		//

		//
		// indirect goal: minimize (extremize) distance to pacman (to kill him)
		//

		final double currentTotalPacmanDist = g.getEuclideanDistance(g.getCurGhostLoc(ghostID), g.getCurPacManLoc());

		int minDistReward = 0;
		if (g.getEdibleTime(ghostID) > 0) {
			// if edible: indirect goal is to maximize distances
			minDistReward = (int) (3 * (previousPacmanDist - currentTotalPacmanDist));
		} else {
			// if not edible: indirect goal is to minimize distances
			minDistReward = (int) (3 * (currentTotalPacmanDist - previousPacmanDist));
		}

		//
		// other goals ...
		//

		// update data
		previousPacmanDist = currentTotalPacmanDist;

		return minDistReward;
	}
}
