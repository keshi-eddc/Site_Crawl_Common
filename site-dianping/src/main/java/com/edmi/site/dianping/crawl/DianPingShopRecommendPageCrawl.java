package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendPage;
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

public class DianPingShopRecommendPageCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopInfo shop;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopRecommendPageCrawl(DianpingShopInfo shop) {
		super();
		this.shop = shop;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + shop.getShopId() +"/dishlist");
		header.setReferer("http://www.dianping.com/shop/" + shop.getShopId());
		String html = DianPingCommonRequest.getShopRecommend(header);
		if(StringUtils.isNotEmpty(html)) {
			Document doc = Jsoup.parse(html);
			
			int totalPage = DianpingParser.parseShopRecommendTotalPage(doc);
			List<SqlEntity> sqlEntityList = new ArrayList<>();
			if (totalPage == 0) {
				DianpingShopRecommendPage recommendPage = new DianpingShopRecommendPage();
				recommendPage.setShopId(shop.getShopId());
				recommendPage.setTotalPage(totalPage);
				recommendPage.setPage(0);
				sqlEntityList.add(new SqlEntity(recommendPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
			} else {
				for (int page = 1; page <= totalPage; page++) {
					// 解析成功，如果是第一页，先将这一页的推荐菜解析，并且第一个页的状态置为200
					DianpingShopRecommendPage recommendPage = new DianpingShopRecommendPage();
					recommendPage.setShopId(shop.getShopId());
					recommendPage.setTotalPage(totalPage);
					recommendPage.setPage(page);
					if (page == 1) {
						recommendPage.setStatus(200);
						
						List<DianpingShopRecommendInfo> recommendList = DianpingParser.parseShopRecommend(doc, shop, page);
						for (DianpingShopRecommendInfo recommend : recommendList) {
							FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(recommend,
									DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
						}
						
					} else {
						recommendPage.setStatus(-1);
					}
					sqlEntityList.add(new SqlEntity(recommendPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
				}
			}
			iGeneralJdbcUtils.batchExecute(sqlEntityList);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

		DianPingCommonRequest.refreshShopRecommendCookie();
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1000 * from Dianping_ShopInfo A "
				+ "where not exists (select 1 from Dianping_Shop_Recommend_Page B where A.shop_id = B.shop_id) "
//				+ "and shop_id in ('72351070')"
				+ "order by shop_id"
				);
		
		while (true) {
			List<DianpingShopInfo> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopInfo.class);
			if (CollectionUtils.isNotEmpty(urls)) {
				
				ExecutorService pool = Executors.newFixedThreadPool(5);
				
				for (DianpingShopInfo shopInfo : urls) {
					pool.execute(new DianPingShopRecommendPageCrawl(shopInfo));
				}
				
				pool.shutdown();

				while (true) {
					if (pool.isTerminated()) {
						log.error("大众点评-refresh shop comment total page 抓取完成");
						break;
					} else {
						try {
							TimeUnit.SECONDS.sleep(60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			} else {
				break;
			}
		}
		
	}
	
}
