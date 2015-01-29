package game.player.pacman.group4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class ActionConditionMemory implements IMemory {
	private static ArrayList<IStarCSObject> memory = new ArrayList<IStarCSObject>();
	private static IStarCSComperator comparator = new IStarCSComperator();
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
		System.out.println("ClassifierToRead: " + objectCount);

		int count = 0;

		for (int i = 0; i < objectCount; i++) {
			try {

				// game.entries.pacman.group4.IStarCSObject obj1 =
				// (game.entries.pacman.group4.IStarCSObject) in.readObject();
				//
				// XCSObject obj = new XCSObject(obj1.getCondition(),
				// obj1.getAction(), obj1.getPrediction(),
				// obj1.getPredictionError(), obj1.getFitness());

				IStarCSObject obj = (IStarCSObject) in.readObject();

				if (!Double.isNaN(obj.getFitness())) {
					memory.add(obj);
					count++;
				}

			} catch (ClassNotFoundException e) {
				System.out.println("Klasse nicht gefunden!");
				e.printStackTrace();
				break;
			}
		}
		System.out.println("Classifier added: " + count);
		Collections.sort(memory, comparator);
		this.printBestClassifier();
		in.close();
		fileIn.close();
	}

	@Override
	public ArrayList<IStarCSObject> getMatchings(String observation) {
		ArrayList<IStarCSObject> matchings = new ArrayList<IStarCSObject>();
		IStarCSObject classifier = new XCSObject(observation, "", 0.0, 0.0, 0.0);
		int index = Collections.binarySearch(memory, classifier, comparator);

		if (index < 0) {
			return matchings;
		}
		int indexToSearch = Math.max(0, index - 4);
		int endSearch = Math.min(memory.size(), index + 4);
		for (int i = indexToSearch; i < endSearch; i++) {
			classifier = memory.get(i);
			if (classifier.compareToGivenObservation(observation) != null) {
				matchings.add(classifier);
			}
		}
		return matchings;
	}

	@Override
	public void addClassifier(IStarCSObject classifier) {
		int index = Collections.binarySearch(memory, classifier, comparator);

		if (index < 0) {
			if (memory.size() > maximumSize) {
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
				int addIndex = Collections.binarySearch(memory, classifier,
						comparator);
				if (addIndex >= 0)
					memory.add(addIndex, classifier);
				else
					memory.add(-addIndex - 1, classifier);
			}
		}else{
			int indexToSearch = Math.max(0, index - 4);
			int endSearch = Math.min(memory.size(), indexToSearch + 10);
			
			for(int i = indexToSearch; i< endSearch; i++){
				if (classifier.equals(memory.get(i))) {
					memory.remove(i);
					memory.add(i, classifier);
				}
			}
			
		
			
		}
//		for(int i=0; i< memory.size()-1; i++){
//			if(comparator.compare(memory.get(i), memory.get(i+1)) > 0){
//				System.err.println("UNSORTED");
//			}
//		}
		
	}

	@Override
	public IStarCSObject generateNewClassifierForObservation(
			String observation, int[] possibleDirections) {
		String condition = new String();
		String action = new String();
		double fitness = calculatePopulationAverageFitness();
		condition = observation;
		action = EnvironmentObserver.binaryDirections[(int) (Math.random() * EnvironmentObserver.binaryDirections.length)];
		IStarCSObject obj = new XCSObject(condition, action,
				calculatePopulationAveragePrediction(),
				calculatePopulationAveragePredictionError(), fitness);
		this.addClassifier(obj);
		return obj;
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

	private double calculatePopulationAveragePrediction() {
		double predictionSum = 0;
		for (int i = 0; i < memory.size(); i++) {
			predictionSum = predictionSum + memory.get(i).getPrediction();
		}

		if (memory.size() > 0) {
			return predictionSum / memory.size();
		} else
			return 10.0;

	}

	private double calculatePopulationAveragePredictionError() {
		double predictionErrorSum = 0;
		for (int i = 0; i < memory.size(); i++) {
			predictionErrorSum = predictionErrorSum
					+ memory.get(i).getPredictionError();
		}

		if (memory.size() > 0) {
			return predictionErrorSum / memory.size();
		} else
			return 0.5;

	}

	@Override
	public void printClassifier() {
		for (int i = 0; i < memory.size(); i++) {
			System.out.println("Classifier: " + memory.get(i));
		}

	}

	// @Override
	public void printBestClassifier() {
		double average = this.calculatePopulationAveragePrediction();
		for (int i = 0; i < memory.size(); i++) {
			if (memory.get(i).getPrediction() > average)
				System.out.println("Classifier: " + memory.get(i));
		}

	}

}
