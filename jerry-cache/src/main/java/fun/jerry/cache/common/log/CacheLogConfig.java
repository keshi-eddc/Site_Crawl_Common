package fun.jerry.cache.common.log;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class CacheLogConfig {

	private static final Logger cacheLog;
	
	/**
	 * log4j配置文件路径
	 */
	//private static final String CONF_FILE_PATH = CacheLogConfig.class.getResource("/").getPath() + "/cache_log.properties";
	
	private static final InputStream CONF_FILE_PATH = CacheLogConfig.class.getResourceAsStream("/cache_log.properties");
	
	static {
		
		try {
			PropertyConfigurator.configure(CONF_FILE_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cacheLog = Logger.getLogger("cache.log");
		
	}

	public static Logger getCacheLog() {
		return cacheLog;
	}
}
