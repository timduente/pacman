package game.player.pacman.group4;

import java.util.Comparator;

public class IStarCSComperator implements Comparator<IStarCSObject>{

	@Override
	public int compare(IStarCSObject arg0, IStarCSObject arg1) {
		return arg0.getCondition().compareTo(arg1.getCondition());
	}

}
