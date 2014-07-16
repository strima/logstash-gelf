package biz.paluch.logging.gelf.jboss7;

import java.util.logging.LogRecord;

import biz.paluch.logging.gelf.async.AsyncLogging;
import biz.paluch.logging.gelf.async.LogHandler;

public class JBoss7GelfLogAsyncHandler extends JBoss7GelfLogHandler implements LogHandler<LogRecord> {
    
    public JBoss7GelfLogAsyncHandler() {
        super();
    }

    @Override
    public void publish(LogRecord record) {
       AsyncLogging.INSTANCE.log(record, this);
    }

    @Override
    public void handle(LogRecord logEvent) {
        super.publish(logEvent);
    }

    
  
}
