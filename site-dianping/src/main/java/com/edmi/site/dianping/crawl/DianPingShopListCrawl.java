package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.DataSource;
import fun.jerry.entity.SqlEntity;
import fun.jerry.entity.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class DianPingShopListCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingSubCategorySubRegion subCategorySubRegion;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopListCrawl(DianpingSubCategorySubRegion subCategorySubRegion) {
		super();
		this.subCategorySubRegion = subCategorySubRegion;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		crawl();
	}
	
	private void crawl() {

		StringBuilder sql = new StringBuilder();
		sql.append("select top 1000 * from Dianping_SubCategory_SubRegion where shop_total_page = -1 ")
//			.append("and sub_category_id in (select sub_category_id from dbo.Dianping_City_SubCategory ")
//				.append("where primary_category = '" + primaryCategory + "' ")
//				.append(StringUtils.isNotEmpty(category) ? "and category = '" + category + "' " : " ")
//				.append(StringUtils.isNotEmpty(subCategory) ? "and sub_category = '" + subCategory + "' " : " ")
//				.append(")")
//			.append("and sub_region_id in (select sub_region_id from dbo.Dianping_City_SubRegion ")
//				.append(" where city_cnname = '" + cityCnname + "' ")
//				.append(StringUtils.isNotEmpty(region) ? "and region = '" + region + "' " : " ")
//				.append(StringUtils.isNotEmpty(subRegion) ? "and sub_region = '" + subRegion + "' " : " ")
//				.append(")")
			;
		
		while (true) {
			
			List<DianpingSubCategorySubRegion> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingSubCategorySubRegion.class);
			
			List<SqlEntity> updateList = new ArrayList<>();
			
			ExecutorService pool = Executors.newFixedThreadPool(10); 
			
			for (DianpingSubCategorySubRegion sub : urls) {
				sub.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						HttpRequestHeader header = new HttpRequestHeader();
						header.setUrl(sub.getUrl());
						String html = DianPingCommonRequest.getShopList(header);
						Document doc = Jsoup.parse(html);
						Element shopTag = doc.select("#shop-all-list").first();
						if (null != shopTag) {
							Elements shopElements = doc.select("#shop-all-list ul li");
							// 如果发现有店铺列表，找出有多少页
							if (CollectionUtils.isNotEmpty(shopElements)) {
								Element totalPageEle = doc.select(".page .PageLink").last();
								int totalPage = null == totalPageEle ? 1 : Integer.parseInt(totalPageEle.text().trim());
								sub.setShopTotalPage(totalPage);
							} else {
								sub.setShopTotalPage(-1);
							}
						} else {
							// 如果么有店铺列表，为0页
							sub.setShopTotalPage(0);
						}
//						updateList.add(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
//						iGeneralJdbcUtils.execute(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
						FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
					}
				});
			}
			
			pool.shutdown();

			while (true) {
				if (pool.isTerminated()) {
					log.error("大众点评-抓取完成");
					break;
				} else {
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void parseShopList(Document doc, int page) {
		
	}
	
	private int getTotalPage(Document doc) {
		int totalPage = 50;
		Element page = doc.select(".page .PageLink").last();
		if (null != page) {
			totalPage = Integer.valueOf(page.text().trim());
			String href = page.attr("href").trim();
			aid = href.substring(href.indexOf("aid="));
		}
		return totalPage;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1000 * from Dianping_SubCategory_SubRegion where shop_total_page > 0 ");
		
		while (true) {
			
			IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
			List<DianpingSubCategorySubRegion> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingSubCategorySubRegion.class);
			
			List<SqlEntity> updateList = new ArrayList<>();
			
			ExecutorService pool = Executors.newFixedThreadPool(1); 
			
			for (DianpingSubCategorySubRegion sub : urls) {
				pool.submit(new DianPingShopListCrawl(sub));
			}
			
			pool.shutdown();

			while (true) {
				if (pool.isTerminated()) {
					log.error("大众点评-店铺列表-抓取完成");
					break;
				} else {
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
