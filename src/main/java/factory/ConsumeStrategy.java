package factory;

import Skeleton.StatisticsContainer;

/**
 * Default robot strategy: just consume from the buffer.
 */
public class ConsumeStrategy implements ActionStrategy {
    @Override
    public void execute(String name, SharedBuffer<String> buffer) throws InterruptedException {
        // consume one widget
        buffer.take();


    }
}
