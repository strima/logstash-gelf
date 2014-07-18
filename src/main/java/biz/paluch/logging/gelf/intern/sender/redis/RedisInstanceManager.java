package biz.paluch.logging.gelf.intern.sender.redis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
* Singleton for administration of commonly used redis instances
* 
* @author (c) https://github.com/Batigoal/logstash-gelf.git
*
*/
enum RedisInstanceManager {
    INSTANCE;
    
    private final Map<String, RedisInstance> instances = new HashMap<String, RedisInstance>();

    public synchronized RedisInstance getRedisInstance(URI connectionURI, int port) {
        
        String lowerCasedConnectionString = connectionURI.toString().toLowerCase();
        String cleanConnectionString = lowerCasedConnectionString.substring(0,lowerCasedConnectionString.length() - connectionURI.getFragment().length());

        

        if (!instances.containsKey(cleanConnectionString)) {
            JedisPoolManager.INSTANCE.getConfiguration().setMaxWaitMillis(RedisConfiguration.INSTANCE.getConnectionCreationTimeout());
            JedisPoolManager.INSTANCE.getConfiguration().setMaxTotal(RedisConfiguration.INSTANCE.getMaxConnectionPoolSizePerInstance());
            Pool<Jedis> pool = JedisPoolManager.INSTANCE.getJedisPool(connectionURI, port);
            // TODO: Error-Logging konsolidieren
            int maxErrorCount = RedisConfiguration.INSTANCE.getMaxConnectionErrors();
            RedisInstance connection = new RedisInstance(lowerCasedConnectionString,connectionURI.getFragment(),pool,maxErrorCount);
            instances.put(cleanConnectionString, connection);
        }
        return instances.get(cleanConnectionString);
    }
}