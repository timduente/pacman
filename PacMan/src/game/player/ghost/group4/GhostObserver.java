package game.player.ghost.group4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.core.Game;
import game.player.ghost.group4.system.ZCSEntry;
import game.player.ghost.group4.system.ZCSObservation;

//
// data source interface hier auch, damit inhalt der observation-objekte
// moeglichst an einer stelle beisammen ist
//
public class GhostObserver implements IObserverSource, IClassifierDataSource, IClassifierGenerator {

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
	// [ 0] ghost-y < pacman-y
	// [ 1] ghost-y > pacman-y
	// [ 2] ghost-x > pacman-x
	// [ 3] ghost-x < pacman-x
	//
	// [ 4] ghosts-are-edible
	//
	// [ 5] other ghost nearby (konkrete distanz im Code ersichtlich)
	// [ 6] pacman very far away (konkrete distanz im Code ersichtlich)
	// [ 7] pacman very nearby (konkrete distanz im Code ersichtlich)
	// [ 8] pacman NEARER to thisghost-minimaldist-powerpill
	//
	// [ 9] ghost-y < pill-schwerpunkt-y
	// [10] ghost-x < pill-schwerpunkt-x
	// [11] pacman NEARER to pill-schwerpunkt than ghost
	//
	//
	//
	//
	// koordinatenursprung (0,0) ist oben links und y nach unten steigend und x nach rechts steigend
	//

	private double euclDistSquared(double xa, double ya, double xb, double yb) {
		final double dx = xa - xb;
		final double dy = ya - yb;

		return (dx * dx) + (dy * dy);
	}

	private static final int PACMAN_VERYNEARBY_DIST_THRESHOLD = 5;
	private static final int PACMAN_VERYFARAWAY_DIST_THRESHOLD = 25;
	private static final double GHOST_NEARBY_DIST_THRESHOLD = 8;
	private static final int DIST_MAX_STARTVAL = 999999;

