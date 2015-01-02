package game.entries.pacman.group4;

import java.util.ArrayList;

public class ActionChooser implements IActionChooser {

	// ArrayList<IStarCSObject> actions = new ArrayList<IStarCSObject>();
	// ArrayList<IStarCSObject> actionsWhichAreNotInActionSet = new
	// ArrayList<IStarCSObject>();
	@Override
	public void getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {
		
		double maxPrediction = 0.0;
		int indexToAdd = -1;
		for(int i = 0; i<matchingSet.size(); i++){
			double elementPrediction = matchingSet.get(i).getPrediction();
			if(elementPrediction >= maxPrediction){
				maxPrediction = elementPrediction;
				indexToAdd = i;		
			}
		}
		
		for(int i = 0; i< matchingSet.size(); i++){
			if( i == indexToAdd){
				actionSet.add(matchingSet.get(i));
			}else{
				matchingSetMinusActionSet.add(matchingSet.get(i));
			}
		}
	}

	@Override
	public int convertActionStringToDirectionInt(String action) {
		for (int i = 0; i < EnvironmentObserver.binaryDirections.length; i++) {
			if (action.equals(EnvironmentObserver.binaryDirections[i])) {
				return i - 1;
			}
		}
		return -1;
	}

}
