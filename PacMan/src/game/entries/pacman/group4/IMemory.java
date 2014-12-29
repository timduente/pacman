package game.entries.pacman.group4;

import java.util.ArrayList;

public interface IMemory {

	/**
	 * Speichert alle Classifier in einer Datei.
	 * 
	 * @param fileName
	 *            Dateiname unter dem die Classifier gespeichert werden.
	 */
	public void writeMemoryToFile(String fileName);

	/**
	 * Liest alle Classifier aus einer Datei ein.
	 * 
	 * @param fileName
	 *            Dateiname unter dem die Classifier gespeichert werden.
	 */
	public void readMemoryFromFile(String fileName);

	/**
	 * Gibt alle Matchings zu einer bestimmten Beobachtung zurück.
	 * 
	 * @param observation
	 *            binär codierte Beobachtung.
	 * @return Alle Classifier, die auf die Beobachtung passen.
	 */
	public ArrayList<IStarCSObject> getMatchings(String observation);

	/*
	 * Weitere Operationen update aller Fitnesswerte. etc...
	 */

}
