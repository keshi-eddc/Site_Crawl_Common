package fun.jerry.cache.holder;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fun.jerry.cache.FirstSqlCache;
import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.constant.Constant;

@Component
public class CacheBootstar {
	
	private Logger log = CacheLogConfig.getCacheLog();
	
	/**
	 * 每隔2秒检查一次缓存队列中是否有需要执行的sql
	 * 每次检查的时候最多向执行队列中提交100次
	 */
//	@Scheduled(cron="0 0/5 * * * ?")
	@Scheduled(cron="${cache.polling.interval}")
	private void execute() {
		if (!FirstSqlCache.getQueue().isEmpty()) {
			int size = FirstSqlCache.getQueue().size();
			int count = (size / Constant.batchExecuteSize) + 1;
			log.info("当前缓存中存在数据量 " + FirstSqlCache.getQueue().size());
//			int leftSize = Constant.executeCacheSize - ExecuteCacheHolder.getInstance().getQueue().size();
//			log.info("execute cache left queue size is " + leftSize);
			for (int i = 0; i < count; i++) {
				ExecuteCacheHolder.getInstance().submitExecuteCache();
			}
		} else {
			log.info("当前缓存中没有数据");
		}
	}
}
