package biz.paluch.logging.gelf.intern.sender.redis;


/**
 * A single temporary redis connection  underlying real connection pool (jedis)
 *  
 * (c) https://github.com/Batigoal/logstash-gelf.git
 *
 */
interface RedisConnection {
    
    String getRedisKey();
    String getConnectionString();
    void write(String message);
    void close();
    
}
