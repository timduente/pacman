package game.player.ghost.group4.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ZCSDatabase {

	// static values to find "global" solutions --> could have been achieved with "pseudo-communication"
	// asking other databases for their best classifiers with a given observation
	// TODO: currently not possible because observations are "ghost-dependent" and DO CONTAIN GHOST-DEPENDENT data
	
	
	Map<Long, ZCSEntry> mp = new HashMap<Long, ZCSEntry>();
	Stack<Long> tmpStack = new Stack<Long>();
	
	float avrgFitness = 0;

	public void add(ZCSEntry entry) {

		final long key = entry.getObservation().getUniqueObservationID();

		// when classifier already exits --> update if fitness is better!
		ZCSEntry current = mp.get(key);
		if (current != null && current.getAction().getActionBits() == entry.getAction().getActionBits()) {

			// update if better
			if (entry.getFitness() > current.getFitness()) {
				mp.put(key, entry);
			}

		} else {
			// insert if no classifier exists
			mp.put(key, entry);
		}

	}

	public void add(List<ZCSEntry> entries) {
		for (ZCSEntry e : entries) {
			add(e);
		}
	}

	public void clear() {
		mp.clear();
	}

	public void remove(ZCSEntry entry) {
		mp.remove(entry.getObservation().getUniqueObservationID());
	}

	public ZCSEntry[] getAllEntries() {
		ZCSEntry[] arr = new ZCSEntry[mp.values().size()];
		return mp.values().toArray(arr);
	}

	public void removeClassifiersWithSmallerFitness(int threshold) {

		tmpStack.clear();

		Iterator<ZCSEntry> coll = mp.values().iterator();
		while (coll.hasNext()) {
			ZCSEntry e = coll.next();
			if (e.getFitness() < threshold)
				tmpStack.push(e.getObservation().getUniqueObservationID());
		}

		while (!tmpStack.empty()) {
			mp.remove(tmpStack.pop());
		}

	}

	public float getMatches(int observationBits, List<ZCSEntry> dest) {

		dest.clear();
		
		int tmp = 0;
		final int count = mp.values().size();
		float totalFitness = 0;

		Iterator<ZCSEntry> coll = mp.values().iterator();
		while (coll.hasNext()) {
			ZCSEntry e = coll.next();
			tmp += e.getFitness();
			if (e.observation.matches(observationBits)) {
				dest.add(e);
				totalFitness += e.getFitness();
			}
		}
		
		// keep track with some monitoring info
		avrgFitness = tmp / count;
		
		return totalFitness;
	}
	
	public float getCurrentAverageFitness() {
		return avrgFitness;
	}
}
