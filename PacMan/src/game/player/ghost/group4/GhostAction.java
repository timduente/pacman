package game.player.ghost.group4;


//
// wrapper klasse um einfach die korrekten ghost daten weiterzuleiten
//
//0: oben
// 1: rechts
// 2: unten
// 3: links
public class GhostAction implements IAction {

	int dir = -1;

	public GhostAction(int ghostDirection) {
		dir = ghostDirection;
	}

	@Override
	public int getActionBits() {
		return dir;
	}

	@Override
	public String getDescription() {
		if(dir == 0)
			return "upwards";
		else if(dir == 1)
			return "right";
		else if(dir == 2)
			return "downwards";
		else if(dir == 3)
			return "left";
		
		return "previous action";
	}

}
