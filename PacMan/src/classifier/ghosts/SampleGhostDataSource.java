package classifier.ghosts;

import java.util.ArrayList;
import java.util.List;

import classifier.IAction;
import classifier.IObservation;
import classifier.IZCSClassifierDataSource;
import classifier.system.zcs.ZCSEntry;
import classifier.system.zcs.ZCSObservation;

public class SampleGhostDataSource implements IZCSClassifierDataSource {

	List<ZCSEntry> data = new ArrayList<ZCSEntry>();
	
	public SampleGhostDataSource() {
		
		// 0: oben
		// 1: rechts
		// 2: unten
		// 3: links
		
		
		
		// some sample data
		final long wildcardHopefullyAll1 = 0xFFFFFFFFFFFFFFFFL;
		IObservation tst0o = new ZCSObservation(0, wildcardHopefullyAll1);
		IAction tst0a = new GhostAction(new int[]{ 0, -1, 2, -3});
		ZCSEntry everything = new ZCSEntry(tst0o, tst0a, 9999);
		data.add(everything);
		
		
	}
	
	
	@Override
	public List<ZCSEntry> getSomeData() {
		return data;
	}

}
