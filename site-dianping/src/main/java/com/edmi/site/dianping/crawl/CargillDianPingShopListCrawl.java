package com.edmi.site.dianping.crawl;

import org.apache.log4j.Logger;

import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class CargillDianPingShopListCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 1:按照品类; 2:按照关键词
	 */
	private int type;
	
	private String keyword;
	
	private String cityId;
	
	private String cityEnName;
	
	private String cityCnName;
	
	private String primaryCategory;
	
	private String primaryCategoryId;
	
	private String category;
	
	private String categoryId;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public CargillDianPingShopListCrawl(int type, String keyword, String cityId, String cityEnName, String cityCnName,
			String primaryCategory, String primaryCategoryId, String category, String categoryId) {
		super();
		this.type = type;
		this.keyword = keyword;
		this.cityId = cityId;
		this.cityEnName = cityEnName;
		this.cityCnName = cityCnName;
		this.primaryCategory = primaryCategory;
		this.primaryCategoryId = primaryCategoryId;
		this.category = category;
		this.categoryId = categoryId;
		iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		HttpRequestHeader header = new HttpRequestHeader();
		if (type == 1) {
			header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId);
		} else {
			header.setUrl("http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId + "_" + keyword);
		}
		
		String html = DianPingCommonRequest.getShopList(header);
	}
	
}
