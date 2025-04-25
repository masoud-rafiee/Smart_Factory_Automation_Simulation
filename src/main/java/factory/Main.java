package factory;

import Skeleton.SimulationInput;
import Skeleton.StatisticsContainer;
import java.util.ArrayList;
import java.util.List;
import Skeleton.Statistic;

public class Main {
	/**
	 * Runs a test with the given input and returns the statistics
	 * produced from the test run. Simplifies the testing process.
	 *
	 * @param input The input to run the test with.
	 * @return The statistics of the test run.
	 **/
	public static StatisticsContainer runTest(SimulationInput input) {
		// Reset and get a fresh statistics container
		Skeleton.StatisticsContainer.resetInstance();
		StatisticsContainer stats = StatisticsContainer.getInstance(input);

		StatisticObserver consoleLogger = (component, statName, newValue) ->
				System.out.printf("Observer: %s → %s updated by %s%n",
						component, statName, newValue);

		Statistic.registerObserver(consoleLogger);

		// Run the simulation
		Matrix.run(input);

		// Debug: print all component names
		System.out.println("DEBUG Keys: " + stats.getComponentNames());

		return stats;
	}

	/**
	 * Overload for array-based inputs.
	 **/
	public static StatisticsContainer runTest(ArrayList<ArrayList<String>> input) {
		return runTest(new SimulationInput(input));
	}

	public static void main(String[] args) {
		SimulationInput si = new SimulationInput();
		si.addInput("Time",             List.of("10")); // seconds
		si.addInput("ActionsPerSecond", List.of("1"));

		// Example input; not used by tests
		si.addInput("RobotsMustYell",   List.of("HELLO, WORLD"));

		// Run and print stats
		StatisticsContainer stats = runTest(si);
		stats.printStatisticsContainer();
	}
}
