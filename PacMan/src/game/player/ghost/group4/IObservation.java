package game.player.ghost.group4;

public interface IObservation {
	
	//
	// currently based on binary kram
	//
	
	long getUniqueObservationID(); // contains bits and observationbits --> currently only possible because #bits=32 and #wildcard=32
	
	boolean matches(IObservation other);
	boolean matches(int observationBits);
	int getBits(); // bit=0 --> false, bit=1 --> true # max 64 conditions bei long-typ
	int getWildcards(); // bit=0 --> no wildcard, bit=1 --> isWildcard (ONLY FOR DATABASE USE)
}
