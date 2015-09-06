import java.io.IOException;

public class Commons {
  // Environment settings
  private static final double MAX_LOAD = 7.5;
  private static final double UTILITY_BASE = 1.0;
  private static final int POPULATION_SIZE = 10;
  private static final double ENVIRONMENTAL_FACTOR = 3; // k = {0.25, 0.5, 1, 3, 5}
  private static final double FREE_RIDER_FACTOR = 1.15;

  // Experimental settings
  private static final long SEED = 1234;
  private static final int NUM_RUNS = 10;
  private static final int NUM_ITERATIONS = 100;
  
  public static void main (String args []) throws IOException {
    double[][] totalLoads = new double[NUM_RUNS][NUM_ITERATIONS];
    double[][] utilityAverages = new double[NUM_RUNS][NUM_ITERATIONS];
    double[][] utilityStandardDeviations = new double[NUM_RUNS][NUM_ITERATIONS];
    double[][] numFreeRiders = new double[NUM_RUNS][NUM_ITERATIONS];

    for (int runs = 0; runs < NUM_RUNS; runs++) {
      Random generator = new Random(SEED);
      Agent[] agents = new Agent[POPULATION_SIZE];
      for (int i = 0; i < POPULATION_SIZE; i++) {
        agents[i] = new Agent(generator, POPULATION_SIZE);
      }

      for (int iterations = 0; iterations < NUM_ITERATIONS; iterations++) {
        // Get total load imposed by all agents
        double totalLoad = 0;
        for (Agent a : agents) {
          totalLoad += a.getLoad();
        }

        // Compare against threshold to establish utility returned to agents
        double utilityPerLoad = (totalLoad <= MAX_LOAD) ?
          UTILITY_BASE :
          Math.exp(-1 * ENVIRONMENTAL_FACTOR * (totalLoad - MAX_LOAD));

        // Return utility to agents
        for (Agent a : agents) {
          a.setUtility(a.getLoad() * utilityPerLoad);
        }

        // Calculate standard deviation of utility
        double averageUtility = totalLoad * utilityPerLoad / POPULATION_SIZE;
        double deviations = 0;
        for (Agent a : agents) {
          deviations += Math.pow(a.getUtility() - averageUtility, 2);
        }
        double utilitySD = Math.sqrt(deviations / (POPULATION_SIZE - 1));

        // Calculate number of free riders
        int freeRiders = 0;
        if (totalLoad > MAX_LOAD) {
          for (Agent a : agents) {
            double averageLoadOfOthers = (totalLoad - a.getLoad()) /
                                         (POPULATION_SIZE - 1); 
            if (a.getLoad() >= FREE_RIDER_FACTOR * averageLoadOfOthers) {
              freeRiders++;
            }
          }
        }

        // Store results for this iteration
        totalLoads[runs][iterations] = totalLoad;
        utilityAverages[runs][iterations] = averageUtility;
        utilityStandardDeviations[runs][iterations] = utilitySD;
        numFreeRiders[runs][iterations] = freeRiders; 
      }
    }

    // Print out average and standard deviations of results
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      System.out.printf("%d %5.3f %6.4f %7.5f %6.4f %5.3f %6.4f\n",
        i + 1,
        average(totalLoads, i), standardDeviation(totalLoads, i),
        average(utilityAverages, i), standardDeviation(utilityAverages, i), 
        average(utilityStandardDeviations, i), standardDeviation(utilityStandardDeviations, i));
    }
  }

  private static double average(double[][] array, int iteration) {
    double total = 0;
    for (int i = 0; i < NUM_RUNS; i++) {
      total += array[i][iteration];
    }
    return total / NUM_RUNS;
  }

  private static double standardDeviation(double[][] array, int iteration) {
    double average = average(array, iteration);
    double deviations = 0;
    for (int run = 0; run < NUM_RUNS; run++) {
      deviations += Math.pow(array[run][iteration] - average, 2);
    }
    return Math.sqrt(deviations / (POPULATION_SIZE - 1));
  }
}
