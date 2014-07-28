package biz.paluch.logging.gelf.jboss7;

import java.util.logging.LogRecord;

import org.jboss.logmanager.ExtLogRecord;

import biz.paluch.logging.gelf.MdcGelfMessageAssembler;
import biz.paluch.logging.gelf.async.AsyncLogging;
import biz.paluch.logging.gelf.async.LogHandler;
import biz.paluch.logging.gelf.intern.GelfMessage;

public class JBoss7GelfLogAsyncHandler extends JBoss7GelfLogHandler implements LogHandler<LogRecord> {
    
    public JBoss7GelfLogAsyncHandler() {
        super();
    }

    @Override
    public void publish(LogRecord record) {
       record = ExtLogRecord.wrap(record);
       ((ExtLogRecord)record).copyAll();
       AsyncLogging.INSTANCE.log(record, this);
    }

    @Override
    public void handle(LogRecord logEvent) {
        super.publish(logEvent);
    }

  
  
}
