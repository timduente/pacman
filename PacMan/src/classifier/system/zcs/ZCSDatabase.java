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
	
	public ZCSMatchSet getMatches(int observationBits) {
		ZCSMatchSet erg = new ZCSMatchSet();
		
		for(ZCSEntry e : data) {
			
			//System.out.print("matching\n\t" + ZCSSystem.INT2BinaryStr(observationBits)  + "\n\t" + ZCSSystem.INT2BinaryStr(e.observation.getObservedConditions())
			//		+ " (entry " + e.action.getDescription() + ")\n\t" + ZCSSystem.INT2BinaryStr(e.observation.getWildcards()) + " (wildcards)");
			
			if(e.observation.matches(observationBits)) {
				erg.add(e);
				//System.out.println("\n success");
			} 
			//else
			//	System.out.println("\n no match");
		}
		
		return erg;
	}
}
