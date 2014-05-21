package biz.paluch.logging.gelf.intern.sender.redis;


/**
 * Interface for implementing different client-side strategies
 * like round robin or failover
 * 
 * (c) https://github.com/Batigoal/logstash-gelf.git
 *
 */
public interface RedisDistributionStrategy {

    RedisInstance getInstance();
    
}