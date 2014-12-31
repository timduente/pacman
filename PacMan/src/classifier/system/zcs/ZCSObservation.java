package classifier.system.zcs;

import classifier.IObservation;

public class ZCSObservation implements IObservation {

	private long observationBits = 0;
	private long wildcardBits = 0; // bit=0 --> no wildcard, bit=1 --> isWildcard (ONLY FOR DATABASE USE)

	public ZCSObservation(long observation, long wildcard) {
		observationBits = observation;
		wildcardBits = wildcard;
	}

	public long getObservedConditions() {
		return observationBits;
	}

	public boolean matches(long observationBits) {
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
}
