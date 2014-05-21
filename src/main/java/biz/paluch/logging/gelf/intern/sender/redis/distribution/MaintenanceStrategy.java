package biz.paluch.logging.gelf.intern.sender.redis.distribution;

import biz.paluch.logging.gelf.intern.sender.redis.RedisInstance;


abstract class MaintenanceStrategy {
    
    private long maintanceInterval;
    
    MaintenanceStrategy(long maintanceInterval) {
        super();
        this.maintanceInterval = maintanceInterval;

    }

    boolean isInErrorState(RedisInstance instance) {
        maintain(instance);
        return instance.isInErrorState();
    }
    
    private void maintain(RedisInstance instance) {
       if(instance.isInErrorState() || instance.getErrorCount() > 0) {
           if(System.currentTimeMillis() > (instance.getErrorTimestamp()+maintanceInterval)) {
               instance.reset();
           } 
       }
    }
}
