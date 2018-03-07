package fun.jerry.entity;

/**
 * 数据源常量类
 * @author conner
 */
public enum DataSource {
	
	DATASOURCE_CrawlElve("", ""),
	DATASOURCE_EPOS("", ""),
	DATASOURCE_SGM("", ""),
	DATASOURCE_Budweiser("", ""),
	DATASOURCE_SouFang("", ""),
	DATASOURCE_ProxyIP("", ""),
	DATASOURCE_DianPing("", ""),
	DATASOURCE_Monitor("", "");
	
	public String code;
	
	public String name;

	private DataSource(String code, String name) {
		this.code = code;
		this.name = name;
	}
}