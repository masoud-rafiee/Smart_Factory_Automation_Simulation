package factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Skeleton.SimulationInput;
import Skeleton.StatisticsContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotTest {

    @BeforeEach
    void resetStats() {
        StatisticsContainer.resetInstance();
    }

    @Test
    void performAction_shouldIncrementActionsPerformed() {
        SimulationInput input = new SimulationInput();
        input.addInput("Time",             List.of("1"));
        input.addInput("ActionsPerSecond", List.of("2"));
        input.addInput("NumRobots",        List.of("1"));
        input.addInput("NumBelts",         List.of("0"));

        var stats = Main.runTest(input);

        float total = stats
                .getComponent("Robot-1")
                .getStatistic("ActionsPerformed")
                .summarize();

        assertEquals(2.0f, total, "Robot should perform 2 actions in one second");
    }
}
