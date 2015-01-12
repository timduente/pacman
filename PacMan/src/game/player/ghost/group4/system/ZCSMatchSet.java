package game.player.ghost.group4.system;

import java.util.LinkedList;
import java.util.List;

public class ZCSMatchSet {

	List<ZCSEntry> matches = new LinkedList<ZCSEntry>();
	
	public void add(ZCSEntry entry) {
		matches.add(entry);
	}

	public List<ZCSEntry> getAllMatches() {
		return matches;
	}
}
