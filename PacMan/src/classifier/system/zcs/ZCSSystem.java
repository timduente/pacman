package classifier.system.zcs;

import java.util.HashMap;
import java.util.Map;

import classifier.IAction;
import classifier.IClassifierSystem;
import classifier.IZCSClassifierDataSource;

public class ZCSSystem implements IClassifierSystem {

	ZCSDatabase database = new ZCSDatabase();
	ZCSActionSet actionset_A_current = null;
	ZCSActionSet actionset_A_previous = null;

	@Override
	public void addData(IZCSClassifierDataSource dataSource) {
		for (ZCSEntry entry : dataSource.getSomeData()) {
			database.add(entry);
		}
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
		ZCSMatchSet matchset = database.getMatches(observationBits);
		
		// actionset bilden (logik s. methodenrumpf)
		ZCSActionSet actionset = actionSelection(matchset);
		
		// zeitablauf aktualisieren
		actionset_A_previous = actionset_A_current;
		actionset_A_current = actionset;

		// tatsaechliche action waehlen
		IAction result = actionset.getHighestFitnessAction();
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

	
	
	public static String INT2BinaryStr(int value){
		
		StringBuilder sb = new StringBuilder(32);
		final int mask = 1;
		
		for(int i=31;i>=0;i--){
			final int shiftErg = value >> i;
			if((shiftErg & mask) == mask)
				sb.append('1');
			else
				sb.append('0');
		}
		
		return sb.toString();
	}
	
	
}
