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
import game.entries.pacman.group4.Saver;
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

	private static Saver saver;
	private static boolean hookAdded = false;;

	IMemory memory;
	IEnvironmentObserver observer;
	IActionChooser actionChooser;
	IRewarder rewarder;
	IRewarder secondLevelRewarder;

	public MyPacMan() {
		super();
		memory = new ActionConditionMemory(10);
		observer = new EnvironmentObserver();
		actionChooser = new ActionChooser();
		rewarder = new Rewarder();
		secondLevelRewarder = new Rewarder();

		try {

			memory.readMemoryFromFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Test:
		// memory.addClassifier(new
		// XCSObject("1######00010000000100001000010000", "00", 10, 0.2, 20));
		// memory.addClassifier(new
		// XCSObject("#1#####00110000000100001000010000", "01", 10, 0.2, 20));
		// memory.addClassifier(new
		// XCSObject("##1####01010000000100001000010000", "10", 10, 0.2, 20));
		// memory.addClassifier(new
		// XCSObject("###1###01110000000100001000010000", "11", 10, 0.2, 20));

		// memory.addClassifier(new XCSObject("1######1###########", "00", 10,
		// 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("#1######1##########", "01", 10,
		// 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("##1######1#########", "10", 10,
		// 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("###1######1########", "11", 10,
		// 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("00", "00", 10, 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("01", "01", 10, 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("10", "10", 10, 0.2,
		// 20));
		// memory.addClassifier(new XCSObject("11", "11", 10, 0.2,
		// 20));

		// Hook zum Abspeichern der Classifier
		if (!hookAdded) {
			saver = new Saver();
			Runtime.getRuntime().addShutdownHook(saver);
			hookAdded = true;
		}
		saver.setMemory(memory);

	}

	@Override
	public int getAction(Game game, long timeDue) {
		int nextDirection = -1;

		// Ablauf

		// Beobachtung machen
		String observation = observer.getObservationFromCurrentGameState(game);

		System.out.println("Beobachtung: " + observation);
		memory.printClassifier();

		// Reward aus letzter Aktion berechnen
		double reward = observer.getReward(game, timeDue);
		// System.out.println("Reward: "+ reward);

		// Beobachtung weiterverarbeiten
		ArrayList<IStarCSObject> matchings = memory.getMatchings(observation);

		ArrayList<IStarCSObject> actionSet = new ArrayList<IStarCSObject>();
		ArrayList<IStarCSObject> matchingSetMinusActionSet = new ArrayList<IStarCSObject>();

		// Berechnung des ActionSets
		actionChooser.getActionSetFor(matchings, actionSet,
				matchingSetMinusActionSet);

		// Belastung aller Classifier, die nicht im ActionSet enthalten sind.
		rewarder.payTaxesToRemainingClassifier(matchingSetMinusActionSet);

		// Wenn Aktionen im ActionSet sind, dann wird die Aktion ausgeführt.
		if (!actionSet.isEmpty()) {
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(actionSet.get(0)
							.getAction());

		} else {
			// TODO: Genetische Algorithmen um für neue Situation eine
			// entsprechende Handlungsweise ableiten zu können.
			// Action wird zum Action Set und zum Matching Set hinzugefügt.
			// nextDirection = nextPill(game.getCurPacManLoc(), game);

			System.out.println("ActionSet war wohl leer");

			IStarCSObject generatedClassifier = memory
					.generateNewClassifierForObservation(observation);
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(generatedClassifier
							.getAction());
			actionSet.add(generatedClassifier);
		}

		System.out.println("Aktion, die jetzt gemacht wird: "
				+ actionSet.get(0).getAction());

		// System.out.println("Größe des ActionSets: " + actionSet.size());

		// TODO: Reward anpassen
		// rewarder.giveRewardToActions(reward + 0.71
		// * actionSet.get(0).getPrediction());

		secondLevelRewarder.giveRewardToActions(reward / 2.0);
		secondLevelRewarder.removeAllActionsFromBucket();

		rewarder.giveRewardToActions(reward);
		ArrayList<IStarCSObject> secondLevelActionSet = rewarder
				.removeAllActionsFromBucket();

		// Alle Aktionen im ActionSet werden zum Bucket hinzugefügt.
		for (int i = 0; i < actionSet.size(); i++) {
			rewarder.addActionToBucket(actionSet.get(i));
		}

		for (int i = 0; i < secondLevelActionSet.size(); i++) {
			secondLevelRewarder.addActionToBucket(secondLevelActionSet.get(i));
		}

		// try {
		// memory.writeMemoryToFile("test.txt");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Richtung wird zurückgegeben.
		return nextDirection;
	}

	@Override
	public String getGroupName() {
		return "Falk und Tim Pacman";
	}
}