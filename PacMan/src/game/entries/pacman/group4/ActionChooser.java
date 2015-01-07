package game.entries.pacman.group4;

import java.util.ArrayList;

public class ActionChooser implements IActionChooser {

	// ArrayList<IStarCSObject> actions = new ArrayList<IStarCSObject>();
	// ArrayList<IStarCSObject> actionsWhichAreNotInActionSet = new
	// ArrayList<IStarCSObject>();
	@Override
	public IStarCSObject getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {

//		ArrayList<ArrayList<IStarCSObject>> possibleActionSets = new ArrayList<ArrayList<IStarCSObject>>(
//				EnvironmentObserver.binaryDirections.length);
//
//		double maxPrediction = 0.0;
//		int indexToAdd = -1;
//
//		for (int i = 0; i < EnvironmentObserver.binaryDirections.length; i++) {
//			possibleActionSets.add(getSameActions(
//					EnvironmentObserver.binaryDirections[i], matchingSet));
//			double actualPrediction = 0.0;
//			for (int j = 0; j < possibleActionSets.get(i).size(); j++) {
//				actualPrediction = actualPrediction
//						+ possibleActionSets.get(i).get(j).getPrediction();
//			}
//			if (possibleActionSets.get(i).size() != 0) {
//				actualPrediction = actualPrediction
//						/ possibleActionSets.get(i).size();
//			}
//			if (actualPrediction > maxPrediction) {
//				maxPrediction = actualPrediction;
//				indexToAdd = i;
//			}
//
//		}

		double maxPrediction = 0.1;
		int indexToAdd = -1;
		for (int i = 0; i < matchingSet.size(); i++) {
			double elementPrediction = matchingSet.get(i).getPrediction() * matchingSet.get(i).getSpecifity();
			if (elementPrediction >= maxPrediction) {
				maxPrediction = elementPrediction;
				indexToAdd = i;
			}
		}
		
//		System.out.println("ACHTUNG: " + indexToAdd);
//		System.out.println("Prediction: "+ maxPrediction);
		
//		if (maxPrediction <= 0.001) {
//
//			indexToAdd = -1;
//
//		}
		
		
		
		if(indexToAdd != -1){
			actionSet.addAll(getSameActions(matchingSet.get(indexToAdd).getAction(), matchingSet));
			matchingSetMinusActionSet.addAll(matchingSet);
			matchingSetMinusActionSet.removeAll(actionSet);
				
		}else{
			matchingSetMinusActionSet.addAll(matchingSet);
		}
		
		



//		for (int i = 0; i < EnvironmentObserver.binaryDirections.length; i++) {
//			if (indexToAdd == i) {
//				actionSet.addAll(possibleActionSets.get(i));
//			} else {
//				matchingSetMinusActionSet.addAll(possibleActionSets.get(i));
//			}
//		}

		// for (int i = 0; i < matchingSet.size(); i++) {
		// if (i == indexToAdd) {
		// actionSet.add(matchingSet.get(i));
		// } else {
		// matchingSetMinusActionSet.add(matchingSet.get(i));
		// }
		// }
		if(indexToAdd != -1)
			return matchingSet.get(indexToAdd);
		else
			return null;
	}

	private ArrayList<IStarCSObject> getSameActions(String action,
			ArrayList<IStarCSObject> matchingSet) {
		ArrayList<IStarCSObject> sameActions = new ArrayList<IStarCSObject>();
		System.out.println("Action: "+ action);

		for (int i = matchingSet.size() - 1; i >= 0; i--) {
			if (matchingSet.get(i).getAction().equals(action)) {
				sameActions.add(matchingSet.get(i));
			}
		}

		return sameActions;
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
