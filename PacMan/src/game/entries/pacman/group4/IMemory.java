package game.entries.pacman.group4;

import java.io.IOException;
import java.util.ArrayList;

public interface IMemory {

	/**
	 * Speichert alle Classifier in einer Datei.
	 * 
	 * @param fileName
	 *            Dateiname unter dem die Classifier gespeichert werden.
	 */
	public void writeMemoryToFile(String fileName) throws IOException;

	/**
	 * Liest alle Classifier aus einer Datei ein.
	 * 
	 * @param fileName
	 *            Dateiname unter dem die Classifier gespeichert werden.
	 */
	public void readMemoryFromFile(String fileName) throws IOException;

	/**
	 * Gibt alle Matchings zu einer bestimmten Beobachtung zurück.
	 * 
	 * @param observation
	 *            binär codierte Beobachtung.
	 * @return Alle Classifier, die auf die Beobachtung passen.
	 */
	public ArrayList<IStarCSObject> getMatchings(String observation);

	/**
	 * Fügt einen Classifier zu der Memory Liste hinzu.
	 * 
	 * @param classifier
	 *            Classifier, der hinzugefügt wird.
	 */
	public void addClassifier(IStarCSObject classifier);
	
	public IStarCSObject generateNewClassifierForObservation(String observation);

	/*
	 * Weitere Operationen update aller Fitnesswerte. etc...
	 */

}
