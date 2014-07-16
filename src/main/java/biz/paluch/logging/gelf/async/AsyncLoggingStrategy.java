package biz.paluch.logging.gelf.async;

public interface AsyncLoggingStrategy<T> {
    void log(T record, LogHandler<T> handler);
    long getAllocatedBufferSize();
}