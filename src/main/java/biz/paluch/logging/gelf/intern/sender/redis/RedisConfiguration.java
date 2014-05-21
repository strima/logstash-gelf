package biz.paluch.logging.gelf.intern.sender.redis;


/**
* Singleton for administration of commonly used redis instances
* 
* @author (c) https://github.com/Batigoal/logstash-gelf.git
*
*/
public enum RedisConfiguration {
    INSTANCE;

    /**
     * Configures the number of retries for getting a connection to a redis instance
     * on logging.
     * (Default = Number of configured instances)
     */
    public int getMaxConnectionRetries() {
        return Integer.parseInt(System.getProperty("logging.redis.maxconnectionretries", "-1"));
    }
    
    /**
     * Configures maximum number of connection errors within 
     * maintainance interval before setting redis instance in error state
     * (Default = 3)
     */
    public int getMaxConnectionErrors() {
        return Integer.parseInt(System.getProperty("logging.redis.maxconnectionerrors", "3"));
    }
    
    /**
     * Configures timeout in milliseconds to wait for creating a connection to a
     * redis instance 
     * (Default = 10000)
     */
    public long getConnectionCreationTimeout() {
        return Long.parseLong(System.getProperty("logging.redis.connectioncreationtimeout", "10000"));
    }

    /**
     * Configures the maintenance interval of redis instances to
     * recover from error state and/or clear the number of already
     * occured erros
     * (Default = 600000 (10 Minutes))
     */
    public long getMaintenanceInterval() {
        return Long.parseLong(System.getProperty("logging.redis.maintenanceinterval", "600000"));
    }
    
    /**
     * Configures the maximum number of logged errors  to std.err
     * within a minute in case of failures of redis logging
     * (Default = 15)
     */
    public int getMaxLoggedErrorsInMinute() {
        return Integer.parseInt(System.getProperty("logging.redis.maxloggederrorsinminute", "15"));
    }

    /**
     * Defines the strategy to use for distribution to configured redis instances (roundrobin, failover, single)
     * (Defaults: roundrobin > 1 instances, Single instance == 1 instance)
     * @return
     */
    public String getDistributionStrategy() {
        return System.getProperty("logging.redis.distributionstrategy");
    }
    
    /**
     * Defines the strategy to use for distribution to configured redis instances (roundrobin, failover, single)
     * (Defaults: roundrobin > 1 instances, Single instance == 1 instance)
     * @return
     */
    public boolean isUnsentLoggerActivated() {
        return Boolean.parseBoolean(System.getProperty("logging.gelf.activateunsentlogger","false"));
    }
    
    

}