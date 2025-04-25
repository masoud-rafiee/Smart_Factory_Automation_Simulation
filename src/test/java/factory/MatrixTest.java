package factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Skeleton.SimulationInput;
import Skeleton.StatisticsContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixTest {

    @BeforeEach
    void resetStats() {
        StatisticsContainer.resetInstance();
    }

    @Test
    void threeRobotsAndThreeBelts_runAtOneActionPerSecondForOneSecond() {
        // Arrange: 1s, 1 action/sec, 3 robots, 3 belts
        SimulationInput input = new SimulationInput();
        input.addInput("Time",             List.of("1"));
        input.addInput("ActionsPerSecond", List.of("1"));
        input.addInput("NumRobots",        List.of("3"));
        input.addInput("NumBelts",         List.of("3"));

        // Act
        var stats = Main.runTest(input);

        // Robots each do 1 action: total 3
        float totalRobotActions =
                stats.getComponent("Robot-1").getStatistic("ActionsPerformed").summarize()
                        + stats.getComponent("Robot-2").getStatistic("ActionsPerformed").summarize()
                        + stats.getComponent("Robot-3").getStatistic("ActionsPerformed").summarize();

        // Belts each move 1 item: total 3
        float totalBeltMoves =
                stats.getComponent("belt-1").getStatistic("ItemsMoved").summarize()
                        + stats.getComponent("belt-2").getStatistic("ItemsMoved").summarize()
                        + stats.getComponent("belt-3").getStatistic("ItemsMoved").summarize();

        // Assert
        assertEquals(3f, totalRobotActions, "3 robots should each perform 1 action");
        assertEquals(3f, totalBeltMoves,    "3 belts should each move 1 item");
    }
}
