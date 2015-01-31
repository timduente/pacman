package game.player.pacman;

import java.io.IOException;
import java.util.ArrayList;

import game.core.Game;
import game.player.pacman.group4.ActionChooser;
import game.player.pacman.group4.ActionConditionMemory;
import game.player.pacman.group4.EnvironmentObserver;
import game.player.pacman.group4.IActionChooser;
import game.player.pacman.group4.IEnvironmentObserver;
import game.player.pacman.group4.IMemory;
import game.player.pacman.group4.IRewarder;
import game.player.pacman.group4.IStarCSObject;
import game.player.pacman.group4.Rewarder;
import game.player.pacman.group4.Saver;
import gui.AbstractPlayer;

public class PacmanGroup4 extends AbstractPlayer{

	private static Saver saver;
	private static boolean hookAdded = false;;

	IMemory memory;
	IEnvironmentObserver observer;
	IActionChooser actionChooser;
	IRewarder rewarder;

	public PacmanGroup4() {
		super();
		memory = new ActionConditionMemory(10000);
		observer = new EnvironmentObserver();
		actionChooser = new ActionChooser();
		rewarder = new Rewarder(1);


		try {

			memory.readMemoryFromFile("Group4DataForLevelX.txt");
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
	
	public void write(){
		
		try {
			memory.writeMemoryToFile("test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getAction(Game game, long timeDue) {
		int nextDirection = -1;

		// Ablauf

		// Beobachtung machen
		String observation = observer.getObservationFromCurrentGameState(game);

		

		// Reward aus letzter Aktion berechnen
		double reward = observer.getReward(game, timeDue);

		// Beobachtung weiterverarbeiten
		ArrayList<IStarCSObject> matchings = memory.getMatchings(observation);

		ArrayList<IStarCSObject> actionSet = new ArrayList<IStarCSObject>();
		ArrayList<IStarCSObject> matchingSetMinusActionSet = new ArrayList<IStarCSObject>();
		
		

		// Berechnung des ActionSets
		IStarCSObject classifierWithMaxPred = actionChooser.getActionSetFor(matchings, actionSet,
				matchingSetMinusActionSet);

		// Belastung aller Classifier, die nicht im ActionSet enthalten sind.
		//rewarder.payTaxesToRemainingClassifier(matchingSetMinusActionSet);

		// Wenn Aktionen im ActionSet sind, dann wird die Aktion ausgeführt.
		if (!actionSet.isEmpty()) {
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(actionSet.get(0)
							.getAction());

		} else {

			IStarCSObject generatedClassifier = memory
					.generateNewClassifierForObservation(observation, game.getPossiblePacManDirs(true));
			nextDirection = actionChooser
					.convertActionStringToDirectionInt(generatedClassifier
							.getAction());
			actionSet.add(generatedClassifier);
			classifierWithMaxPred = generatedClassifier;
		}

		rewarder.giveRewardToActions(reward ,  classifierWithMaxPred.getPrediction());
		rewarder.moveBuckets();

		// Alle Aktionen im ActionSet werden zum Bucket hinzugefügt.
		for (int i = 0; i < actionSet.size(); i++) {
			rewarder.addActionToBucket(actionSet.get(i));
		}

		observer.setPDir(nextDirection);
		// Richtung wird zurückgegeben.
		return nextDirection;
	}

	@Override
	public String getGroupName() {
		return "Group 4 Pacman";
	}
}
