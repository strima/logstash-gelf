package biz.paluch.logging.gelf.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncExecutorTasksStrategy<T> implements AsyncLoggingStrategy<T> {

    private final ExecutorService executorService;
    private final BlockingQueue<Runnable> recordQueue;
    
    public AsyncExecutorTasksStrategy(int bufferSize, int parallelWorker) {
        this.recordQueue = new ArrayBlockingQueue<Runnable>(bufferSize);
        this.executorService = new ThreadPoolExecutor(parallelWorker, parallelWorker,0, TimeUnit.MILLISECONDS, recordQueue,new ThreadPoolExecutor.CallerRunsPolicy());
    }
    
    @Override
    public void log(T record, LogHandler<T> handler) {
        this.executorService.submit(new LoggingTask<T>(record, handler));
    }

    @Override
    public long getAllocatedBufferSize() {
        return recordQueue.size();
    }

  
    private static final class LoggingTask<R> implements Runnable {
        
        private R record; 
        private LogHandler<R> handler;
        
        private LoggingTask(R record, LogHandler<R> handler) {
            super();
            this.record = record;
            this.handler = handler;
        }

        @Override
        public void run() {
            this.handler.handle(record);
        }
        
    }

}
