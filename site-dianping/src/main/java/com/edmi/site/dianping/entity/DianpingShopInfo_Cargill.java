package com.edmi.site.dianping.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_ShopInfo_Cargill")
public class DianpingShopInfo_Cargill extends DianpingShopInfo {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@ColumnMapping("keyword")
	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
