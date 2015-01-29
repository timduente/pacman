package game.player.pacman.group4;

import java.util.ArrayList;

public class ActionChooser implements IActionChooser {

	@Override
	public IStarCSObject getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {

		double maxPrediction =  0.0;//Math.abs(averagePrediction); // Möchte gerne
															// positiven Reward
															// haben.
		int indexToAdd = -1;
		IStarCSObject classifier;
		for (int i = 0; i < matchingSet.size(); i++) {
			classifier = matchingSet.get(i);
			double elementPrediction = classifier.getPrediction() ;
			if (elementPrediction > maxPrediction) {
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

	@Override
	public int convertActionStringToDirectionInt(String action) {
		for (int i = 0; i < EnvironmentObserver.binaryDirections.length; i++) {
			if (action.equals(EnvironmentObserver.binaryDirections[i])) {
				return i;
			}
		}
		return -1;
	}

}
