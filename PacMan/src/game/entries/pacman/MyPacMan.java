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

	public MyPacMan() {
		super();
		memory = new ActionConditionMemory(16000);
		observer = new EnvironmentObserver();
		actionChooser = new ActionChooser();
		rewarder = new Rewarder(1);


		try {

			memory.readMemoryFromFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		
		//memory.printClassifier();

		// Reward aus letzter Aktion berechnen
		double reward = observer.getReward(game, timeDue);
		//rewarder.giveRewardToActions(reward ,  0.0);
		// System.out.println("Reward: "+ reward);
		
//		System.out.println("Last Reward: "+ reward);
//		System.out.println("Beobachtung: " + observation);
		// Beobachtung weiterverarbeiten
		ArrayList<IStarCSObject> matchings = memory.getMatchings(observation);

		ArrayList<IStarCSObject> actionSet = new ArrayList<IStarCSObject>();
		ArrayList<IStarCSObject> matchingSetMinusActionSet = new ArrayList<IStarCSObject>();
		
		

		// Berechnung des ActionSets
		IStarCSObject classifierWithMaxPred = actionChooser.getActionSetFor(matchings, actionSet,
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

//			System.out.println("ActionSet war wohl leer");

			IStarCSObject generatedClassifier = memory
					.generateNewClassifierForObservation(observation, game.getPossiblePacManDirs(true));
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(generatedClassifier
							.getAction());
			actionSet.add(generatedClassifier);
			classifierWithMaxPred = generatedClassifier;
		}
		
		//System.out.println("ActionSet");		
//		for(int i = 0; i< 1; i++){
//			System.out.println("Action: " + actionSet.get(i).getAction());
//		}

//		System.out.println("Aktion, die jetzt gemacht wird: "
//				+ actionSet.get(0).getAction());

		// System.out.println("Größe des ActionSets: " + actionSet.size());

		rewarder.giveRewardToActions(reward ,  classifierWithMaxPred.getPrediction());
		rewarder.moveBuckets();

		// Alle Aktionen im ActionSet werden zum Bucket hinzugefügt.
		for (int i = 0; i < actionSet.size(); i++) {
			rewarder.addActionToBucket(actionSet.get(i));
		}
		//System.out.println("Aktion: "+ nextDirection);

		observer.setPDir(nextDirection);
		// Richtung wird zurückgegeben.
		return nextDirection;
	}

	@Override
	public String getGroupName() {
		return "Falk und Tim Pacman";
	}
}