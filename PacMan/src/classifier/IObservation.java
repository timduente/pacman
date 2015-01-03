package classifier;

public interface IObservation {
	
	//
	// currently based on binary kram
	//
	
	
	boolean matches(IObservation other);
	boolean matches(int observationBits);
	int getObservedConditions(); // bit=0 --> false, bit=1 --> true # max 64 conditions bei long-typ
	int getWildcards(); // bit=0 --> no wildcard, bit=1 --> isWildcard (ONLY FOR DATABASE USE)
}
