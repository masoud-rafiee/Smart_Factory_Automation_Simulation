package Skeleton;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Singleton object for all statistics. At this level, we have one set of Skeleton.Statistics
 * for each Skeleton.Unit. The name of the object is the key used here in the HashMap.
 *
 */
public class StatisticsContainer {

	// Contains each statistic for the unit of a given name
	private LinkedHashMap<String, Statistics> allStatistics;
	private static StatisticsContainer stats;
	private SimulationInput input;

	/* Clear out any existing singleton so each test starts fresh. */
	public static void resetInstance() {
		stats = null;
	}

	/** Constructor that creates the hash table. */
	private StatisticsContainer(SimulationInput input) {
		this.input = input;
		this.allStatistics = new LinkedHashMap<>();
	}

	/** Constructs a container with statistics split into components. */
	private StatisticsContainer(SimulationInput input, String[] compNames) {
		this(input);
		for (String comp : compNames) {
			this.allStatistics.put(comp, new Statistics(this.input));
		}
	}

	/** @return The singleton instance (creating with default input if needed). */
	public static StatisticsContainer getInstance() {
		return StatisticsContainer.getInstance(new SimulationInput());
	}

	/**
	 * Returns the statistics object if it exists, otherwise it creates it.
	 * @param si SimulationInput to initialize if new
	 */
	public static StatisticsContainer getInstance(SimulationInput si) {
		if (StatisticsContainer.stats == null) {
			StatisticsContainer.stats = new StatisticsContainer(si);
		}
		return StatisticsContainer.stats;
	}

	/**
	 * Add a statistic component to the hash table.
	 * @param component Name of the component
	 */
	public Statistics addComponent(String component) {
		allStatistics.put(component, new Statistics(this.input));
		return this.getComponent(component);
	}

	/**
	 * Get the statistics for the given component name, creating an empty one if absent.
	 * @param component Name of the component
	 * @return The Statistics object
	 */
	public Statistics getComponent(String component) {
		return this.allStatistics.computeIfAbsent(
				component,
				key -> new Statistics(this.input)
		);
	}

	/**
	 * Returns the names of all registered components.
	 */
	public Set<String> getComponentNames() {
		return this.allStatistics.keySet();
	}

	/** Print the statistics container. */
	public void printStatisticsContainer() {
		for (String key : this.allStatistics.keySet()) {
			System.out.println(String.format("Statistics for %s:", key));
			this.allStatistics.get(key).printStatistics();
			System.out.println();
		}
	}
}
