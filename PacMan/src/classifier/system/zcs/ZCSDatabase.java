package classifier.system.zcs;

import java.util.ArrayList;
import java.util.List;

public class ZCSDatabase {

	List<ZCSEntry> data = new ArrayList<ZCSEntry>();
	
	public void add(ZCSEntry entry) {
		data.add(entry);
	}
	
	public void clear() {
		data.clear();
	}
	
	public void remove(ZCSEntry entry) {
		data.remove(entry);
	}
	
	public ZCSMatchSet getMatches(long observationBits) {
		ZCSMatchSet erg = new ZCSMatchSet();
		erg.startSelection();
		
		for(ZCSEntry e : data) {
			if(e.observation.matches(observationBits)) {
				erg.add(e);
			}
		}
		
		return erg;
	}
}
