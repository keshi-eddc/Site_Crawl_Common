package fun.jerry.common.enumeration;

public enum ProxyVendor {
	
	/**
	 *静态IP，指定IP厂商为代理云
	 */
	DLY,
	
	/**
	 *静态IP，指定IP厂商为 DungProxy
	 */
	DUNG,

	/**
	 *云代理，指定云代理厂商为ABUYUN
	 */
	ABUYUN,

	/**
	 *云代理，指定云代理厂商为国外厂商Luminati
	 */
	LUMINATI;
	
	
	public static void main(String[] args) {
		System.out.println(DUNG.toString());
	}
	
}
