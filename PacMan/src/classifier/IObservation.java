package classifier;

public interface IObservation {
	boolean matches(IObservation other);
	boolean matches(long observationBits);
	long getObservedConditions(); // bit=0 --> false, bit=1 --> true # max 64 conditions bei long-typ
	//long getWildcards(); // bit=0 --> no wildcard, bit=1 --> isWildcard (ONLY FOR DATABASE USE)
}
