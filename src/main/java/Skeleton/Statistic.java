package Skeleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import factory.StatisticObserver;

/**
 * This class is used to represent a Skeleton.Statistic. For an example,
 * see Skeleton.WorkerStatistic. We can give it a name like "ActiveUnits",
 * and this will represent some metric of your simulation (in that
 * case it would number of units with the same name performing
 * a particular action). There's also the RobotActionsPerformed
 * statistic in the Robot class.
 *
 * In general, you should be able to get by using the Skeleton.WorkerStatistic,
 * but it would be helpful to make a different ones for more specialized summarization,
 * or value parsing.
 */

public abstract class Statistic implements Iterable<Object> {
	protected ArrayList<Object> values;
	private final String name;
	private String componentName; // Added to track which component this statistic belongs to

	private static final List<StatisticObserver> observers = new CopyOnWriteArrayList<>();

	/** Register a new observer */
	public static void registerObserver(StatisticObserver obs) {
		observers.add(obs);
	}

	/** Notify observers of an update */
	private void notifyObservers(String component, String statName, float newValue) {
		for (var obs : observers) {
			obs.onStatisticUpdated(component, statName, newValue);
		}
	}

	public Statistic(String name) {
		this.name = name;
		this.values = new ArrayList<Object>();
		this.componentName = "Unknown"; // Default component name
	}

	// Add a setter for the component name
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getName() {
		return this.name;
	}

	public String getComponentName() {
		return this.componentName;
	}

	// Stats are all iterable, making it easier to use in for-loops
	public Iterator<Object> iterator() {
		return values.iterator();
	}

	// Use these methods to add/remove/get values
	public synchronized void addValue(Object item) {
		this.values.add(item);
		// notify observers with the correct component name and statistic name
		try {
			float v = Float.parseFloat(item.toString());
			notifyObservers(this.componentName, this.name, v);
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	public synchronized void removeValue() {
		this.removeValue(0);
	}

	public synchronized void removeValue(int index) {
		this.values.remove(index);
	}

	public Object getValue(int index) {
		return this.values.get(index);
	}

	// Implement these to summarize, and print the statistic
	public abstract float summarize();
	public abstract void printStatistic();
}