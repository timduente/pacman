package game.entries.pacman.group4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ActionConditionMemory implements IMemory {
	private ArrayList<IStarCSObject> memory = new ArrayList<IStarCSObject>();
	int maximumSize;

	public ActionConditionMemory(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	@Override
	public void writeMemoryToFile(String fileName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(new File(fileName));
		ObjectOutputStream out = new ObjectOutputStream(fileOut);

		// Anzahl der Objekt wird geschrieben.
		out.writeInt(memory.size());

		for (int i = 0; i < memory.size(); i++) {
			out.writeObject(memory.get(i));
		}
		out.close();
		fileOut.close();
	}

	@Override
	public void readMemoryFromFile(String fileName) throws IOException {
		memory = new ArrayList<IStarCSObject>();

		File f = new File(fileName);

		if (!f.exists()) {
			return;
		}

		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);

		int objectCount = in.readInt();

		for (int i = 0; i < objectCount; i++) {
			try {

				IStarCSObject obj = (IStarCSObject) in.readObject();

				if (!Double.isNaN(obj.getFitness())) {
					memory.add(obj);
				}

			} catch (ClassNotFoundException e) {
				System.out.println("Klasse nicht gefunden!");
				e.printStackTrace();
				break;
			}
		}
		in.close();
		fileIn.close();
	}

	@Override
	public ArrayList<IStarCSObject> getMatchings(String observation) {
		ArrayList<IStarCSObject> matchings = new ArrayList<IStarCSObject>();
		for (int i = memory.size() - 1; i >= 0; i--) {
			//System.out.println("Classifier: " + memory.get(i));

//			if (memory.get(i).getFitness() <= 0.5 * calculatePopulationAverageFitness()) {
//				continue;
//			}
			IStarCSObject classifier = memory.get(i).compareToGivenObservation(
					observation);

			if (classifier != null) {
				matchings.add(classifier);
				System.out.println("Matching Classifier: "+ classifier);
			}
			
		}
		return matchings;
	}

	@Override
	public void addClassifier(IStarCSObject classifier) {
		
		if(memory.contains(classifier)){
//			for(int i = memory.size() - 1; i>= 0; i--){
//				IStarCSObject doublette = memory.get(i);
//				//memory.remove(i);			
//				memory.add(new XCSObject(doublette.getCondition(), doublette.getAction(), (doublette.getPrediction() + classifier.getPrediction())/2.0, (doublette.getPredictionError()  + classifier.getPredictionError())/2.0, (doublette.getFitness() + classifier.getFitness())/2.0 ));
//			}
		}else if (memory.size() > maximumSize) {
			System.out.println("Memory is Full");
			double fitness = calculatePopulationAverageFitness();

			System.out.println("Size before: " + memory.size());
			for (int i = memory.size() - 1; i > 0; i--) {
				if (memory.get(i).getFitness() < fitness * 0.5) {
					memory.remove(i);
				}
			}
			System.out.println("Size after: " + memory.size());
		} else {
			memory.add(classifier);
		}

	}

	@Override
	public IStarCSObject generateNewClassifierForObservation(String observation) {
		String condition = new String();
		String action = new String();
		double fitness = calculatePopulationAverageFitness();

		for (int i = 0; i < observation.length(); i++) {
			if (Math.random() < 0.01) {
				condition = condition + '#';
			} else {
				condition = condition + observation.charAt(i);
			}
		}
		// System.out.println("Generate new Classifier");

		// int[] indicies = getClassifierIndiciesWithHighestFitness();
		// IStarCSObject fitest = memory.get(indicies[0]);
		// IStarCSObject secondFitest = memory.get(indicies[1]);
		//
		// String action1 = fitest.getAction();
		// String action2 = fitest.getAction();
		//
		// String mutatedAction1 = new String();
		// String mutatedAction2 = new String();
		//
		// for (int i = 0; i < action1.length(); i++) {
		// if (Math.random() < 0.001) {
		// // Mutation Bit kippt um:
		// mutatedAction1 = mutatedAction1
		// + ((action1.charAt(i) == '1') ? '0' : '1');
		// }
		// if (Math.random() < 0.001) {
		// // Mutation Bit kippt um:
		// mutatedAction2 = mutatedAction2
		// + (action2.charAt(i) == '1' ? '0' : '1');
		// }
		// }

		action = EnvironmentObserver.binaryDirections[(int) (Math.random()
				* EnvironmentObserver.binaryDirections.length - 1) + 1];
		IStarCSObject obj = new XCSObject(condition, action.substring(1),
				fitness / 2.0, 1.0, fitness);
		this.addClassifier(obj);
		return obj;
	}

	private IStarCSObject getClassifierWithHighestFitness() {
		double maximumFitness = 0.0;
		int index = -1;
		for (int i = 0; i < memory.size(); i++) {
			if (maximumFitness <= memory.get(i).getFitness()) {
				maximumFitness = memory.get(i).getFitness();
				index = i;
			}
		}
		return memory.get(index);
	}

	private int[] getClassifierIndiciesWithHighestFitness() {
		double maximumFitness = 0.0;
		int[] indicies = new int[2];
		indicies[0] = -1;
		indicies[1] = 0;
		for (int i = 0; i < memory.size(); i++) {
			if (maximumFitness <= memory.get(i).getFitness()) {
				maximumFitness = memory.get(i).getFitness();
				indicies[1] = indicies[0];
				indicies[0] = i;
			}
		}
		return indicies;
	}

	private double calculatePopulationAverageFitness() {
		double fitnessSum = 0;
		for (int i = 0; i < memory.size(); i++) {
			fitnessSum = fitnessSum + memory.get(i).getFitness();
		}

		if (memory.size() > 0) {
			return fitnessSum / memory.size();
		} else
			return 20.0;

	}

	@Override
	public void printClassifier() {
		for(int i= 0; i<memory.size(); i++){
			System.out.println("Classifier: "+memory.get(i));
		}
		
	}

}
