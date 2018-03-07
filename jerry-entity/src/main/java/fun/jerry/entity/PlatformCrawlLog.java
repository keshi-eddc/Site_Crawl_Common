package fun.jerry.entity;

import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Platform_Crawl_Log")
public class PlatformCrawlLog extends Model {

	private static final long serialVersionUID = 1L;
	
	private Integer brand_id;
	
	private String brand_name;
	
	private String brand_url;

	public PlatformCrawlLog() {
		super();
	}

	public PlatformCrawlLog(Integer brand_id, String brand_name, String brand_url) {
		super();
		this.brand_id = brand_id;
		this.brand_name = brand_name;
		this.brand_url = brand_url;
	}

	@Override
	public String toString() {
		return "AutohomeCarBrand [brand_id=" + brand_id + ", brand_name=" + brand_name + ", brand_url=" + brand_url
				+ "]";
	}

	public Integer getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getBrand_url() {
		return brand_url;
	}

	public void setBrand_url(String brand_url) {
		this.brand_url = brand_url;
	}
	
}
