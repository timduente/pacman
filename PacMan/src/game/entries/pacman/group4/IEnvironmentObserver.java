package game.entries.pacman.group4;

import game.core.Game;

public interface IEnvironmentObserver {

	/**
	 * Diese Methode wertet die Umwelt aus und gibt die Informationen als
	 * binärcodierten String zurück.
	 * 
	 * @param game
	 *            GameObject, über das Informationen abgefragte werden.
	 * @return String, der die den Zustand des Spiels binär repräsentiert. Dies
	 *         ist die Basis für das Matching der Classifier.
	 */
	public String getObservationFromCurrentGameState(Game game);

	/**
	 * Gibt Reward zurück. Dieser Reward berechnet sich aus den Veränderungen
	 * zum letzten Mal. Eventuell eine Pille eingesammelt macht 1 Reward.
	 * Powerpille eingesammelt 5 Reward. Geist mit Powerpille gefressen 3 Reward.
	 * 
	 * @param game GameObject, über das Informationen abgefragte werden.
	 * @param time vergangene Zeit
	 * @return Integer gibt den Reward an.
	 */
	public double getReward(Game game, long time);
	
	public void setPDir(int PDir);

}
