package game.entries.pacman.group4;

import java.io.Serializable;

public interface IStarCSObject extends Serializable {
	/**
	 * Vergleicht die gegebene Beobachtung mit der Condition dieses Elements.
	 * Passt die Beobachtung zur Condition, dann wird das Object zurückgegeben,
	 * ansonsten null.
	 * 
	 * @param observation
	 *            Binär codierte Beobachtung
	 * @return dieses Objekt, wenn es eine Aktion für die gemachte Beobachtung
	 *         bereit stellt. Sonst null.
	 */
	public IStarCSObject compareToGivenObservation(String observation);
	
	/**Gibt die Aktion zurück.
	 * 
	 * @return Binärcodierte Aktion, die ausgeführt werden soll.
	 */
	public String getAction();

}
