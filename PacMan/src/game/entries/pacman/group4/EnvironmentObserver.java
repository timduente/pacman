package game.entries.pacman.group4;

import game.core.Game;

public class EnvironmentObserver implements IEnvironmentObserver{
	
	public String getObservationFromCurrentGameState(Game game){
		/*Ideen für Informationen, die codiert werden sollten:
		 * - benachbarte Nodes vorhanden? (oben unten rechts links) 4 bit
		 * - Richtung in der die nächste Pille liegt. 2bit 00 oben 01 rechts 10 unten 11 links
		 * - Richtung in der die nächste Power Pille liegt. 2bit
		 * - Abstand zum nächsten Geist?
		 * - Abständer aller Geister?
		 * - Richtungen zu allen Geistern?
		 * 
		 */
		
		
		return null;
		
	}

	@Override
	public int getReward(Game game, long time) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	 * Methoden die die entsprechenden Funktionalitäten bereitstellen.
	 * 
	 */
	
	

}
