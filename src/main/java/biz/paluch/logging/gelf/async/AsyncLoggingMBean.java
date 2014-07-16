package biz.paluch.logging.gelf.async;

public interface AsyncLoggingMBean {

    int getNumberOfParallelWorkers();
    int getBufferSize();
    long getRemaingBufferSize();
    long getAllocatedBufferSize();
    String getStrategy();
}
