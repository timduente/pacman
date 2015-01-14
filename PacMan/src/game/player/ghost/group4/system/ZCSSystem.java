package game.player.ghost.group4.system;

import game.player.ghost.group4.IAction;
import game.player.ghost.group4.IClassifierGenerator;
import game.player.ghost.group4.IClassifierSystem;
import game.player.ghost.group4.IClassifierDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZCSSystem implements IClassifierSystem {

	ZCSDatabase database = new ZCSDatabase();
	ZCSActionSet actionset_A_current = null;
	ZCSActionSet actionset_A_previous = null; // TODO: dynamisch viele zulassen --> parametereinstellungen aus vorlesung
	ZCSMatchSet matchset = new ZCSMatchSet(); // objekt kann an sich ja wiederverwendet werden, da nur liste
	
	IClassifierGenerator classifierGenerator = null;
	static final int DEFAULT_RANDOM_FITNESS = 50;
	
	public ZCSSystem(IClassifierGenerator gen){
		classifierGenerator = gen;
	}
	

	@Override
	public void addData(IClassifierDataSource dataSource) {
		for (ZCSEntry entry : dataSource.getSomeData()) {
			addData(entry);
		}
	}
	
	@Override
	public void addData(ZCSEntry entry) {
		database.add(entry);
	}

	private void rewardActions(int reward) {

		// einfach reward auf aktuelles actionset anwenden (weitere zeitschritte waeren moeglich)
		if(actionset_A_current != null)
			actionset_A_current.reward(reward);
		
		// zeitschritt davor auch noch bewerten
		if(actionset_A_previous != null)
			actionset_A_previous.reward(reward / 2);
	}

	@Override
	public IAction getAction(int observationBits, int previousReward) {
		
		// reward previous selection
		rewardActions(previousReward);

		// matchset bilden
		database.getMatches(observationBits, matchset);
		
		// actionset bilden (logik s. methodenrumpf)
		ZCSActionSet actionset = actionSelection(matchset);
		
		// zeitablauf aktualisieren
		actionset_A_previous = actionset_A_current;
		actionset_A_current = actionset;

		// tatsaechliche action waehlen
		IAction result = actionset.getHighestFitnessAction();
		
		
		// generieren von classifiern, wenn nichts gefunden
		if(result == null) {
			// TODO: generation
			
			ZCSEntry newEntry = classifierGenerator.generateRandomClassifierForObservation(observationBits, DEFAULT_RANDOM_FITNESS);
			addData(newEntry);
			result = newEntry.getAction();
		}
		
		
		return result;
	}

	private ZCSActionSet actionSelection(ZCSMatchSet m) {

		// pro action (iwwi eindeutig hoffentlich ..) wird eine gesamtsumme
		// der fitnesswerte gebildet, um dann die am besten geeignete action zu finden
		// TODO: die action-objekte/-zeiger muessen eindeutig sein
		Map<IAction, Integer> mapActionToFitness = new HashMap<IAction, Integer>();
		int curMaximum = Integer.MIN_VALUE;
		IAction curMaximumAction = null;

		for (ZCSEntry e : m.getAllMatches()) {
			IAction curAction = e.getAction();

			// fitnesswerte
			int curValue = mapActionToFitness.containsKey(curAction) ? mapActionToFitness.get(curAction) : 0;
			curValue += e.getFitness();
			mapActionToFitness.put(curAction, curValue); // aktuellen wert aufaddieren

			// beste selektion gleich mit-aktualisieren
			if (curValue >= curMaximum) {
				curMaximum = curValue;
				curMaximumAction = curAction;
			}
		}

		// find all classifiers with "best action" and add to actionset
		ZCSActionSet erg = new ZCSActionSet();

		for (ZCSEntry e : m.getAllMatches()) {
			if(e.getAction() == curMaximumAction)
				erg.add(e);
		}

//		// ALT: einfach alle testweise
//		for (ZCSEntry e : m.getAllMatches()) {
//			erg.add(e);
//		}

		return erg;
	}


	@Override
	public List<ZCSEntry> getClassifierDatabase() {
		return database.getAllEntries();
	}
	
	public IAction generateAndRegisterNewClassifier(int observationBits, IAction lastAction) {
		
		
		return null;
	}
	
	
}
