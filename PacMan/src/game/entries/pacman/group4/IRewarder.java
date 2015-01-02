package game.entries.pacman.group4;

import java.util.ArrayList;

public interface IRewarder {

	/**
	 * Fügt dem bucket eine Aktion hinzu.
	 * 
	 * @param classifier
	 */
	public void addActionToBucket(IStarCSObject classifier);

	/**
	 * Löscht alle Aktionen aus dem Bucket.
	 * 
	 */
	public void removeAllActionsFromBucket();

	/**
	 * Allen Aktionen im Bucket wird der Reward zu Teil. Der Reward wird
	 * irgendwie aufgeteilt.
	 * 
	 * @param reward
	 *            entsprechender Reward für ein erreichtes Ziel.
	 */
	public void giveRewardToActions(int reward);

	/**
	 * Alle Classifier, die nicht im ActionSet sind müssen Taxes bezahlen.
	 * 
	 * @param matchingSetMinusActionSet
	 *            Classifier, die abgewertet werden.
	 */
	public void payTaxesToRemainingClassifier(
			ArrayList<IStarCSObject> matchingSetMinusActionSet);

}
