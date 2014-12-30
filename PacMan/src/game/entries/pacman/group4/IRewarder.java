package game.entries.pacman.group4;

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

}
