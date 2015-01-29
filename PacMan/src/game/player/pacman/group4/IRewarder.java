package game.player.pacman.group4;

import java.util.ArrayList;

public interface IRewarder {

	/**
	 * Fügt dem bucket eine Aktion hinzu.
	 * 
	 * @param classifier
	 */
	public void addActionToBucket(IStarCSObject classifier);

	/**
	 * Allen Aktionen im Bucket wird der Reward zu Teil. Der Reward wird
	 * irgendwie aufgeteilt.
	 * 
	 * @param reward
	 *            entsprechender Reward für ein erreichtes Ziel.
	 * @param maximumPrediction maximale Prediction
	 */

	public void giveRewardToActions(double reward, double maximumPrediction);

	/**
	 * Alle Classifier, die nicht im ActionSet sind müssen Taxes bezahlen.
	 * 
	 * @param matchingSetMinusActionSet
	 *            Classifier, die abgewertet werden.
	 */
	public void payTaxesToRemainingClassifier(
			ArrayList<IStarCSObject> matchingSetMinusActionSet);

	/**
	 * Macht aus dem Bucket a[i] a[-1] usw.
	 */
	public void moveBuckets();
}
