package classifier.ghosts;

import classifier.IAction;

//
// wrapper klasse um einfach die korrekten ghost daten weiterzuleiten
//
//0: oben
// 1: rechts
// 2: unten
// 3: links
public class GhostAction implements IAction {

	int[] dirs = null;

	public GhostAction(int[] ghostDirections) {
		dirs = ghostDirections;
	}

	@Override
	public int[] getActionBits() {
		return dirs;
	}

}
