package classifier.system.zcs;

import classifier.IAction;
import classifier.IObservation;

public class ZCSEntry {
	IObservation observation;
	IAction action;
	int fitness;
	
	public ZCSEntry(IObservation o, IAction a, int f)
	{
		observation = o;
		action = a;
		fitness = f;
	}
	
	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public IObservation getObservation() {
		return observation;
	}

	public IAction getAction() {
		return action;
	}
}
