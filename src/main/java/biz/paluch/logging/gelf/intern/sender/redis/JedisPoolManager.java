package biz.paluch.logging.gelf.intern.sender.redis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
* Singleton for administration of commonly used jedis pools 
* 
* @author (c) https://github.com/Batigoal/logstash-gelf.git
*
*/

enum JedisPoolManager {
    INSTANCE;
    
    private final Map<String, JedisPool> pools = new HashMap<String, JedisPool>();
    private final JedisPoolConfig configuration = new JedisPoolConfig();
    
    public JedisPoolConfig getConfiguration() {
        return configuration;
    }

    public synchronized JedisPool getJedisPool(URI connectionURI, int configuredPort, String configuredPassword) {
        
        String lowerCasedConnectionString = connectionURI.toString().toLowerCase();
        String cleanConnectionString = lowerCasedConnectionString.substring(0,lowerCasedConnectionString.length() - connectionURI.getFragment().length());
        
        if (!pools.containsKey(cleanConnectionString)) {

            String password = configuredPassword;
            if (connectionURI.getUserInfo() != null) {
                password = connectionURI.getUserInfo().split(":", 2)[1];
            }
            int database = Protocol.DEFAULT_DATABASE;
            if(connectionURI.getPath() != null && connectionURI.getPath().length() > 1) {
                database = Integer.parseInt(connectionURI.getPath().split("/", 2)[1]);
            }
            int port = connectionURI.getPort() > 0 ? connectionURI.getPort() : configuredPort;
            JedisPool newPool = new JedisPool(
                    getConfiguration(), 
                    connectionURI.getHost(), 
                    port,
                    10000, 
                    password, 
                    database);
            
            pools.put(cleanConnectionString, newPool);
        }
        return pools.get(cleanConnectionString);
    }
}
