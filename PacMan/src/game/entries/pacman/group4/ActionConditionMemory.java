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

		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);

		int objectCount = in.readInt();

		for (int i = 0; i < objectCount; i++) {
			try {
				IStarCSObject obj = (IStarCSObject) in.readObject();
				System.out.println("Condition: " + ((XCSObject)obj).condition);
				
				memory.add(obj);
				System.out.println("1 Objekt eingelesen");

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
		for(int i = 0; i< memory.size(); i++){
			IStarCSObject classifier = memory.get(i).compareToGivenObservation(observation);
			if(classifier != null){
				matchings.add(classifier);
			}
		}
		return matchings;
	}

	@Override
	public void addClassifier(IStarCSObject classifier) {
		memory.add(classifier);

	}

}
