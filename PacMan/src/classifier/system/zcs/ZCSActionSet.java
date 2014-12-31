package classifier.system.zcs;

import java.util.LinkedList;
import java.util.List;

import classifier.IAction;

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
}
