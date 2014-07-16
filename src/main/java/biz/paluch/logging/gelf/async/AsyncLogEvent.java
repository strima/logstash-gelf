package biz.paluch.logging.gelf.async;


public class AsyncLogEvent<T> {

    private T logRecord;
    private LogHandler<T> handler;
    
    public T getLogRecord() {
        return logRecord;
    }
    
    public void setLogRecord(T logRecord) {
        this.logRecord = logRecord;
    }

    public LogHandler<T> getLogHandler() {
        return handler;
    }

    public void setLogHandler(LogHandler<T> handler) {
        this.handler = handler;
    }

  
}
