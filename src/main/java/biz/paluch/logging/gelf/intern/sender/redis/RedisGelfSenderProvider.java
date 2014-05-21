package biz.paluch.logging.gelf.intern.sender.redis;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;
import biz.paluch.logging.gelf.intern.ErrorReporter;
import biz.paluch.logging.gelf.intern.GelfSender;
import biz.paluch.logging.gelf.intern.GelfSenderConfiguration;
import biz.paluch.logging.gelf.intern.GelfSenderProvider;
import biz.paluch.logging.gelf.intern.sender.redis.distribution.RedisDistributionStrategyFactory;

/**
 * 
 * (c) https://github.com/Batigoal/logstash-gelf.git
 *
 */
public class RedisGelfSenderProvider implements GelfSenderProvider {
    
    @Override
    public boolean supports(String host) {
        return host.startsWith("redis");
    }

    @Override
    public GelfSender create(GelfSenderConfiguration configuration) throws IOException {
        String graylogHost = configuration.getHost();

        final String[] singleConnectionList = graylogHost.split(";");
        final List<RedisInstance> redisInstanceList = new ArrayList<RedisInstance>();
        
        for(String singleConnection : singleConnectionList) {
            RedisInstance redisInstance = RedisInstanceManager.INSTANCE.getRedisInstance(URI.create(singleConnection), configuration.getPort());
            redisInstanceList.add(redisInstance);
        }
        final RedisDistributionStrategy redisClientStrategy = RedisDistributionStrategyFactory.createStrategy(redisInstanceList);
        ErrorReporter wrappedErrorReport = new ErrorReporterWrapper(configuration.getErrorReport(), RedisConfiguration.INSTANCE.getMaxLoggedErrorsInMinute());

        int maxRetries = RedisConfiguration.INSTANCE.getMaxConnectionRetries() > 0 ? RedisConfiguration.INSTANCE.getMaxConnectionRetries() :  redisInstanceList.size();;
        return new GelfRedisSender(new RedisConnectionManager(redisClientStrategy,maxRetries,wrappedErrorReport), wrappedErrorReport);
        
    }
  
  
    
}
