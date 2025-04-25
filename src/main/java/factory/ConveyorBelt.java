package factory;

import Skeleton.SimulationInput;
import Skeleton.WorkerStatistic;
import Skeleton.Unit;

/**
 * A conveyor belt that moves items and tracks how many items it’s moved.
 */
public class ConveyorBelt extends Unit {
    private final SharedBuffer<String> buffer;

    /** Primary constructor: name, input and shared buffer */
    public ConveyorBelt(String name, SimulationInput input, SharedBuffer<String> buffer) {
        super(name, input);
        this.getStats().addStatistic("ItemsMoved", new WorkerStatistic("ItemsMoved"));
        this.buffer = buffer;
    }

    /** For tests or convenience: uses a tiny dummy buffer of capacity 1 */
    public ConveyorBelt(String name, SimulationInput input) {
        this(name, input, new SharedBuffer<>(1));
    }

    /** Default name and dummy buffer */
    public ConveyorBelt(SimulationInput input) {
        this("ConveyorBelt", input);
    }

    @Override
    public void performAction() {
        try {
            buffer.put("widget");      // produce one item
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void submitStatistics() {
        this.getStats()
                .getStatistic("ItemsMoved")
                .addValue(1);
    }
}
