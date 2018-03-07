package fun.jerry.cache.holder.thread;

import java.util.List;

import fun.jerry.cache.FirstSqlCache;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.entity.SqlEntity;

/**
 * 负责从二级缓存获取，执行sql
 * @author conner
 *
 */
public class ExecuteCacheThread implements Runnable {
	
//	private Logger log = CacheLogConfig.getCacheLog();
	
	public ExecuteCacheThread() {
		super();
	}

	@Override
	public void run() {
		List<SqlEntity> list = FirstSqlCache.getBatch();
		((IGeneralJdbcUtils<?>)ApplicationContextHolder.getBean(GeneralJdbcUtils.class)).batchExecute(list);
	}
	
}
