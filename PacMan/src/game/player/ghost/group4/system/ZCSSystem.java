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
import java.util.Random;

public class ZCSSystem implements IClassifierSystem {

	static final int LEARNING_HISTORY_LENGTH = 15;
	static final float LEARNING_FACTOR_BETA = 1f;
	static final float LEARNING_FACTOR_GAMMA = 0.71f;
	static final double COVER_TRIGGER = 0.5;
	static final double GENETIC_RATE = 0.15;
	static final float TAX_PAYOFF = 0.1f;
	static final float DEFAULT_RANDOM_FITNESS = 20;

	ZCSDatabase database = new ZCSDatabase();
	List<ZCSEntry> matchset = new LinkedList<ZCSEntry>(); // objekt kann an sich ja wiederverwendet werden, da nur liste
	LinkedList<ZCSActionSet> previousActionsets = new LinkedList<ZCSActionSet>();
	IClassifierGenerator classifierGenerator = null;
	Random rnd = new Random();

	Map<IAction, Float> mapActionToFitness = new HashMap<IAction, Float>(); // temporary map for action selection

	public ZCSSystem(IClassifierGenerator gen) {
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

		Iterator<ZCSActionSet> prevIterator = previousActionsets.iterator();

		if (!prevIterator.hasNext()) {
			return;
		}

		ZCSActionSet A = prevIterator.next();
		float A_reward = (reward * LEARNING_FACTOR_BETA) / A.size();
		A.reward(A_reward);

		// TODO bestrafe alle eintraege in matchset, die nicht ins actionset gekommen sind

		float currentBucketContent = 0;
		ZCSActionSet bucketSource = A;

		while (prevIterator.hasNext()) {

			for (ZCSEntry ac : bucketSource.getActions()) {
				currentBucketContent += ac.fitness;
			}
			currentBucketContent *= LEARNING_FACTOR_BETA * LEARNING_FACTOR_GAMMA;

			ZCSActionSet bucketDest = prevIterator.next();
			float current_reward = currentBucketContent / A.size();
			bucketDest.reward(current_reward);

			// ZCSActionSet a = prevIterator.next();
			// a.reward((int) curRew);
			//
			// curRew *= LEARNING_SLOWDOWN_FACTOR;
			//
			currentBucketContent = 0; // clear for next reward-run
		}

	}

	@Override
	public IAction getAction(int observationBits) {

		// matchset bilden
		final float matchsetCombinedFitness = database.getMatches(observationBits, matchset);
		
		if(matchsetCombinedFitness < (COVER_TRIGGER * database.getCurrentAverageFitness())) {
			// use covering when matchset is "poor"
			addData(RANDOM_covering(observationBits));
		}

		// actionset bilden (logik s. methodenrumpf)
		ZCSActionSet actionset = actionSelection(matchset);

		// zeitablauf aktualisieren
		if (previousActionsets.size() > LEARNING_HISTORY_LENGTH) {
			ZCSActionSet lastASet = previousActionsets.removeLast();
			if (lastASet != null)
				lastASet.getActions().clear(); // clear some memory
		}
		previousActionsets.addFirst(actionset);

		// tatsaechliche action waehlen
		IAction result = actionset.getHighestFitnessAction();

		// generieren von classifiern, wenn nichts gefunden
		if (result == null) {
			// assumption: total actionset is empty (see implementation)
			// --> try use matchset to get some good generation directions
			ZCSEntry generated = generateNewClassifier(observationBits, matchset);
			result = generated.getAction();
			addData(generated);
		}

		// sometimes make random genectic
		if (matchset.size() >= 2 && rnd.nextDouble() < GENETIC_RATE) {
			addData(GA_covering(observationBits, matchset));
		}

		return result;
	}

	private ZCSActionSet actionSelection(List<ZCSEntry> m) {

		// pro action (iwwi eindeutig hoffentlich ..) wird eine gesamtsumme
		// der fitnesswerte gebildet, um dann die am besten geeignete action zu finden
		// TODO: die action-objekte/-zeiger muessen eindeutig sein

		float curMaximum = Integer.MIN_VALUE;
		IAction curMaximumAction = null;

		mapActionToFitness.clear();

		for (ZCSEntry e : m) {
			IAction curAction = e.getAction();

			// fitnesswerte
			float curValue = mapActionToFitness.containsKey(curAction) ? mapActionToFitness.get(curAction) : 0;
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
			if (e.getAction() == curMaximumAction) {
				erg.add(e);
				// m.remove(e); // remove from matchset because negative reward will be applied to non-chosen entries
			} else {
				// TODO "pay taxes to non-selected entries (before reward-method .......)
				e.setFitness(e.getFitness() - TAX_PAYOFF);
			}
		}

		return erg;
	}

	public ZCSEntry[] getClassifierDatabase() {
		return database.getAllEntries();
	}

	public ZCSEntry generateNewClassifier(int observationBits, List<ZCSEntry> tips) {

		ZCSEntry generated = null; // MUSTNT be null at return point !!!!!!!!

		if (tips.size() < 2) {
			// there are no "anhaltspunkte" --> random generation
			generated = RANDOM_covering(observationBits);

		} else {
			// try use previous knowledge
			generated = GA_covering(observationBits, tips);
		}

		return generated;
	}
	
	private ZCSEntry RANDOM_covering(int observationBits) {
		// there are no "anhaltspunkte" --> random generation
		return classifierGenerator.generateRandomClassifierForObservation(observationBits, DEFAULT_RANDOM_FITNESS);// database.getCurrentAverageFitness());
	}

	private ZCSEntry GA_covering(int observationBits, List<ZCSEntry> tips) {
		// startvalues
		ZCSEntry bestA = tips.get(0);
		ZCSEntry bestB = tips.get(1);

		// search best ones
		ZCSEntry[] src = database.getAllEntries();

		for (ZCSEntry e : src) {

			if (e.getFitness() > bestA.getFitness()) {
				bestB = bestA;
				bestA = e;
			} else {
				if (e.getFitness() > bestB.getFitness()) {
					bestB = e;
				}
			}
		}

		return classifierGenerator.generateGeneticClassifier(observationBits, bestA, bestB);
	}

	@Override
	public void printSomeInfoToConsole() {

		float minFitness = Integer.MAX_VALUE;
		float maxFitness = Integer.MIN_VALUE;
		int fitnessSum = 0;
		ZCSEntry[] data = database.getAllEntries();

		for (int i = 0; i < data.length; ++i) {
			ZCSEntry e = data[i];
			fitnessSum += e.fitness;
			minFitness = Math.min(minFitness, e.fitness);
			maxFitness = Math.max(maxFitness, e.fitness);
		}

		float avrgFitness = fitnessSum / (float) data.length;

		System.out.println("minf: " + minFitness + " # maxf: " + maxFitness + " # avrg: " + avrgFitness + " # classifiers: " + data.length);
	}

	@Override
	public void removeClassifiersWithSmallerFitness(int threshold) {
		database.removeClassifiersWithSmallerFitness(threshold);
	}

}
