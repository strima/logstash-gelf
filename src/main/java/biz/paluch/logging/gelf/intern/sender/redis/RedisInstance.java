package biz.paluch.logging.gelf.intern.sender.redis;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * A single entry of a redis connection with given configuration and 
 * underlaying real connection pool (jedis)
 *  
 * (c) https://github.com/Batigoal/logstash-gelf.git
 *
 */
public class RedisInstance {

    private String connectionString;
    private String redisKey;
    private Pool<Jedis> jedisPool;
    
    private int maxErrorCount;
    private long errorTimestamp = 0;
    private AtomicBoolean errorState = new AtomicBoolean(false);
    private AtomicInteger errorCount = new AtomicInteger(0);
    
    
    
    public RedisInstance(String connectionString, String redisKey, Pool<Jedis> jedisPool, int maxErrorCount) {
        super();
        this.redisKey = redisKey;
        this.jedisPool = jedisPool;
        this.connectionString = connectionString;
        this.maxErrorCount = maxErrorCount;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public Pool<Jedis> getPool() {
        return jedisPool;
    }

    public String getConnectionString() {
        return connectionString;
    }
    
    public RedisConnection createConnection() {
        if(isInErrorState()) {
            throw new IllegalStateException("Cannot create connection - redis instance is in error state!");
        }
        return new RedisConnectionImpl(jedisPool.getResource(),this);
    }
    
    public boolean isInErrorState() {
        return errorState.get();
    }
    
    public long getErrorTimestamp() {
        return errorTimestamp;
    }

    public void incrementErrorCount() {
        this.errorTimestamp = System.currentTimeMillis();
        int actualerrorCount = this.errorCount.incrementAndGet();
        if(actualerrorCount == maxErrorCount) {
            this.errorState.compareAndSet(false,true);
        }
    }

    public int getErrorCount() {
        return errorCount.intValue();
    }

    public void reset() {
        if(this.errorState.compareAndSet(true,false)) {
            this.errorCount.set(0);
            this.errorTimestamp = 0;
        }
    }

    public static class RedisConnectionImpl implements RedisConnection {

        private RedisInstance redisInstance;
        private Jedis jedisClient;
        
        RedisConnectionImpl(Jedis jedisClient,RedisInstance instance) {
            super();
            this.redisInstance = instance;
            this.jedisClient = jedisClient;
        }

        @Override
        public String getRedisKey() {
            return redisInstance.getRedisKey();
        }

        @Override
        public String getConnectionString() {
           return redisInstance.getConnectionString();
        }

        @Override
        public void write(String message) {
            jedisClient.lpush(getRedisKey(), message);
        }

        @Override
        public void close() {
            redisInstance.getPool().returnResourceObject(jedisClient);
        }

        @Override
        public String toString() {
            return "RedisConnectionImpl [getRedisKey()=" + getRedisKey() + ", getConnectionString()=" + getConnectionString() + "]";
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionString == null) ? 0 : connectionString.hashCode());
        result = prime * result + ((redisKey == null) ? 0 : redisKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RedisInstance other = (RedisInstance) obj;
        if (connectionString == null) {
            if (other.connectionString != null)
                return false;
        } else if (!connectionString.equals(other.connectionString))
            return false;
        if (redisKey == null) {
            if (other.redisKey != null)
                return false;
        } else if (!redisKey.equals(other.redisKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RedisInstance [connectionString=" + connectionString + ", redisKey=" + redisKey + "]";
    }
    
    
    
}
