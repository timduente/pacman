package game.player.ghost.group4.system;

import game.player.ghost.group4.IAction;

import java.util.LinkedList;
import java.util.List;

public class ZCSActionSet {
	List<ZCSEntry> actions = new LinkedList<ZCSEntry>();
	
	
	public void add(ZCSEntry e) {
		actions.add(e);
	}
	
	public List<ZCSEntry> getActions() {
		return actions;
	}
	
	public IAction testGetFirstAction() {
		
		if(actions.size() > 0)
			return actions.get(0).action;
		
		return null;
	}
	
	public int size() {
		return actions.size();
	}
	
	public void reward(float deltaReward) {
		for(ZCSEntry e : actions) {
			e.setFitness(e.getFitness() + deltaReward);
		}
	}
	
	public IAction getHighestFitnessAction() {
		
		float curmaxFitness = Float.MIN_VALUE;
		IAction erg = null;
		
		for(ZCSEntry e : actions) {
			if(e.getFitness() >= curmaxFitness) {
				erg = e.getAction();
				curmaxFitness = e.getFitness();
			}
		}
		
		return erg;
	}
}
