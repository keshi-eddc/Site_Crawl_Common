package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo_Cargill;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.parse.DianpingParser;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
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
	
	private String region;
	
	private String regionId;
	
	private String subRegion;
	
	private String subRegionId;
	
	private String primaryCategory;
	
	private String primaryCategoryId;
	
	private String category;
	
	private String categoryId;
	
	private String subCategory;
	
	private String subCategoryId;
	
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
		
		int totalPage = 1;
		
		for (int page = 1; page <= totalPage; page ++) {
			while (true) {
				HttpRequestHeader header = new HttpRequestHeader();
				if (type == 1) {
					header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId + "o2p" + page);
				} else {
					header.setUrl("http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId.replace("ch", "") + "_" + keyword + "/o2p" + page);
				}
				
				log.info("当前页数 " + page + " 总页数 " + totalPage + " " + header.getUrl());
				
				String pageHtml = DianPingCommonRequest.getShopList(header);
				
				Document pageDoc = Jsoup.parse(pageHtml);
				
				if (page == 1) {
					totalPage = DianpingParser.parseShopListPage(pageDoc);
					log.info("总页数 " + totalPage + " " + header.getUrl());
					if (totalPage > 30) {
						totalPage = 30;
					}
				}
				
				List<DianpingShopInfo> list = DianpingParser.parseShopList(pageDoc, page);
				if (list.size() == 15 || page == totalPage) {
					save(list, page, totalPage);
					break;
				} else {
					log.info(header.getUrl() + " 第 " + page + " 页 抓取到的店铺数量不足15条 " + list.size());
				}
			}
		}
		
	}
	
	private void save(List<DianpingShopInfo> list, int page, int totalPage) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (DianpingShopInfo shop : list) {
				DianpingShopInfo_Cargill shopCargill = new DianpingShopInfo_Cargill();
				BeanUtils.copyProperties(shop, shopCargill);
				
				shopCargill.setSource(type == 1 ? "category" : "keyword");
				shopCargill.setPage(page);
				shopCargill.setTotalPage(totalPage);
				shopCargill.setSubCategoryId("");
				shopCargill.setCategoryId(StringUtils.isNotEmpty(categoryId) ? categoryId : "");
				shopCargill.setPrimaryCategoryId(StringUtils.isNotEmpty(primaryCategoryId) ? primaryCategoryId : "");
				shopCargill.setSubRegionId(StringUtils.isNotEmpty(subRegionId) ? subRegionId : "");
				shopCargill.setRegionId(StringUtils.isNotEmpty(regionId) ? regionId : "");
				shopCargill.setCityId(StringUtils.isNotEmpty(cityId) ? cityId : "");
				shopCargill.setKeyword(StringUtils.isNotEmpty(keyword) ? keyword : "");
				
				SqlEntity sqlEntity = new SqlEntity(shopCargill, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS);
				FirstCacheHolder.getInstance().submitFirstCache(sqlEntity);
			}
		}
	}
	
}
