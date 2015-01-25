package game.player.ghost.group4.system;

import game.player.ghost.group4.IAction;
import game.player.ghost.group4.IClassifierGenerator;
import game.player.ghost.group4.IClassifierSystem;
import game.player.ghost.group4.IClassifierDataSource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ZCSSystem implements IClassifierSystem {

	static final int LEARNING_HISTORY_LENGTH = 20;
	static final double LEARNING_SLOWDOWN_FACTOR = 0.7;
	
	
	ZCSDatabase database = new ZCSDatabase();
	List<ZCSEntry> matchset = new LinkedList<ZCSEntry>(); // objekt kann an sich ja wiederverwendet werden, da nur liste
	
	LinkedList<ZCSActionSet> previousActionsets = new LinkedList<ZCSActionSet>();
	
	IClassifierGenerator classifierGenerator = null;
	static final int DEFAULT_RANDOM_FITNESS = 80;
	
	Map<IAction, Integer> mapActionToFitness = new HashMap<IAction, Integer>(); // temporary map for action selection
	
	public ZCSSystem(IClassifierGenerator gen){
		classifierGenerator = gen;
	}
	

	@Override
	public void addData(IClassifierDataSource dataSource) {
		database.add(dataSource.getSomeData());
	}
	
	@Override
	public void addData(ZCSEntry entry) {
		database.add(entry);
	}

	public void reward(int reward) {

		double curRew = reward;
		Iterator<ZCSActionSet> prevIterator = previousActionsets.iterator();
		while(prevIterator.hasNext()) {
			ZCSActionSet a = prevIterator.next();
			a.reward((int) curRew);
			
			curRew *= LEARNING_SLOWDOWN_FACTOR;
		}
		
	}

	@Override
	public IAction getAction(int observationBits) {
		
		// matchset bilden
		database.getMatches(observationBits, matchset);
		
		// actionset bilden (logik s. methodenrumpf)
		ZCSActionSet actionset = actionSelection(matchset);
		
		// zeitablauf aktualisieren
		if(previousActionsets.size() > LEARNING_HISTORY_LENGTH) {
			previousActionsets.removeLast();
		}
		previousActionsets.addFirst(actionset);
		
		// tatsaechliche action waehlen
		IAction result = actionset.getHighestFitnessAction();
		
		// generieren von classifiern, wenn nichts gefunden
		if(result == null) {
			// assumption: total actionset is empty (see implementation)
			//             --> try use matchset to get some good generation directions
			ZCSEntry generated = generateNewClassifier(observationBits, matchset);
			result = generated.getAction();
			addData(generated);
		}
		
		return result;
	}

	private ZCSActionSet actionSelection(List<ZCSEntry> m) {

		// pro action (iwwi eindeutig hoffentlich ..) wird eine gesamtsumme
		// der fitnesswerte gebildet, um dann die am besten geeignete action zu finden
		// TODO: die action-objekte/-zeiger muessen eindeutig sein
		
		int curMaximum = Integer.MIN_VALUE;
		IAction curMaximumAction = null;
		
		mapActionToFitness.clear();

		for (ZCSEntry e : m) {
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

		for (ZCSEntry e : m) {
			if(e.getAction() == curMaximumAction)
				erg.add(e);
		}
		
		return erg;
	}


	public ZCSEntry[] getClassifierDatabase() {
		return database.getAllEntries();
	}
	
	public ZCSEntry generateNewClassifier(int observationBits, List<ZCSEntry> tips) {

		ZCSEntry generated = null; // MUSTNT be null at return point !!!!!!!!
		
		if(tips.size() < 2) {
			// there are no "anhaltspunkte" --> random generation
			generated = classifierGenerator.generateRandomClassifierForObservation(observationBits, database.getCurrentAverageFitness());
			
		} else {
			// try use previous knowledge
			
			// startvalues
			ZCSEntry bestA = tips.get(0);
			ZCSEntry bestB = tips.get(1);
			
			// search best ones
			ZCSEntry[] src = database.getAllEntries();
			
			for(ZCSEntry e : src) {
				
				if(e.getFitness() > bestA.getFitness()) {
					bestB = bestA;
					bestA = e;
				} else {
					if(e.getFitness() > bestB.getFitness()) {
						bestB = e;
					}
				}
			}
			
			generated = classifierGenerator.generateGeneticClassifier(observationBits, bestA, bestB);
			
		}

		return generated;
	}


	@Override
	public void printSomeInfoToConsole() {
		
		int minFitness = Integer.MAX_VALUE;
		int maxFitness = Integer.MIN_VALUE;
		int fitnessSum = 0;
		ZCSEntry[] data = database.getAllEntries();
		
		
		for (int i=0;i<data.length;++i) {
			ZCSEntry e = data[i];
			fitnessSum += e.fitness;
			minFitness = Math.min(minFitness, e.fitness);
			maxFitness = Math.max(maxFitness, e.fitness);
		}
		
		double avrgFitness = fitnessSum / (double) data.length;
		
		
		System.out.println("minf: "  + minFitness + " # maxf: " + maxFitness + " # avrg: " + avrgFitness + " # classifiers: " + data.length);
	}


	@Override
	public void removeClassifiersWithSmallerFitness(int threshold) {
		database.removeClassifiersWithSmallerFitness(threshold);
	}
	
	
}
