package game.player.ghost.group4.system;

import java.util.ArrayList;
import java.util.List;

public class ZCSDatabase {

	List<ZCSEntry> data = new ArrayList<ZCSEntry>();
	
	public void add(ZCSEntry entry) {
		data.add(entry);
	}
	
	public void add(List<ZCSEntry> entries) {
		data.addAll(entries);
	}
	
	public void clear() {
		data.clear();
	}
	
	public void remove(ZCSEntry entry) {
		data.remove(entry);
	}
	
	public List<ZCSEntry> getAllEntries() {
		return data;
	}
	
	public void getMatches(int observationBits, ZCSMatchSet dest) {
		
		dest.clear();
		
		for(ZCSEntry e : data) {
			
			//System.out.print("matching\n\t" + ZCSSystem.INT2BinaryStr(observationBits)  + "\n\t" + ZCSSystem.INT2BinaryStr(e.observation.getObservedConditions())
			//		+ " (entry " + e.action.getDescription() + ")\n\t" + ZCSSystem.INT2BinaryStr(e.observation.getWildcards()) + " (wildcards)");
			
			if(e.observation.matches(observationBits)) {
				dest.add(e);
				//System.out.println("\n success");
			} 
			//else
			//	System.out.println("\n no match");
		}
	}
}
