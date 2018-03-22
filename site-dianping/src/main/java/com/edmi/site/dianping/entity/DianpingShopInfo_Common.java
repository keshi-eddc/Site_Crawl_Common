package com.edmi.site.dianping.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.TableMapping;

public class DianpingShopInfo_Common extends DianpingShopInfo {

	private static final long serialVersionUID = 4217070017854978866L;
	
	private String subCategoryId;
	
	@ColumnMapping("category_id")
	private String categoryId;
	
	private String primaryCategoryId;
	
	@ColumnMapping("sub_region_id")
	private String subRegionId;
	
	private String regionId;
	
	@ColumnMapping("city_id")
	private String cityId;
	
}
