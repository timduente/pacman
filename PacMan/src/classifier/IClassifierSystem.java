package classifier;

public interface IClassifierSystem {

	void rewardAction(int reward, IAction a);
	IAction getAction(long observationBits); // true/false fuer einige conditions
	
	void addData(IZCSClassifierDataSource dataSource);
}
