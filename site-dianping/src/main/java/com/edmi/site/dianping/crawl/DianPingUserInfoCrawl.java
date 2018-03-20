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
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendPage;
import com.edmi.site.dianping.entity.DianpingUserInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.parse.DianpingParser;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.proxy.enumeration.ProxyType;

public class DianPingUserInfoCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopComment comment;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingUserInfoCrawl(DianpingShopComment comment) {
		super();
		this.comment = comment;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
//		WebDriverConfig config = new WebDriverConfig();
//		config.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		WebDriver driver = WebDriverSupport.getChromeDriverInstance(config);
		try {
			HttpRequestHeader header = new HttpRequestHeader();
//			header.setUrl("http://www.dianping.com/member/" + comment.getUserId());
			header.setUrl("https://m.dianping.com/userprofile/ajax/profileinfo?id=" + comment.getUserId());
//			header.setReferer("http://www.dianping.com/shop/" + comment.getShopId());
//			driver.get(header.getUrl());
			String html = DianPingCommonRequest.getUserInfo(header);
//			String html = driver.getPageSource();
			
//			if(StringUtils.isNotEmpty(html) && !html.contains("页面无法访问")) {
//				DianpingUserInfo user = DianpingParser.parseUserInfo_PC(Jsoup.parse(html), comment);
//				iGeneralJdbcUtils.execute(new SqlEntity(user, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
//			}
			
			if(StringUtils.isNotEmpty(html)) {
				Document doc = Jsoup.parse(html);
				JSONObject jsonObj = JSONObject.parseObject(doc.body().text());
				if (jsonObj.containsKey("code") && jsonObj.getInteger("code") == 200) {
					DianpingUserInfo user = DianpingParser.parseUserInfo_Mobile(jsonObj, comment);
//					iGeneralJdbcUtils.execute(new SqlEntity(user, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(user, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			driver.close();
//			driver.quit();
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

//		DianPingCommonRequest.refreshUserInfoCookie();
//		DianPingCommonRequest.refreshShopRecommendCookie();
		
		StringBuilder sql = new StringBuilder();
		sql.append("with comment as ( "
					+ "	select user_id, user_name, max(shop_id) as shop_id from dbo.Dianping_Shop_Comment "
					+ "	where len(user_id) > 0 "
					+ "	group by user_id, user_name "
					+ ") select top 5000 * from comment A "
					+ "where not exists (select 1 from dbo.Dianping_User_Info B where A.user_id = B.user_id) "
				);
		
		while (true) {
			List<DianpingShopComment> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopComment.class);
			if (CollectionUtils.isNotEmpty(urls)) {
				ExecutorService pool = Executors.newFixedThreadPool(5);
				for (DianpingShopComment comment : urls) {
					pool.execute(new DianPingUserInfoCrawl(comment));
				}
				
				pool.shutdown();

				while (true) {
					if (pool.isTerminated()) {
						log.error("大众点评-refresh user 抓取完成");
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
