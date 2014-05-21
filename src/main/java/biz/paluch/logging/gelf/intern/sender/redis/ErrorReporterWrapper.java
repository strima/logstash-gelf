package biz.paluch.logging.gelf.intern.sender.redis;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import biz.paluch.logging.gelf.intern.ErrorReporter;

/**
 * 
 * Wraps the origin error reporter for possible logging of more than only the first error message 
 * to standard error. 
 *
 */
class ErrorReporterWrapper implements ErrorReporter {

    private int maxLogsPerMinute = 10;
    
    private final AtomicLong logCount = new AtomicLong(0);
    private ErrorReporter wrapped;
    
    private long lastTimeStamp = 0;
    
    ErrorReporterWrapper(ErrorReporter wrapped, int maxLogsPerMinute) {
        super();
        this.wrapped = wrapped;
        this.maxLogsPerMinute = maxLogsPerMinute;
        this.lastTimeStamp = System.currentTimeMillis();
    }



    @Override
    public void reportError(String message, Exception ex) {
        wrapped.reportError(message, ex);

        if(System.currentTimeMillis() - lastTimeStamp > 60000) {
            logCount.set(0);
            lastTimeStamp = System.currentTimeMillis();
        }
        long actualCount = logCount.getAndIncrement();
        if(actualCount <= maxLogsPerMinute) {
           
            System.err.println("REDIS-ERROR : "+message);
            if (ex != null) {
                ex.printStackTrace();
            }
            if(actualCount == maxLogsPerMinute) {
                System.err.println("REDIS-ERROR: Maxinum number of system error messages reached!");
            }
        }
    }
    

}
