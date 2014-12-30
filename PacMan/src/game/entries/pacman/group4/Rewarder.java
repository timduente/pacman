package game.entries.pacman.group4;

import java.util.ArrayList;


/**Die Liste der Aktionen wird sortiert gehalten. Die neueste Aktion steht am Anfang der Liste.
 * 
 * @author Tim Dünte
 *
 */
public class Rewarder implements IRewarder {
	ArrayList<IStarCSObject> lastActionBucket = new ArrayList<IStarCSObject>();

	@Override
	public void addActionToBucket(IStarCSObject classifier) {
		lastActionBucket.add(0, classifier);
	}

	@Override
	public void removeAllActionsFromBucket() {
		for (int i = lastActionBucket.size() - 1; i > 0; i--) {
			lastActionBucket.remove(i);
		}
	}

	@Override
	public void giveRewardToActions(int reward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void payTaxesToRemainingClassifier(double taxes,
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {
		// TODO Auto-generated method stub
		
	}

}
