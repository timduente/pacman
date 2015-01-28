package game.player.ghost.group4.system;

import game.player.ghost.group4.IAction;
import game.player.ghost.group4.IObservation;

public class ZCSEntry {
	IObservation observation;
	IAction action;
	float fitness;
	
	public ZCSEntry(IObservation o, IAction a, float f)
	{
		observation = o;
		action = a;
		fitness = f;
	}
	
	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public IObservation getObservation() {
		return observation;
	}

	public IAction getAction() {
		return action;
	}
}
