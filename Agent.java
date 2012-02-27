import java.util.Random;

public class Agent {
	private double aspLevel;
	private double load;
	private double prevLoad;
	private double loadChange = 0.01;
	private double util;
	private double learningRate = 0.5;
	private boolean overThreshold = false;

	public Agent(Random generator, int numAgents) {
		aspLevel = 0;
		load = 2 * generator.nextDouble() / numAgents;
	}

	public double getLevel() {
		return aspLevel;
	}

	public double getLoad() {
		return load;
	}

	public double getUtil() {
		return util;
	}

	public void setUtility(double newUtil) {
		adjust(newUtil);
		util = newUtil;
	}
	
	public void adjust(double newUtil) {
		if (newUtil < aspLevel) {
			load = (load + prevLoad) / 2;
			if (!overThreshold) {
				loadChange /= 2;
				overThreshold = true;
			}
		}
		else {
			aspLevel = (learningRate * newUtil) + (1 - learningRate)* util;
			prevLoad = load;
			load += loadChange;
			overThreshold = false;
		}
	}
}