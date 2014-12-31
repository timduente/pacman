package game.entries.pacman;

import game.controllers.PacManController;
import game.core.Game;
import game.entries.pacman.group4.ActionChooser;
import game.entries.pacman.group4.ActionConditionMemory;
import game.entries.pacman.group4.EnvironmentObserver;
import game.entries.pacman.group4.IActionChooser;
import game.entries.pacman.group4.IEnvironmentObserver;
import game.entries.pacman.group4.IMemory;
import game.entries.pacman.group4.IRewarder;
import game.entries.pacman.group4.IStarCSObject;
import game.entries.pacman.group4.Rewarder;
import game.entries.pacman.group4.XCSObject;
import gui.AbstractPlayer;

import java.io.IOException;
import java.util.ArrayList;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends AbstractPlayer implements PacManController {
	// Place your game logic here to play the game as Ms Pac-Man

	IMemory memory;
	IEnvironmentObserver observer;
	IActionChooser actionChooser;
	IRewarder rewarder;

	public MyPacMan() {
		super();
		memory = new ActionConditionMemory();
		observer = new EnvironmentObserver();
		actionChooser = new ActionChooser();
		rewarder = new Rewarder();

		// Initialisierung der Classifier
		// try {
		// memory.readMemoryFromFile("test.txt");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Test:
		memory.addClassifier(new XCSObject("1", "0", 0.0, 0.0, 20));

		try {
			memory.writeMemoryToFile("test.txt");
			memory.readMemoryFromFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (memory.getMatchings("1") != null) {
			System.out.println("Test: Speichern und Einlesen läuft");
		} else {
			System.out.println("Test: Speichern und Einlesen läuft nicht");
		}
	}

	@Override
	public int getAction(Game game, long timeDue) {
		int nextDirection = -1;

		// Ablauf

		// Beobachtung machen
		String observation = observer.getObservationFromCurrentGameState(game);

		// Reward aus letzter Aktion berechnen
		int reward = observer.getReward(game, timeDue);
		rewarder.giveRewardToActions(reward);

		if (false && reward > 0) { // <- Weiß noch nicht wie Reward verrechnet
									// wird. Theoretisch könnte man auf alle
									// Aktionen den Reward andwenden. Oder er
									// vergisst einfach irgendwann seine
									// Aktionen.
			rewarder.removeAllActionsFromBucket();
		}

		// Beobachtung weiterverarbeiten
		ArrayList<IStarCSObject> matchings = memory.getMatchings(observation);

		ArrayList<IStarCSObject> actionSet = new ArrayList<IStarCSObject>();
		ArrayList<IStarCSObject> matchingSetMinusActionSet = new ArrayList<IStarCSObject>();

		// Berechnung des ActionSets
		actionChooser.getActionSetFor(matchings, actionSet,
				matchingSetMinusActionSet);

		// Belastung aller Classifier, die nicht im ActionSet enthalten sind.
		double taxes = 1.0;
		rewarder.payTaxesToRemainingClassifier(taxes, matchingSetMinusActionSet);

		// Wenn Aktionen im ActionSet sind, dann wird die Aktion ausgeführt.
		if (!actionSet.isEmpty()) {
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(actionSet.get(0)
							.getAction());

		} else {
			// TODO: Genetische Algorithmen um für neue Situation eine
			// entsprechende Handlungsweise ableiten zu können.
			// Action wird zum Action Set und zum Matching Set hinzugefügt.
			nextDirection = -1;
		}

		// Alle Aktionen im ActionSet werden zum Bucket hinzugefügt.
		for (int i = 0; i < actionSet.size(); i++) {
			rewarder.addActionToBucket(actionSet.get(i));
		}

		/*
		 * //Alter Code findet die nächste Pille. int nextDirection =
		 * nextPill(game.getCurPacManLoc(), game);
		 */

		// Richtung wird zurückgegeben.
		return nextDirection;
	}

	int nextPill(int currentPosition, Game game) {
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
				System.out.println("nächste Node: " + path[1]);
				int[] pacmanNeighbors = game.getPacManNeighbours();
				for (int i = 0; i < pacmanNeighbors.length; i++) {
					if (pacmanNeighbors[i] == path[1]) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public String getGroupName() {
		return "Falk und Tim Pacman";
	}
}