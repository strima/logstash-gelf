package biz.paluch.logging.gelf.intern.sender.redis;

import biz.paluch.logging.gelf.intern.ErrorReporter;


/**
 * Manages a set of pool entries for different client-side strategies 
 * (e.g. roundrobin, single pool) 
 * 
 * (c) https://github.com/Batigoal/logstash-gelf.git
 */
class RedisConnectionManager {
    
    private RedisDistributionStrategy distributionStrategy;
    private ErrorReporter errorReporter;
    private int maxRetries;
    
    RedisConnectionManager(RedisDistributionStrategy distributionStrategy, int maxRetries,  ErrorReporter errorReporter) {
        super();
        this.distributionStrategy = distributionStrategy;
        this.maxRetries = maxRetries;
        this.errorReporter = errorReporter;
    }

    public RedisConnection getConnection() {
        
        for(int i = 0; i < maxRetries; i++) {
            //System.out.println("I "+i);
            RedisInstance redisInstance = distributionStrategy.getInstance();
            if(redisInstance != null && !redisInstance.isInErrorState()) {
                try {
                    return redisInstance.createConnection();
                } catch(Exception e) {
                    redisInstance.incrementErrorCount();
                    errorReporter.reportError("Error on trying to connect to redis-instance "+redisInstance+ " - this instance is in error-state: "+redisInstance.isInErrorState(),e);
                }
            }
        }
        return null;
    }

}
