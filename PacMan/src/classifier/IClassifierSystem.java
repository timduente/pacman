package classifier;

public interface IClassifierSystem {

	
	// observation: true/false fuer einige conditions
	// previous reward: relative reward summand for previous actionset
	IAction getAction(int observationBits, int previousReward);
	
	// add some entries to database
	void addData(IZCSClassifierDataSource dataSource);
}
