package biz.paluch.logging.gelf.intern.sender.redis.distribution;

import biz.paluch.logging.gelf.intern.sender.redis.RedisDistributionStrategy;
import biz.paluch.logging.gelf.intern.sender.redis.RedisInstance;

/**
 * 
 * Simple strategy
 *
 */
class SimpleSingleInstanceStrategy implements RedisDistributionStrategy {

    private RedisInstance instance;
    
    SimpleSingleInstanceStrategy(RedisInstance instance) {
        this.instance = instance;
    }


    @Override
    public RedisInstance getInstance() {
        return instance;
    }

 
}
