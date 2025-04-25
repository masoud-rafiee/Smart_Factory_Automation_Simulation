package Skeleton;

import java.util.HashMap;
import java.util.Set;

/**
 * This class is used to keep track of Skeleton.Statistic instances by name
 */
public class MetricsComponent {
    private final String name;
    private final HashMap<String, Statistic> statistics;

    public MetricsComponent(String name) {
        this.name = name;
        this.statistics = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public void addStatistic(Statistic stat) {
        // Set the component name on the statistic
        stat.setComponentName(this.name);
        statistics.put(stat.getName(), stat);
    }

    public Statistic getStatistic(String name) {
        return statistics.get(name);
    }

    public Set<String> getStatisticNames() {
        return statistics.keySet();
    }
}