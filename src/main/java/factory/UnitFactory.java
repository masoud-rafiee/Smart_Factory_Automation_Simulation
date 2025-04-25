package factory;
import Skeleton.Unit;

import Skeleton.SimulationInput;

/**
 * Factory Method: creates Units by type.
 */
public class UnitFactory {

    /**
     * @param type   "robot" or "belt"
     * @param name   the unit’s name
     * @param input  simulation input
     * @param buffer shared buffer
     */
    public static Unit create(String type, String name,
                              SimulationInput input,
                              SharedBuffer<String> buffer) {
        if ("robot".equalsIgnoreCase(type)) {
            return new Robot(name, input, buffer);
        } else if ("belt".equalsIgnoreCase(type)) {
            return new ConveyorBelt(name, input, buffer);
        } else {
            throw new IllegalArgumentException("Unknown unit type: " + type);
        }
    }
}
