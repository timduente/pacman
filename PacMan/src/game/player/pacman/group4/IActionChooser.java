package game.player.pacman.group4;

import java.util.ArrayList;

public interface IActionChooser {

	/**
	 * Wählt aus einem MatchingSet nach bestimmten Größen die beste Aktion aus.
	 * Alle Classifier, die diese Aktion anbieten landen im actionSet. Alle
	 * anderen im matchingSetMinusActionSet.
	 * 
	 * @param matchingSet
	 *            Eingabe aller Matchings auf die gemachte Beobachtung.
	 * @param actionSet
	 *            Ausgabe aller Classifier, die die beste Aktion anbieten.
	 * @param matchingSetMinusActionSet
	 *            Ausgabe aller restlichen Classifier. Nötig für Taxes.
	 */

	public IStarCSObject getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet);

	/**
	 * Konvertiert die binärcodiert Aktion in eine Richtungsangabe, in die der
	 * Pacman gehen soll. (eventuell auch NOP möglich??? <- eher ineffektiv,
	 * aber könnte man ausprobieren)
	 * 
	 * @param action
	 *            binärcodierte Aktion
	 * @return Richtung, in die gegangen werden soll:
	 * 
	 *         <ul>
	 *         <li>
	 *         0: oben</li>
	 *         <li>
	 *         1: rechts</li>
	 *         <li>
	 *         2: unten</li>
	 *         <li>
	 *         3: links</li>
	 *         </ul>
	 */
	public int convertActionStringToDirectionInt(String action);

}
