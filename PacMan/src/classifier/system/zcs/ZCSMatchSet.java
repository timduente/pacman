package classifier.system.zcs;

import java.util.LinkedList;
import java.util.List;

public class ZCSMatchSet {

	List<ZCSEntry> matches = new LinkedList<ZCSEntry>();
	
	
	public void startSelection() {
		matches.clear();
	}
	
	public void add(ZCSEntry entry) {
		matches.add(entry);
	}

	public List<ZCSEntry> getAllMatches() {
		return matches;
	}
	
	// TODO: get specific matches satisfying some conditions

}
