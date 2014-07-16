package biz.paluch.logging.gelf.async;


public interface LogHandler<T> {

    public void handle(T logRecord);
}
