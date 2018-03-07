package fun.jerry.cache.holder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.holder.thread.ExecuteCacheThread;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;

/**
 * 一级缓存入口
 * @author conner
 *
 */
public class ExecuteCacheHolder {

	private static Logger log = CacheLogConfig.getCacheLog();
	
	private static ExecuteCacheHolder instance = new ExecuteCacheHolder();
	
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10000);
	
	/** 执行sql的最大线程数 */
	private final static int poolSize = 5;
	
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(poolSize, poolSize + 5, 60, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.CallerRunsPolicy());
	
	private ExecuteCacheHolder() {
	}

	public static ExecuteCacheHolder getInstance() {
		return instance;
	}

	public void submitExecuteCache() {
		try {
			pool.submit(new ExecuteCacheThread());
			int size = pool.getQueue().size();
			log.info("执行SQL的任务队列size ： " + size);
			if (size == 0) {
				log.info("执行SQL的队列中暂时没有需要执行的任务");
			}
		} catch (Exception e) {
			log.error("submit execute cache error.", e);
		}
	}

	public BlockingQueue<Runnable> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<Runnable> queue) {
		this.queue = queue;
	}
	
}
