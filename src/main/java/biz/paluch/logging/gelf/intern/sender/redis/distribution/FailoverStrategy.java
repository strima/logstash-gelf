package biz.paluch.logging.gelf.intern.sender.redis.distribution;

import java.util.List;

import biz.paluch.logging.gelf.intern.sender.redis.RedisDistributionStrategy;
import biz.paluch.logging.gelf.intern.sender.redis.RedisInstance;


class FailoverStrategy extends MaintenanceStrategy implements RedisDistributionStrategy {
    
    private RedisInstance[] redisIntances;
    
    FailoverStrategy(List<RedisInstance> redisInstances, long maintanceInterval) {
        super(maintanceInterval);
        this.redisIntances = redisInstances.toArray(new RedisInstance[redisInstances.size()]);
    }

    
    @Override
    public RedisInstance getInstance() {

        for(RedisInstance instance : redisIntances) {
            if(!isInErrorState(instance)) {
                return instance;
            }
        }
        return null;
    }

  

}
