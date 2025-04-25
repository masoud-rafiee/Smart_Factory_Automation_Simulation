package factory;

import Skeleton.SimulationInput;
import Skeleton.WorkerStatistic;
import Skeleton.Unit;
import java.util.concurrent.Semaphore;
import factory.ActionStrategy;
import factory.ConsumeStrategy;


public class Robot extends Unit {
    private static final Semaphore robotSemaphore = new Semaphore(2);
    private final ActionStrategy strategy;

    private final SharedBuffer<String> buffer;
    /** Primary constructor */
    public Robot(String name, SimulationInput input,
                 SharedBuffer<String> buffer,
                 ActionStrategy strategy) {
        super(name, input);
        this.getStats().addStatistic("ActionsPerformed", new WorkerStatistic("ActionsPerformed"));
        this.buffer = buffer;
        this.strategy = strategy;
    }

    /** Default to ConsumeStrategy */
    public Robot(String name, SimulationInput input, SharedBuffer<String> buffer) {
        this(name, input, buffer, new ConsumeStrategy());
    }

    /** Default name and buffer */
    public Robot(String name, SimulationInput input) {
        this(name, input, new SharedBuffer<>(1));
    }

    public Robot(SimulationInput input) {
        this("Robot", input);
    }

//    public Robot(String name, SimulationInput input) {
//        super(name, input);
//        this.getStats().addStatistic("ActionsPerformed", new WorkerStatistic("ActionsPerformed"));
//    }
//
//    public Robot(SimulationInput input) {
//        this("Robot", input);
//    }
@Override
public void performAction() {
    try {
        robotSemaphore.acquire();
        // Only consume if there's at least one belt producing
        int beltCount = getSimInput().getIntegerInput("NumBelts");
        if (beltCount > 0) {
            strategy.execute(getName(), buffer);
        }
        // else: skip consuming, just count via submitStatistics()
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        robotSemaphore.release();
    }
}


    @Override
    public void submitStatistics() {
        this.getStats()
                .getStatistic("ActionsPerformed")
                .addValue(1);
    }
}
