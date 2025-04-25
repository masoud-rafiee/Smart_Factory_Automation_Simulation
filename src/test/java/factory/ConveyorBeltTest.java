package factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Skeleton.SimulationInput;
import Skeleton.StatisticsContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConveyorBeltTest {

    @BeforeEach
    void resetStats() {
        StatisticsContainer.resetInstance();
    }

    @Test
    void conveyorBelt_movesExpectedNumbersOfItems() {
        SimulationInput input = new SimulationInput();
        input.addInput("Time",             List.of("1"));
        input.addInput("ActionsPerSecond", List.of("3"));
        input.addInput("NumRobots",        List.of("0"));
        input.addInput("NumBelts",         List.of("1"));

        var stats = Main.runTest(input);

        float moved = stats
                .getComponent("belt-1")           // <- thread name from Matrix
                .getStatistic("ItemsMoved")
                .summarize();

        assertEquals(3.0f, moved,
                "The conveyor belt should have moved 3 items in one second at 3 actions/sec");
    }
}
