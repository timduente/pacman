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
	private final static double TAX = 0.05;
	private final static double lEARNING_RATE = 0.35;
	private final static double DISCOUNT_RATE = 0.71;
	
	ArrayList<ArrayList<IStarCSObject>> buckets;
	int bucketCount;
	
	public Rewarder(int bucketCount){
		buckets = new ArrayList<ArrayList<IStarCSObject>>(bucketCount);
		this.bucketCount = bucketCount;
		for(int i = 0; i< bucketCount; i++){
			buckets.add(new ArrayList<IStarCSObject>());
		}
	}

	@Override
	public void addActionToBucket(IStarCSObject classifier) {
		buckets.get(0).add(classifier);

	}

	@Override
	public void giveRewardToActions(double reward, double maximumPrediction) {
		reward = reward + DISCOUNT_RATE * maximumPrediction;
		for (int i = 0; i < buckets.size(); i++) {
			giveRewardToActionList(reward, buckets.get(i) );
			reward = reward * DISCOUNT_RATE;
//			if(reward < 0){
//				break;
//			}
		}
	}
	
	private void giveRewardToActionList(double reward, ArrayList<IStarCSObject> list){
		for (int i = 0; i < list.size(); i++) {
			list.get(i).update(reward /list.size(), lEARNING_RATE);
		}
	}

	@Override
	public void payTaxesToRemainingClassifier(
			ArrayList<IStarCSObject> matchingSetMinusActionSet) {
		for (int i = 0; i < matchingSetMinusActionSet.size(); i++) {
			matchingSetMinusActionSet.get(i).payTax(TAX);
		}
	}

	@Override
	public void moveBuckets() {
		buckets.remove(bucketCount -1);
		buckets.add(0,new ArrayList<IStarCSObject>());
	}
}
