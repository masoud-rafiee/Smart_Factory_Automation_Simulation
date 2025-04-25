package Skeleton;

import java.lang.Math;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class that represents a unit in the simulation.
 * 
 * All objects running in the simulation must implement this, or a modified
 * subclass of it.
 * */
public abstract class Unit implements Runnable {
	private String name; // Name of the unit
	private SimulationInput input; // The input settings
	private Statistics stats;

	public Unit(SimulationInput input) {
		this("DefaultUnitName", input);
	}

	/**
	 * General constructor for the Skeleton.Unit.
	 * 
	 * @param name The name of the unit.
	 * @param input The input settings.
	 * */
	public Unit(String name, SimulationInput input) {
		this.name = name;
		this.input = input;

		// Get the statistics object for this Skeleton.Unit
		this.stats = StatisticsContainer.getInstance().addComponent(this.getName());

		// Add a statistic for the number of active units performing an action
		this.stats.addStatistic("ActiveUnits", new WorkerStatistic("ActiveUnits"));
	}

	public Statistics getStats() {
		return this.stats;
	}

	/**
	 * Has your unit perform a single action. For example:
	 * 		Minning some ore.
	 * 		Depositing some cargo.
	 * 		Drive to a light.
	 * 		Gathering produce from a producer.
	 * 		...
	 * 
	 * Your Skeleton.Unit may do a portion of a larger action as well
	 * at this stage and hold a state of where it is along the path.
	 * 
	 * The unit could also attempt it all at once, but consider
	 * how that will impact drift in the timing of the actions
	 * (actions/second). There is nothing wrong with sleeping
	 * with much longer than the actions/second rate though, and
	 * it's encouraged to do so in some areas of your simulation.
	 * 
	 * */
	public abstract void performAction();

	/**
	 * Submit some statistics to the Skeleton.Statistics object.
	 * 
	 * This should handle sending statistics that don't depend on
	 * what happens in the performAction method. In there, you may
	 * want to also submit statistics (ore mined during action,
	 * kilometers driven, tonnage of cargo deposited, etc.).
	 * 
	 * */
	public abstract void submitStatistics();

	/** Returns the Skeleton.SimulationInput **/
	public SimulationInput getSimInput() {
		return this.input;
	}

	/** Returns the Skeleton.Unit's name. **/
	public String getName() {
		return this.name;
	}

	/** Units are equal when their names are equal. **/
	public boolean equals(Object o) {
		return ((Unit) o).getName().equals(this.getName());
	}


	/**
	 * This is the method which runs the Skeleton.Unit for `Time` seconds (specified) in
	 * the input and performs `ActionsPerSecond` actions/second during this time.
	 * 
	 * The run method also slighlty handles drift in timing. This means that the
	 * amount of time your methods take will impact the amount of time the Skeleton.Unit
	 * waits until it attempts to perform an action again.
	 * 
	 * If you override this method in a subclass, ensure that all of the
	 * above is still implemented.
	 * 
	 * The run method performs the following:
	 * 		Add a worker/unit to the stats object.
	 * 		Perform the action.
	 * 		Submit the statistics.
	 * 		Remove the worker/unit from the stats object.
	 * 		Wait until the next action needs to be performed.
	 * 
	 * */
	@Override
	public void run() {
		int timeSec = this.input.getIntegerInput("Time");
		int actionsPerSec = this.input.getIntegerInput("ActionsPerSecond");
		long msPerAction = (long) ((1.0 / actionsPerSec) * 1000);

		int totalActions = timeSec * actionsPerSec;
		for (int actionCount = 0; actionCount < totalActions; actionCount++) {
			long actionStart = System.currentTimeMillis();

			// 1) mark active
			Statistic activeStat = this.getStats().getStatistic("ActiveUnits");
			activeStat.addValue(1);

			// 2) perform and record
			this.performAction();
			this.submitStatistics();

			// 3) unmark active
			activeStat.removeValue();

			// 4) wait for next action
			long drift = System.currentTimeMillis() - actionStart;
			long sleep = Math.max(0, msPerAction - drift);
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
