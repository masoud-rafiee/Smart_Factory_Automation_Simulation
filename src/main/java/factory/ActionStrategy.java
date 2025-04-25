package factory;

/**
 * Strategy pattern: defines how a unit performs its action.
 */
public interface ActionStrategy {
    /**
     * Execute one action.
     * @param name   the unit’s name
     * @param buffer shared buffer (may be null)
     * @throws InterruptedException if interrupted
     */
    void execute(String name, SharedBuffer<String> buffer) throws InterruptedException;
}