	@Override
	public int getObservation(Game g) {

		boolean[] bitmap = new boolean[NUM_BITS];
		for (int i = 0; i < NUM_BITS; ++i) {
			bitmap[i] = false; // default
		}

		final int myNode = g.getCurGhostLoc(ghostID);
		final int pacmanNode = g.getCurPacManLoc();

		final int myX = g.getX(myNode);
		final int myY = g.getY(myNode);
		final int pacmanX = g.getX(pacmanNode);
		final int pacmanY = g.getY(pacmanNode);

		final int pathDistToPacman = g.getPathDistance(myNode, pacmanNode);

		// minimaldistanz zu anderem geist
		double minEuclideanDistToGhost = DIST_MAX_STARTVAL;
		for (int i = 0; i < Game.NUM_GHOSTS; ++i) {
			minEuclideanDistToGhost = Math.min(g.getEuclideanDistance(myNode, g.getCurGhostLoc(i)), minEuclideanDistToGhost);
		}

		// minimaldistanz zu powerpille
		int minDistToPowerpill = DIST_MAX_STARTVAL;
		int idOfMindistPowerpillNode = -1;
		for (int i = 0; i < g.getPowerPillIndices().length; ++i) {
			final int nodeID = g.getPowerPillIndices()[i];

			// wenn powerpille auf node pillnodes[i] noch aktiv --> distanzberechnung
			if (g.checkPill(g.getPowerPillIndex(nodeID))) {
				minDistToPowerpill = Math.min(g.getPathDistance(myNode, nodeID), minDistToPowerpill);
				idOfMindistPowerpillNode = nodeID;
			}
		}

		// pacmandistanz zur powerpille, die von diesem geist am geringsten entfernt ist
		int pacmanDistToMindistPowerpillNode = DIST_MAX_STARTVAL;
		if (idOfMindistPowerpillNode >= 0) {
			pacmanDistToMindistPowerpillNode = g.getPathDistance(pacmanNode, idOfMindistPowerpillNode);
		}

		// schwerpunkt der normalen pills berechnen
		double schwerpunktPillsX = 0;
		double schwerpunktPillsY = 0;

		int[] allPillNodes = g.getPillIndices();
		for (int i = 0; i < allPillNodes.length; ++i) {
			final int nodeID = allPillNodes[i];

			// wenn pille auf node, dann verwenden fuer schwerpunktsberechnung
			if (g.checkPill(g.getPillIndex(nodeID))) {
				schwerpunktPillsX += g.getX(nodeID);
				schwerpunktPillsY += g.getY(nodeID);
			}
		}
		schwerpunktPillsX /= allPillNodes.length;
		schwerpunktPillsY /= allPillNodes.length;

		//
		// setup bitmasks
		//

		bitmap[0] = myY < pacmanY;
		bitmap[1] = myY > pacmanY;
		bitmap[2] = myX > pacmanX;
		bitmap[3] = myX < pacmanX;

		bitmap[4] = g.getEdibleTime(ghostID) > 0; // isEdible

		bitmap[5] = minEuclideanDistToGhost <= GHOST_NEARBY_DIST_THRESHOLD;
		bitmap[6] = pathDistToPacman >= PACMAN_VERYFARAWAY_DIST_THRESHOLD;
		bitmap[7] = pathDistToPacman <= PACMAN_VERYNEARBY_DIST_THRESHOLD;
		bitmap[8] = pacmanDistToMindistPowerpillNode < minDistToPowerpill;

		bitmap[9] = myY < schwerpunktPillsY;
		bitmap[10] = myX < schwerpunktPillsX;
		bitmap[11] = euclDistSquared(pacmanX, pacmanY, schwerpunktPillsX, schwerpunktPillsY) < euclDistSquared(myX, myY, schwerpunktPillsX, schwerpunktPillsY);

		bitmap[12] = false; // reserviert --> richtungsbits geist0
		bitmap[13] = false; // reserviert --> richtungsbits geist0
		bitmap[14] = false; // reserviert --> richtungsbits geist1
		bitmap[15] = false; // reserviert --> richtungsbits geist1
		bitmap[16] = false; // reserviert --> richtungsbits geist2
		bitmap[17] = false; // reserviert --> richtungsbits geist2
		bitmap[18] = false; // reserviert --> richtungsbits geist3
		bitmap[19] = false; // reserviert --> richtungsbits geist3
		bitmap[20] = false; // reserviert --> richtungsbits pacman
		bitmap[21] = false; // reserviert --> richtungsbits pacman

		//
		// convert binary bitmask to datatype
		//
		int result = 0x00000000;
		for (int i = 0; i < NUM_BITS; ++i) {
			if (bitmap[i]) {
				result = result | (1 << i);
			}
		}

		// Richtungsbits verwenden
		// TODO: links/rechts, negativ, .. sonstwasfür bits --> ggf. erst auf byte casten
		final int ghostDir0 = g.getCurGhostDir(0);
		final int ghostDir1 = g.getCurGhostDir(1);
		final int ghostDir2 = g.getCurGhostDir(2);
		final int ghostDir3 = g.getCurGhostDir(3);
		final int pacmanDir = g.getCurPacManDir();

		// ausgehend davon, dass immer nur 0,1,2,3 moeglich sind
		result |= (ghostDir0 & 3) << 12; // 2 richtungsbits an korrekte stelle schieben
		result |= (ghostDir1 & 3) << 14; // 2 richtungsbits an korrekte stelle schieben
		result |= (ghostDir2 & 3) << 16; // 2 richtungsbits an korrekte stelle schieben
		result |= (ghostDir3 & 3) << 18; // 2 richtungsbits an korrekte stelle schieben
		result |= (pacmanDir & 3) << 20; // 2 richtungsbits an korrekte stelle schieben

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

	static final int[] RND_ACTIONS = { MOVE_UP,MOVE_RIGHT,MOVE_DOWN,MOVE_LEFT };
	
	@Override
	public ZCSEntry generateRandomClassifierForObservation(int observation, int fitness) {
		
		ZCSObservation obs = new ZCSObservation(observation, 0);// wildcard declaring everything significant
		//obs.setWldCard(rnd.nextInt()); // random wildcards
		//obs.setWldCard(~observation); // ueberall, wo in der observation eine 0, wird zu wildcard -> also unwichtige bits
		
		IAction a = new GhostAction(RND_ACTIONS[rnd.nextInt(4)]); // chose random movement
		return new ZCSEntry(obs, a, fitness);
	}

	@Override
	public ZCSEntry generateGeneticClassifier(int observation, ZCSEntry a, ZCSEntry b) {
		
		final int wildcardbits = a.getObservation().getWildcards() ^ b.getObservation().getWildcards();
		ZCSObservation obs = new ZCSObservation(observation, wildcardbits);
	
		final int fitnessAvrg = (int)((a.getFitness() + b.getFitness()) * 0.5);
		
		IAction ac = new GhostAction(a.getAction().getActionBits() ^ b.getAction().getActionBits());
		return new ZCSEntry(obs, ac, fitnessAvrg);
	}
}
