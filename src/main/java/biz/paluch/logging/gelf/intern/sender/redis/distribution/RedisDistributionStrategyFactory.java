package biz.paluch.logging.gelf.intern.sender.redis.distribution;

import java.util.List;

import biz.paluch.logging.gelf.intern.sender.redis.RedisConfiguration;
import biz.paluch.logging.gelf.intern.sender.redis.RedisDistributionStrategy;
import biz.paluch.logging.gelf.intern.sender.redis.RedisInstance;


public final class RedisDistributionStrategyFactory {

    private RedisDistributionStrategyFactory() {
        super();
    }

    public static RedisDistributionStrategy createStrategy(List<RedisInstance> redisInstances) {
        if("roundrobin".equalsIgnoreCase(RedisConfiguration.INSTANCE.getDistributionStrategy())) {
            return roundRobin(redisInstances);
        } else if("failover".equalsIgnoreCase(RedisConfiguration.INSTANCE.getDistributionStrategy())) {
            return failover(redisInstances);
        } else if("single".equalsIgnoreCase(RedisConfiguration.INSTANCE.getDistributionStrategy())) {
            return simpleSingle(redisInstances.iterator().next());
        }else {
            if(redisInstances.size() == 1) {
                return simpleSingle(redisInstances.iterator().next());
            } else {
                return roundRobin(redisInstances);
            }
        }
    }
    
    public static RedisDistributionStrategy simpleSingle(RedisInstance instance) {
        return new SimpleSingleInstanceStrategy(instance);
    }
    
    public static RedisDistributionStrategy roundRobin(List<RedisInstance> redisInstances) {
        return new RoundRobinStrategy(redisInstances, RedisConfiguration.INSTANCE.getMaintenanceInterval());
    }
    
    public static RedisDistributionStrategy failover(List<RedisInstance> redisInstances) {
        return new FailoverStrategy(redisInstances, RedisConfiguration.INSTANCE.getMaintenanceInterval());
    }
    

}
