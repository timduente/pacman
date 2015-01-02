package game.entries.pacman.group4;

public class XCSObject implements IStarCSObject {
	String condition;
	String action;

	double prediction;
	double predictionError;
	double fitness;

	public XCSObject(String condition, String action, double prediction,
			double predictionError, double fitness) {
		this.condition = condition;
		this.action = action;
		this.prediction = prediction;
		this.predictionError = predictionError;
		this.fitness = fitness;
	}

	@Override
	public IStarCSObject compareToGivenObservation(String observation) {
		if (observation.length() != condition.length()) {
			return null;
		}

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

	@Override
	public void update(int reward, double learningRate) {
		double lastPrediction = prediction;
		double lastPredictionError = predictionError;
		double lastFitness = fitness;

		prediction = lastPrediction + learningRate * (reward - lastPrediction);
		predictionError = lastPredictionError + learningRate
				* (Math.abs(reward - lastPrediction) - lastPredictionError);
		fitness = lastFitness + learningRate
				* (1 / (0.01+lastPredictionError) - lastFitness);

	}

	@Override
	public void payTax(double tax) {
		if (fitness > tax)
			fitness = fitness - tax;
	}

	@Override
	public double getPrediction() {
		return prediction;
	}

	@Override
	public double getFitness() {
		return fitness;
	}

	@Override
	public String toString() {
		return condition + "@" + action + "@" + prediction + "@"
				+ predictionError + "@" + fitness;
	}
}
