package classifier.system.zcs;

import classifier.IObservation;

public class ZCSObservation implements IObservation {

	private int observationBits = 0;
	private int wildcardBits = 0; // bit=0 --> no wildcard, bit=1 --> isWildcard (ONLY FOR DATABASE USE)

	public ZCSObservation(int observation, int wildcard) {
		observationBits = observation;
		wildcardBits = wildcard;
	}
	
	public ZCSObservation(int observation) {
		this(observation, 0);
	}

	public int getObservedConditions() {
		return observationBits;
	}

	public boolean matches(int observationBits) {
		// TODO: denkweise korrekt?
		long a = getObservedConditions();
		long b = observationBits;
		
		// unterschiede finden:
		long xor = a ^ b;
		
		// wenn unterschiede 100% mit wildcard uebereinstimmen, ist match erfolgreich
		return (xor & wildcardBits) == xor;
		
	}

	public boolean matches(IObservation other) {
		return matches(other.getObservedConditions());
	}
	
	public ZCSObservation setWldCard(int w) {
		wildcardBits = w;
		return this;
	}
	
	public ZCSObservation setWldBit(int index, boolean isSet) {
		final int mask = 1 << index;
		
		if(isSet) {
			wildcardBits |= mask;
		} else {
			wildcardBits &= ~mask; // bitwise negotiation ~ 
		}
		
		return this;
	}
	
	public ZCSObservation setObsBit(int index, boolean isSet) {
		final int mask = 1 << index;
		
		if(isSet) {
			observationBits |= mask;
		} else {
			observationBits &= ~mask; // bitwise negotiation ~ 
		}
		
		return this;
	}
}
