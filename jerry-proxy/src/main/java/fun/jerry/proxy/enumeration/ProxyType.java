package fun.jerry.proxy.enumeration;

public enum ProxyType {
	
	/**
	 *静态IP，自动切换厂商
	 */
	PROXY_STATIC_AUTO,
	
	/**
	 *静态IP，指定IP厂商为代理云
	 */
	PROXY_STATIC_DLY,
	
	/**
	 *静态IP，指定IP厂商为 DungProxy
	 */
	PROXY_STATIC_DUNG,

	/**
	 *云代理，自动切换
	 */
	PROXY_CLOUD_AUTO,
	
	/**
	 *云代理，指定云代理厂商为ABUYUN
	 */
	PROXY_CLOUD_ABUYUN,

	/**
	 *云代理，指定云代理厂商为国外厂商Luminati
	 */
	PROXY_CLOUD_LUMINATI,
	
	/**
	 * 不使用代理
	 */
	NONE;
	
	public static void main(String[] args) {
		System.out.println(PROXY_CLOUD_ABUYUN.toString());
	}
	
}
