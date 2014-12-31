package classifier.system.zcs;

import classifier.IAction;
import classifier.IClassifierSystem;
import classifier.IZCSClassifierDataSource;

public class ZCSSystem implements IClassifierSystem {

	ZCSDatabase database = new ZCSDatabase();
	
	
	@Override
	public void addData(IZCSClassifierDataSource dataSource) {
		for(ZCSEntry entry :dataSource.getSomeData()) {
			database.add(entry);			
		}
	}
	
	@Override
	public void rewardAction(int reward, IAction a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAction getAction(long observationBits) {
		
		ZCSMatchSet matchset = database.getMatches(observationBits);
		ZCSActionSet actionset = actionSelection(matchset);
		
		// TODO: select one action
		IAction result = actionset.testGetFirstAction();
		
		if(result == null)
			System.out.println("action selection == null");
		return result;
	}

	
	
	private ZCSActionSet actionSelection(ZCSMatchSet m) {
		ZCSActionSet erg = new ZCSActionSet();
		
		// TODO: select correct matches .. depending on ?
		for(ZCSEntry e : m.getAllMatches()) {
			erg.add(e);
		}
		
		return erg;
	}

	
}







