package game.entries.pacman.group4;

import java.util.ArrayList;

public class ActionChooser implements IActionChooser {

	@Override
	public IStarCSObject getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {

		double averagePrediction = getAveragePrediction(matchingSet);
		double maxPrediction = Math.abs(averagePrediction); // Möchte gerne
															// positiven Reward
															// haben.
		int indexToAdd = -1;
		for (int i = 0; i < matchingSet.size(); i++) {
			double elementPrediction = matchingSet.get(i).getPrediction()
					* matchingSet.get(i).getSpecifity();
			if (elementPrediction >= maxPrediction) {
				maxPrediction = elementPrediction;
				indexToAdd = i;
			}
		}

		if (indexToAdd != -1) {
			actionSet.addAll(getSameActions(matchingSet.get(indexToAdd)
					.getAction(), matchingSet));
			matchingSetMinusActionSet.addAll(matchingSet);
			matchingSetMinusActionSet.removeAll(actionSet);
			return matchingSet.get(indexToAdd);

		} else {
			matchingSetMinusActionSet.addAll(matchingSet);
			return null;
		}

	}

	private ArrayList<IStarCSObject> getSameActions(String action,
			ArrayList<IStarCSObject> matchingSet) {
		ArrayList<IStarCSObject> sameActions = new ArrayList<IStarCSObject>();

		for (int i = matchingSet.size() - 1; i >= 0; i--) {
			if (matchingSet.get(i).getAction().equals(action)) {
				sameActions.add(matchingSet.get(i));
			}
		}

		return sameActions;
	}

	private double getAveragePrediction(ArrayList<IStarCSObject> classifierList) {
		double predictionSum = 0;
		for (int i = 0; i < classifierList.size(); i++) {
			predictionSum = predictionSum
					+ classifierList.get(i).getPrediction();
		}

		if (classifierList.size() > 0) {
			return predictionSum / classifierList.size();
		} else
			return 0.0;
	}

	@Override
	public int convertActionStringToDirectionInt(String action) {
		action = "0" + action;
		for (int i = 0; i < EnvironmentObserver.binaryDirections.length; i++) {
			if (action.equals(EnvironmentObserver.binaryDirections[i])) {
				return i - 1;
			}
		}
		return -1;
	}

}
