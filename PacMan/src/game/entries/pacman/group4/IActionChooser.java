package game.entries.pacman.group4;

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

	public void getActionSetFor(ArrayList<IStarCSObject> matchingSet,
			ArrayList<IStarCSObject> actionSet,
			ArrayList<IStarCSObject> matchingSetMinusActionSet);

}
