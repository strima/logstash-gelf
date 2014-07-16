package biz.paluch.logging.gelf.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class AsyncDisruptorStrategy<T> implements AsyncLoggingStrategy<T> {

    private ExecutorService executorService;

    private Disruptor<AsyncLogEvent<T>> disruptor;
    private RingBuffer<AsyncLogEvent<T>> ringBuffer;

    public AsyncDisruptorStrategy(int bufferSize, int parallelWorker) {
        try {
            this.executorService = Executors.newCachedThreadPool();

            
            LogEventHandler<T>[] eventHandler = new LogEventHandler[parallelWorker];
            for (int i = 0; i < parallelWorker; i++) {
                eventHandler[i] = new LogEventHandler<T>();
            }

            this.disruptor = new Disruptor<AsyncLogEvent<T>>(new EventFactory<AsyncLogEvent<T>>() {
                public AsyncLogEvent<T> newInstance() {
                    return new AsyncLogEvent<T>();
                }
            }, bufferSize, this.executorService, ProducerType.MULTI, new SleepingWaitStrategy());

            disruptor.handleEventsWithWorkerPool(eventHandler);
            this.ringBuffer = disruptor.start();
        } catch (Throwable e) {
            System.err.println("Cannot instantiate async logging with LMAX disruptor " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void log(T record, LogHandler<T> handler) {
        long sequence = ringBuffer.next();
        AsyncLogEvent<T> event = ringBuffer.get(sequence);
        event.setLogRecord(record);
        event.setLogHandler(handler);
        ringBuffer.publish(sequence);
    }

    @Override
    public long getAllocatedBufferSize() {
        return ringBuffer.getBufferSize() - ringBuffer.remainingCapacity();
    }

    private final static class LogEventHandler<E> implements WorkHandler<AsyncLogEvent<E>> {

        @Override
        public void onEvent(AsyncLogEvent<E> logEvent) throws Exception {
            logEvent.getLogHandler().handle(logEvent.getLogRecord());
        }

    }
}
