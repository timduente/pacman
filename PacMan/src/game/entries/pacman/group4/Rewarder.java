package game.entries.pacman.group4;

import java.util.ArrayList;

/**
 * Die Liste der Aktionen wird sortiert gehalten. Die neueste Aktion steht am
 * Anfang der Liste.
 * 
 * @author Tim Dünte
 * 
 */
public class Rewarder implements IRewarder {
	ArrayList<IStarCSObject> lastActionBucket = new ArrayList<IStarCSObject>();

	private final static double TAX = 0.1;
	private final static double lEARNING_RATE = 0.5;

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
	public void giveRewardToActions(double reward) {
		for (int i = 0; i < lastActionBucket.size(); i++) {
			lastActionBucket.get(i).update(reward, lEARNING_RATE);
		}
	}

	@Override
	public void payTaxesToRemainingClassifier(
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {
		for (int i = 0; i < matchingSetMinusActionSet.size(); i++) {
			matchingSetMinusActionSet.get(i).payTax(TAX);
		}
	}
}
