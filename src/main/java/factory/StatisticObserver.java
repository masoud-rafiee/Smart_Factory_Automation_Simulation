package factory;

/**
 * Observer for statistic updates.
 */
public interface StatisticObserver {
    /**
     * Called when a statistic is updated.
     * @param component name of the unit (e.g., "Robot-1")
     * @param statName  name of the statistic (e.g., "ActionsPerformed")
     * @param newValue  the value just added
     */
    void onStatisticUpdated(String component, String statName, float newValue);
}
