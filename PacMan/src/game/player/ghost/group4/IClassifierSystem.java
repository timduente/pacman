package game.player.ghost.group4;

import game.player.ghost.group4.system.ZCSEntry;

public interface IClassifierSystem extends ICSDatabaseSource {

	
	// rewards previous actions (learning)
	void reward(int reward);
	
	// observation: true/false fuer einige conditions
	// previous reward: relative reward summand for previous actionset
	IAction getAction(int observationBits);
	
	// add some entries to database
	void addData(IClassifierDataSource dataSource);
	void addData(ZCSEntry entry);
	void removeClassifiersWithSmallerFitness(int threshold);
	
	void printSomeInfoToConsole();
}
