package biz.paluch.logging.gelf.async;

import java.lang.management.ManagementFactory;
import java.util.logging.LogRecord;

import javax.management.MBeanServer;
import javax.management.ObjectName;


//Test:
// One connection per Instance vs. jedesmal Entnahme aus Threadpool
public enum AsyncLogging implements AsyncLoggingMBean {

    INSTANCE;

    private AsyncLoggingStrategy<LogRecord> strategy;

    // Direct use of ExecutorService

    private AsyncLogging() {

        if("disruptor".equalsIgnoreCase(getStrategy())) {
            this.strategy = new AsyncDisruptorStrategy<LogRecord>(getBufferSize(), getNumberOfParallelWorkers());
        } else if("executortasks".equalsIgnoreCase(getStrategy())) {
            this.strategy = new AsyncExecutorTasksStrategy<LogRecord>(getBufferSize(), getNumberOfParallelWorkers());
        } else {
            System.err.println("Async Logging nicht moeglich. Strategie nicht erkannt: "+getStrategy());
            throw new IllegalArgumentException("Unknown async logging strategy "+getStrategy());
        }
        jmxRegister();
    }

    public void log(LogRecord record, LogHandler<LogRecord> handler) {
        this.strategy.log(record, handler);
    }

    @Override
    public String getStrategy() {
        return System.getProperty("gelf.logging.async.strategy", "disruptor");
    }
    
   
    @Override
    public int getNumberOfParallelWorkers() {
        return Integer.parseInt(System.getProperty("gelf.logging.async.parallelworker", "8"));
    }

    @Override
    public int getBufferSize() {
        return Integer.parseInt(System.getProperty("gelf.logging.async.buffersize", "1024"));
    }

    @Override
    public long getRemaingBufferSize() {
        return this.getBufferSize() - this.strategy.getAllocatedBufferSize();
    }

    @Override
    public long getAllocatedBufferSize() {
        return this.strategy.getAllocatedBufferSize();
    }

    private void jmxRegister() {
        if(Boolean.parseBoolean(System.getProperty("gelf.logging.async.jmxEnabled", "true"))) {
            try {
                ObjectName oname = new ObjectName(System.getProperty("logging.async.jmxObjectName", "logstash-gelf:type=Logging,name=AsyncLogging"));
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(this, oname);
            } catch (Exception e) {
                System.err.println("Cannot register async logging mbean on mbeanserver: " + e.getMessage());
            }
        }
    }

}
