package game.entries.pacman.group4;

public class XCSObject implements IStarCSObject {
	String condition;
	String action;

	double prediction;
	double predictionError;
	double fitness;
	
	double specifity;

	public XCSObject(String condition, String action, double prediction,
			double predictionError, double fitness) {
		this.condition = condition;
		this.action = action;
		this.prediction = prediction;
		this.predictionError = predictionError;
		this.fitness = fitness;
		int count = 0;
		
		for(int i = 0; i< condition.length(); i++){
			if(condition.charAt(i)== '#'){
				i++;
			}
		}
		
		specifity = (condition.length()-count)/condition.length();
	}
	
	
	
	@Override
	public boolean equals(Object obj){
		if(obj == null || this == null){
			return false;
		}
		if(!(obj instanceof XCSObject)){
			return false;
		}
		XCSObject xCSObject = (XCSObject)obj;
		if(!xCSObject.action.equals(this.action)){
			return false;
		}
		if(!xCSObject.condition.equals(this.condition)){
			return false;
		}
		return true;
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
	public void update(double reward, double learningRate) {
		
		if(reward != reward){
			System.out.println("Aua");
			(new int[3])[3] = 1;
		}

		prediction = prediction + learningRate * (reward - prediction);
		if(prediction > 2000){
			prediction = 2000;
		}else if(prediction < -2000){
			prediction = -2000;
		}
		predictionError = predictionError + learningRate
				* (Math.abs(reward - prediction) - predictionError);
		fitness = fitness + learningRate
				* (1 / (0.00000001+predictionError) - fitness);

	}

	@Override
	public void payTax(double tax) {
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



	@Override
	public double getSpecifity() {
		return specifity;
	}



	@Override
	public double getPredictionError() {
		return predictionError;
	}
	
	@Override
	public String getCondition(){
		return condition;
	}
}
