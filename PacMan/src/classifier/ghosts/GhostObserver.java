package classifier.ghosts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.core.Game;
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
	double previousPacmanDist = 30; // initial-distanz geschaetzt
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
	// [0] ghost-y < pacman-y
	// [1] ghost-y > pacman-y
	// [2] ghost-x > pacman-x
	// [3] ghost-x < pacman-x
	//
	// [4] ghosts-are-edible
	//
	//
	// koordinatenursprung (0,0) ist oben links und y nach unten steigend und x nach rechts steigend
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

		bitmap[0] = myY < pacmanY;
		bitmap[1] = myY > pacmanY;
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
	private static final int IS_EDIBLE_IDX = 4;
	private static final int MOVE_UP = 0;
	private static final int MOVE_RIGHT = 1;
	private static final int MOVE_DOWN = 2;
	private static final int MOVE_LEFT = 3;

	@Override
	public List<ZCSEntry> getSomeData() {

		final int DEFAULT_MAX_FITNESS = 50;

		// action:
		// 0: oben
		// 1: rechts
		// 2: unten
		// 3: links
		//
		// koordinatenursprung (0,0) ist oben links und y nach unten steigend und x nach rechts steigend
		//

		List<ZCSEntry> data = new ArrayList<ZCSEntry>();

		//
		// basic directions for moving towards pacman when not edible
		//

		// move down
		final int obsBitsDown = 1;
		ZCSObservation obsDown = new ZCSObservation(obsBitsDown); // observation: posy
		obsDown.setWldCard(0xFFFFFFFF).setWldBit(0, false).setWldBit(IS_EDIBLE_IDX, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsDown, new GhostAction(MOVE_DOWN), DEFAULT_MAX_FITNESS));

		// move up
		final int obsBitsUp = 1 << 1;
		ZCSObservation obsUp = new ZCSObservation(obsBitsUp); // observation: posy
		obsUp.setWldCard(0xFFFFFFFF).setWldBit(1, false).setWldBit(IS_EDIBLE_IDX, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsUp, new GhostAction(MOVE_UP), DEFAULT_MAX_FITNESS));

		// move left
		final int obsBitsLeft = 1 << 2;
		ZCSObservation obsLeft = new ZCSObservation(obsBitsLeft); // observation: posx
		obsLeft.setWldCard(0xFFFFFFFF).setWldBit(2, false).setWldBit(IS_EDIBLE_IDX, false); // pos-x and edible are significant
		data.add(new ZCSEntry(obsLeft, new GhostAction(MOVE_LEFT), DEFAULT_MAX_FITNESS));

		// move right
		final int obsBitsRight = 1 << 3;
		ZCSObservation obsRight = new ZCSObservation(obsBitsRight); // observation: posx
		obsRight.setWldCard(0xFFFFFFFF).setWldBit(3, false).setWldBit(IS_EDIBLE_IDX, false); // pos-x and edible are significant
		data.add(new ZCSEntry(obsRight, new GhostAction(MOVE_RIGHT), DEFAULT_MAX_FITNESS));

		//
		// basic directions for moving ***AWAY FROM*** pacman when edible
		//

		// move up
		final int obsBitsDownEscape = 1 | IS_EDIBLE_BITS;
		ZCSObservation obsDownEscape = new ZCSObservation(obsBitsDownEscape); // observation: posy
		obsDownEscape.setWldCard(0xFFFFFFFF).setWldBit(0, false).setWldBit(IS_EDIBLE_IDX, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsDownEscape, new GhostAction(MOVE_UP), DEFAULT_MAX_FITNESS));

		// move down
		final int obsBitsUpEscape = 1 << 1 | IS_EDIBLE_BITS;
		ZCSObservation obsUpEscape = new ZCSObservation(obsBitsUpEscape); // observation: posy
		obsUpEscape.setWldCard(0xFFFFFFFF).setWldBit(1, false).setWldBit(IS_EDIBLE_IDX, false); // pos-y and edible are significant
		data.add(new ZCSEntry(obsUpEscape, new GhostAction(MOVE_DOWN), DEFAULT_MAX_FITNESS));

		// move left
		final int obsBitsLeftEscape = 1 << 2 | IS_EDIBLE_BITS;
		ZCSObservation obsLeftEscape = new ZCSObservation(obsBitsLeftEscape); // observation: posx
		obsLeftEscape.setWldCard(0xFFFFFFFF).setWldBit(2, false).setWldBit(IS_EDIBLE_IDX, false); // pos-x and edible are significant
		data.add(new ZCSEntry(obsLeftEscape, new GhostAction(MOVE_RIGHT), DEFAULT_MAX_FITNESS));

		// move right
		final int obsBitsRightEscape = 1 << 3 | IS_EDIBLE_BITS;
		ZCSObservation obsRightEscape = new ZCSObservation(obsBitsRightEscape); // observation: posx
		obsRightEscape.setWldCard(0xFFFFFFFF).setWldBit(3, false).setWldBit(IS_EDIBLE_IDX, false); // pos-x and edible are significant
		data.add(new ZCSEntry(obsRightEscape, new GhostAction(MOVE_LEFT), DEFAULT_MAX_FITNESS));

		return data;
	}

	//
	// reward
	//

	@Override
	public int getReward(Game g) {

		// negative rewards possible

		//
		// indirect goal: minimize (extremize) distance to pacman (to kill him)
		//

		final double currentTotalPacmanDist = g.getEuclideanDistance(g.getCurGhostLoc(ghostID), g.getCurPacManLoc());

		int minDistReward = 0;
		if (g.getEdibleTime(ghostID) > 0) {
			// if edible: indirect goal is to maximize distances
			minDistReward = (int) (2 * (currentTotalPacmanDist - previousPacmanDist));
		} else {
			// if not edible: indirect goal is to minimize distances
			minDistReward = (int) (2 * (previousPacmanDist - currentTotalPacmanDist));
		}

		//
		// other goals ...
		//

		// update data
		previousPacmanDist = currentTotalPacmanDist;
		return minDistReward;
	}
}
