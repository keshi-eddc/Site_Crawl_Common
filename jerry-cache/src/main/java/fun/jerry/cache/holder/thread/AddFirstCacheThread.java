package fun.jerry.cache.holder.thread;

import java.util.List;

import fun.jerry.cache.FirstSqlCache;
import fun.jerry.entity.system.SqlEntity;

/**
 * 一级缓存线程，负责添加到一级缓存
 * @author conner
 *
 */
public class AddFirstCacheThread implements Runnable {
	
	private SqlEntity sqlEntity;
	
	private List<SqlEntity> list;
	
	/**
	 * 单条添加
	 * @param obj
	 */
	public AddFirstCacheThread(SqlEntity sqlEntity) {
		super();
		this.sqlEntity = sqlEntity;
	}

	/**
	 * 批量添加
	 * @param sqlList
	 */
	public AddFirstCacheThread(List<SqlEntity> list) {
		super();
		this.list = list;
	}

	@Override
	public void run() {
		if (null != sqlEntity) {
			FirstSqlCache.add(sqlEntity);
		}
		FirstSqlCache.add(list);
	}
	
	
}
