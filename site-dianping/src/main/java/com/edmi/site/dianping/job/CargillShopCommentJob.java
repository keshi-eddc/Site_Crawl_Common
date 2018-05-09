package com.edmi.site.dianping.job;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.DianPingShopCommentCrawl;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo_Cargill;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.cache.utils.ClassUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-嘉吉
 * 店铺评论
 * @author conner
 *
 */
public class CargillShopCommentJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
//		DianPingCommonRequest.refreshShopRecommendCookie();
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct shop_id as shop_id from " + ClassUtils.getTableName(DianpingShopInfo_Cargill.class) + " A "
//				+ "where shop_id in ('2278378')"
				+ "order by shop_id"
				);
		
		List<DianpingShopInfo> shopList = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingShopInfo.class);
		
		ExecutorService pool = Executors.newFixedThreadPool(2);
		
		for (DianpingShopInfo shop : shopList) {
			pool.execute(new DianPingShopCommentCrawl(shop, false));
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("嘉吉-点评-店铺评论-抓取完成");
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
