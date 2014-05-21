package biz.paluch.logging.gelf.intern.sender.redis.distribution;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import biz.paluch.logging.gelf.intern.sender.redis.RedisDistributionStrategy;
import biz.paluch.logging.gelf.intern.sender.redis.RedisInstance;


class RoundRobinStrategy extends MaintenanceStrategy implements RedisDistributionStrategy  {
    
    private AtomicInteger safeIndex = new AtomicInteger(0);
    private RedisInstance[] redisIntances;
    private int maxValue;
    private Random failoverRandom = new Random();
    
    RoundRobinStrategy(List<RedisInstance> redisInstances, long maintanceInterval) {
        super(maintanceInterval);
        this.redisIntances = redisInstances.toArray(new RedisInstance[redisInstances.size()]);
        this.maxValue = this.redisIntances.length-1;
    }

    
    @Override
    public RedisInstance getInstance() {
        
        // Threadsafe increment with b
        int currentIndex = 0, newIndex =0, tryCounter =0;
        do {
            currentIndex = safeIndex.get();
            newIndex = (currentIndex == maxValue) ? 0 : (currentIndex + 1);
            tryCounter++;
        } while (!safeIndex.compareAndSet(currentIndex, newIndex) && tryCounter < 10);
        
        //int nextIndex = safeIndex.compareAndSet(maxValue, 0) ? maxValue : safeIndex.getAndIncrement();
        RedisInstance instance = redisIntances[currentIndex];
        
        if(isInErrorState(instance)) {
            instance = searchFailoverInstance(currentIndex);
        }
        return instance;
    }

    private RedisInstance searchFailoverInstance(int failureIndex) {

        // Zufallszahl für ungefähre Gleichverteilung im Failoverfall
        int randomIndex = failureIndex;
        while(randomIndex == failureIndex) {
            randomIndex = failoverRandom.nextInt(redisIntances.length);
        }
        
        RedisInstance failoverInstance = getValidInstanceInRange(randomIndex, redisIntances.length);
        if(failoverInstance == null && randomIndex > 0) {
            failoverInstance = getValidInstanceInRange(0, randomIndex);
        }
        
        return failoverInstance;
    }
    
    private RedisInstance getValidInstanceInRange(int startIndex, int endIndex) {
        for(int i=startIndex; i < endIndex; i++) {
            if(!redisIntances[i].isInErrorState()) {
                return redisIntances[i];
            }
        }
        return null;
    }
  

}
