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

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendPage;
import com.edmi.site.dianping.entity.DianpingUserInfo;
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
		HttpRequestHeader header = new HttpRequestHeader();
//		header.setUrl("http://www.dianping.com/member/" + comment.getUserId());
		header.setUrl("https://m.dianping.com/userprofile/ajax/profileinfo?id=" + comment.getUserId());
//		header.setReferer("http://www.dianping.com/shop/" + comment.getShopId());
		String html = DianPingCommonRequest.getUserInfo(header);
		if(StringUtils.isNotEmpty(html)) {
			Document doc = Jsoup.parse(html);
			
			DianpingUserInfo user = DianpingParser.parseUserInfo(doc, comment);
			
			iGeneralJdbcUtils.execute(new SqlEntity(user, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

//		DianPingCommonRequest.refreshUserInfoCookie();
		DianPingCommonRequest.refreshShopRecommendCookie();
		
		StringBuilder sql = new StringBuilder();
		sql.append("with comment as ( "
					+ "	select user_id, user_name, max(shop_id) as shop_id from dbo.Dianping_Shop_Comment "
					+ "	where len(user_id) > 0 "
					+ "	group by user_id, user_name "
					+ ") select top 1000 * from comment A "
					+ "where not exists (select 1 from dbo.Dianping_User_Info B where A.user_id = B.user_id) "
				);
		
		while (true) {
			List<DianpingShopComment> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopComment.class);
			if (CollectionUtils.isNotEmpty(urls)) {
				ExecutorService pool = Executors.newFixedThreadPool(1);
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
