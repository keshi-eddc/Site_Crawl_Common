package fun.jerry.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.constant.Constant;
import fun.jerry.entity.SqlEntity;

/**
 * 一级缓存100万 只接收需要执行的sql
 * 
 * @author conner
 *
 */
public class FirstSqlCache {

	private static Logger log = CacheLogConfig.getCacheLog();

	private final static BlockingQueue<SqlEntity> first = new ArrayBlockingQueue<SqlEntity>(Constant.firstCacheSize);

	public static void add(SqlEntity obj) {
		try {
			if (null != obj) {
				// 判断对象是否在当前内存缓存中已存在，规则查看具体的对象equals方法
				boolean existInMemory = false;
				for (SqlEntity se : first) {
					if (obj.getObj().equals(se.getObj())) {
						existInMemory = true;
						break;
					}
				}
				
				if (!existInMemory) {
					first.add(obj);
				}
			} else {
				log.error("SqlContext is error " + obj);
			}
		} catch (Exception e) {
			log.error("add FirstSqlCache queue error.", e);
		}
	}

	/**
	 * 从队列中获取一条记录
	 * 
	 * @return
	 */
	public static Object poll() {
		Object obj = null;
		try {
			if (!first.isEmpty()) {
				obj = first.poll(100, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
			log.error("first cache poll error.", e);
		}
		return obj;
	}

	public static List<SqlEntity> getBatch() {
//		synchronized (first) {
			List<SqlEntity> objList = new ArrayList<SqlEntity>();
//			while (!first.isEmpty() && objList.size() < Constant.batchExecuteSize) {
//				//objList.add(first.poll());
//			}
			first.drainTo(objList, Constant.batchExecuteSize);
			log.info(Thread.currentThread().getName() + "当前缓存中存在数据量 " + getQueue().size());
			return objList;
//		}
	}

	public static void add(List<SqlEntity> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (SqlEntity obj : list) {
				add(obj);
			}
		}
	}

	public static BlockingQueue<SqlEntity> getQueue() {
		return first;
	}

}
