import java.io.*;
import java.util.*;

public class Commons {
	private static double totalLoad;
	private static final double maxLoad = 7.5;
	private static final double utilityBase = 1.0;
	private static final int populationSize = 10;
	private static final int numRuns = 10;
	private static final int numIterations = 200;
	private static final double environmentalFactor = 3; // k = {0.25, 0.5, 1, 3, 5}
	private static Agent[] agents;
	private static double[][] load, avgUtility, utilSD, numFreeRiders;


	public static void main (String args []) throws IOException {
		load = new double[numRuns][numIterations];
		avgUtility = new double[numRuns][numIterations];
		utilSD = new double[numRuns][numIterations];
		numFreeRiders = new double[numRuns][numIterations];

		for (int runs = 0; runs < numRuns; runs++) {
			agents = new Agent[populationSize];
			Random generator = new Random();
			for (int i = 0; i < populationSize; i++)
				agents[i] = new Agent(generator, populationSize);

			for (int iterations = 0; iterations < numIterations; iterations++) {
				getTotalLoad();
				returnUtility();
				load[runs][iterations] = totalLoad;
				avgUtility[runs][iterations] = getTotalUtility() / populationSize;
				utilSD[runs][iterations] = utilSD();
				numFreeRiders[runs][iterations] = numFreeRiders(); 
			}
		}
		
		PrintWriter out = new PrintWriter(new FileWriter("plottingOutput.out"));
		for (int i = 0; i < numIterations; i++)
			out.printf("%d %5.3f %6.4f %7.5f %6.4f %5.3f %6.4f \n",
					i + 1, average(load, i), SD(load, i), average(avgUtility, i), SD(avgUtility, i), 
					average(utilSD, i), SD(utilSD, i));
		out.close();
	}

	public static void getTotalLoad() {
		totalLoad = 0;
		for (Agent x : agents)
			totalLoad += x.getLoad();
	}

	public static double getTotalUtility() {
		return totalLoad * UtilityPerLoad();
	}

	public static void returnUtility() {
		double utilPerLoad = UtilityPerLoad();
		for (Agent x : agents)
			x.setUtility(x.getLoad() * utilPerLoad);
	}

	private static double UtilityPerLoad() {
		if (totalLoad <= maxLoad)
			return utilityBase;
		return Math.exp(-1 * environmentalFactor * (totalLoad - maxLoad));
	}
	
	public static double utilSD() {
		double loadAvg = getTotalUtility() / populationSize;
		double deviations = 0;
		for (Agent x : agents)
			deviations += Math.pow(x.getLoad() - loadAvg,2);
		return Math.sqrt(deviations / (populationSize - 1));
	}

	public static double average(double[][] array, int iteration) {
		double total = 0;
		for (int i = 0; i < numRuns; i++)
			total += array[i][iteration];
		return total / numRuns;
	}

	public static double SD(double[][] array, int iteration) {
		double average = average(array, iteration);
		double deviations = 0;
		for (int run = 0; run < numRuns; run++)
			deviations += Math.pow(array[run][iteration] - average, 2);
		return Math.sqrt(deviations / (populationSize - 1));
	}

	public static int numFreeRiders() {
		if (totalLoad <= maxLoad)
			return 0;
		int numFR = 0;
		for (int i = 0; i < populationSize; i++) {
			double averageOthers = (totalLoad - agents[i].getLoad()) / (populationSize - 1); 
			if (agents[i].getLoad() >= 1.15 * averageOthers)
				numFR++;
		}
		return numFR;
	}
}