package fun.jerry.cache.constant;

public class Constant {
	
	/** 一级缓存的容量 */
	public static final int firstCacheSize = 10000000;
	
	/** 任务队列最大容量 */
	public static final int TASK_MAX_CAPACITY = 100000;

	/** 执行队列容量 */
	public static final int executeQueueSize = 10000;

	/** 批量执行sql的容量 */
	public static final int batchExecuteSize = 500;
	
	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE);
	}
}
