package biz.paluch.logging.gelf.intern.sender.redis;

import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;
import biz.paluch.logging.gelf.intern.ErrorReporter;
import biz.paluch.logging.gelf.intern.GelfMessage;
import biz.paluch.logging.gelf.intern.GelfSender;

/**
 * (c) https://github.com/strima/logstash-gelf.git
 */
class GelfRedisSender implements GelfSender {
    private RedisConnectionManager connectionManager;
    private ErrorReporter errorReporter;

    GelfRedisSender(RedisConnectionManager connectionManager,ErrorReporter errorReporter) throws IOException {
        this.connectionManager = connectionManager;
        this.errorReporter = errorReporter;
    }

    public boolean sendMessage(GelfMessage message) {
        if (!message.isValid()) {
            return false;
        }

        RedisConnection redisConnection = null;
        try {
            redisConnection = connectionManager.getConnection();
            if(redisConnection == null) {
                errorReporter.reportError("Couldn't send gelf message: Didn't get get a valid connection to redis",null);
                return false;
            } else {
                redisConnection.write(message.toJson(""));
            }
        } catch (Exception e) {
            if(redisConnection != null) {
                errorReporter.reportError("Cannot send REDIS data with key URI " + redisConnection.getRedisKey() + " to connection "+redisConnection.getConnectionString(),e);
            } else {
                errorReporter.reportError("Couldn't send gelf message: Didn't get get a valid connection to redis",e);
            }
            return false;
        }  finally {
            if(redisConnection != null) {
                redisConnection.close();
            }
        }
        return true;
    }

    public void close() {
        // We don't need anything -> we use a connection manager (with pool)!
    }
}
