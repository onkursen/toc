import java.util.Random;

class Agent {
  private static final double LEARNING_RATE = 0.5;

  private double aspLevel;
  private double load;
  private double loadChange;
  private double previousLoad;
  private boolean overThreshold;
  private double util;

  public Agent(Random generator, int numAgents) {
    aspLevel = 0;
    load = 2 * generator.nextDouble() / numAgents;
    loadChange = 0.01;
    overThreshold = false;
  }

  public double getLoad() {
    return load;
  }

  public double getUtility() {
    return util;
  }

  public void setUtility(double newUtil) {
    if (newUtil < aspLevel) {
      load = (load + previousLoad) / 2;
      if (!overThreshold) {
        loadChange /= 2;
        overThreshold = true;
      }
    }
    else {
      aspLevel = LEARNING_RATE * newUtil + (1 - LEARNING_RATE) * util;
      previousLoad = load;
      load += loadChange;
      overThreshold = false;
    }
    util = newUtil;
  }
}
