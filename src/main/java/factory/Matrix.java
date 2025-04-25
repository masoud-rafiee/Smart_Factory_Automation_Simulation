package factory;

import Skeleton.SimulationInput;
import Skeleton.SimulationInput;
import factory.UnitFactory;
import Skeleton.Unit;

/**
 * The class that is responsible for running the simulation.
 */
public class Matrix {

	public static void run(SimulationInput input) {
		// 1) create a shared buffer
		SharedBuffer<String> buffer = new SharedBuffer<>(10);

		// 2) determine how many robots and belts
		int numRobots;
		try { numRobots = input.getIntegerInput("NumRobots"); }
		catch (RuntimeException e) { numRobots = 3; }
		int numBelts;
		try { numBelts  = input.getIntegerInput("NumBelts"); }
		catch (RuntimeException e) { numBelts = 1; }

		// 3) start robot threads via factory
		Thread[] robots = new Thread[numRobots];
		for (int i = 0; i < numRobots; i++) {
			String name = "Robot-" + (i+1);
			robots[i] = new Thread(
					UnitFactory.create("robot", name, input, buffer)
			);
			robots[i].start();
		}

		// 4) start conveyor belt threads via factory
		Thread[] belts = new Thread[numBelts];
		for (int i = 0; i < numBelts; i++) {
			String name = "belt-" + (i+1);
			belts[i] = new Thread(
					UnitFactory.create("belt", name, input, buffer)
			);
			belts[i].start();
		}

		// 5) wait for all to finish
		try {
			for (Thread t : robots) t.join();
			for (Thread t : belts)  t.join();
		} catch (InterruptedException ignored) { }
	}
}
