package fun.jerry.cache.holder;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.holder.thread.AddFirstCacheThread;
import fun.jerry.entity.SqlEntity;

/**
 * 一级缓存入口
 * @author conner
 *
 */
public class FirstCacheHolder {

	private static Logger log = CacheLogConfig.getCacheLog();
	
	private static FirstCacheHolder instance = new FirstCacheHolder();
	
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1500000);
	
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 30,
			TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

	private FirstCacheHolder() {
	}

	public static FirstCacheHolder getInstance() {
		return instance;
	}

	public void submitFirstCache(List<SqlEntity> list) {
		try {
			pool.submit(new AddFirstCacheThread(list));
		} catch (Exception e) {
			log.error("batch submit first cache error.", e);
			e.printStackTrace();
		}
	}

	public void submitFirstCache(SqlEntity sqlEntity) {
		try {
			if (null == sqlEntity.getDataSource()) {
				log.error(sqlEntity + " 检测到没有指定数据源，不能提交到缓存队列.");
				return;
			}
			pool.submit(new AddFirstCacheThread(sqlEntity));
		} catch (Exception e) {
			log.error("submit first cache error.", e);
			e.printStackTrace();
		}
	}
	
}
