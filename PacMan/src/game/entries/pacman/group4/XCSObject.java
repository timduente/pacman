package game.entries.pacman.group4;


public class XCSObject implements IStarCSObject{
	String condition;
	String action;

	double prediction;
	double predictionError;
	int fitness;

	public XCSObject(String condition, String action, double prediction,
			double predictionError, int fitness) {
		this.condition = condition;
		this.action = action;
		this.prediction = prediction;
		this.predictionError = predictionError;
		this.fitness = fitness;
	}

	@Override
	public IStarCSObject compareToGivenObservation(String observation) {
		for (int i = 0; i < observation.length(); i++) {
			if (this.condition.charAt(i) == '0' && observation.charAt(i) == '1'
					|| this.condition.charAt(i) == '1'
					&& observation.charAt(i) == '0') {
				return null;
			}
		}
		return this;
	}

	@Override
	public String getAction() {
		return action;
	}

}
