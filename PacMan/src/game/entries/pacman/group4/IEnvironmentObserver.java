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

}
