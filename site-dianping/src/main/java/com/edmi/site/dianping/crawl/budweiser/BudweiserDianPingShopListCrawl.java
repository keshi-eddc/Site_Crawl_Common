package com.edmi.site.dianping.crawl.budweiser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.DianPingUserInfoCrawl;
import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo_Budweiser;
import com.edmi.site.dianping.entity.DianpingShopInfo_Cargill;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.http.DianPingTaskRequest;
import com.edmi.site.dianping.parse.DianpingParser;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class BudweiserDianPingShopListCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingSubCategorySubRegion ss;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public BudweiserDianPingShopListCrawl(DianpingSubCategorySubRegion ss) {
		super();
		this.ss = ss;
		iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		
		int totalPage = 50;
		
		for (int page = 1; page <= totalPage; page ++) {
			while (true) {
				HttpRequestHeader header = new HttpRequestHeader();
				header.setUrl(ss.getUrl() + "p" + page);
				header.setProxyType(ProxyType.PROXY_STATIC_DLY);
				header.setProject(Project.BUDWEISER);
				header.setSite(Site.DIANPING);
				
				String pageHtml = DianPingCommonRequest.getShopList(header);
				
				Document pageDoc = Jsoup.parse(pageHtml);
				
				// 如果没有发现店铺列表，说明没有抓取成功，继续抓取
				if (pageHtml.contains("没有找到符合条件的商户")) {
					log.info(header.getUrl() + " 请求成功，没有找到符合条件的商户,停止抓取");
					totalPage = 0;
					DianpingSubCategorySubRegion tempSS = new DianpingSubCategorySubRegion();
					tempSS.setUrl(ss.getUrl());
					tempSS.setShopTotalPage(totalPage);
					iGeneralJdbcUtils.execute(new SqlEntity(tempSS, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
					break;
				} else {
					Elements shopElements = pageDoc.select("#shop-all-list ul li");
					if (CollectionUtils.isEmpty(shopElements)) {
						log.info(header.getUrl() + " 请求成功，未发现店铺列表,继续抓取");
						continue;
					}
				}
				
				if (page == 1) {
					totalPage = DianpingParser.parseShopListPage(pageDoc);
					DianpingSubCategorySubRegion tempSS = new DianpingSubCategorySubRegion();
					tempSS.setUrl(ss.getUrl());
					tempSS.setShopTotalPage(totalPage);
					iGeneralJdbcUtils.execute(new SqlEntity(tempSS, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
					log.info("总页数 " + totalPage + " " + header.getUrl());
				}
				
				List<DianpingShopInfo> list = DianpingParser.parseShopList(pageDoc, page);
//				if (list.size() == 15 || page == totalPage) {
				if (CollectionUtils.isNotEmpty(list)) {
					save(list, page, totalPage);
					break;
				} else {
					log.info(header.getUrl() + " 第 " + page + " 页 抓取到的店铺数量不足15条 " + list.size() + " totalPage " + totalPage);
				}
			}
		}
		
	}
	
	private void save(List<DianpingShopInfo> list, int page, int totalPage) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (DianpingShopInfo shop : list) {
				DianpingShopInfo_Budweiser shopBudweiser = new DianpingShopInfo_Budweiser();
				BeanUtils.copyProperties(shop, shopBudweiser);
				
				shopBudweiser.setPage(page);
				shopBudweiser.setTotalPage(totalPage);
				shopBudweiser.setSubCategoryId(ss.getSubCategoryId());
				shopBudweiser.setCategoryId(ss.getCategoryId());
				shopBudweiser.setPrimaryCategoryId(ss.getPrimaryCategoryId());
				shopBudweiser.setSubRegionId(ss.getSubRegionId());
				shopBudweiser.setRegionId(ss.getRegionId());
				shopBudweiser.setCityId(ss.getCityId());
				
				SqlEntity sqlEntity = new SqlEntity(shopBudweiser, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT);
				FirstCacheHolder.getInstance().submitFirstCache(sqlEntity);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

		int count = 0;
		try {
			while (true) {
				count ++;
				System.out.println("##################" + count);
				List<DianpingSubCategorySubRegion> list = DianPingTaskRequest.getSubCategorySubRegionTask();
				log.info("获取未抓取用户个数：" + list.size());
				if (CollectionUtils.isNotEmpty(list)) {
					ExecutorService pool = Executors.newFixedThreadPool(5);
					for (DianpingSubCategorySubRegion ss : list) {
						pool.submit(new BudweiserDianPingShopListCrawl(ss));
					}
					
					pool.shutdown();

					while (true) {
						if (pool.isTerminated()) {
							log.error("大众点评-refresh DianpingSubCategorySubRegion 抓取完成");
							break;
						} else {
							try {
								TimeUnit.SECONDS.sleep(60);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				} else {
					System.out.println("$$$$$$$$$$$$$$$" + count);
					try {
						TimeUnit.MINUTES.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("##################" + count);
		}
		
	}
	
}
