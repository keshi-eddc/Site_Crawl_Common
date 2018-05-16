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
import com.edmi.site.dianping.http.DianPingCommonRequest;

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
//		sql.append("with temp as ( "
//				+ "	select row_number() over (partition by shop_id order by review_num desc) rn, * "
//				+ "	from dbo.Dianping_ShopInfo_Cargill "
//				+ "), comment as ( "
//				+ "	select count(1) as num, shop_id from dbo.Dianping_Shop_Comment "
//				+ "	group by shop_id "
//				+ ") "
//				+ "select distinct shop_id, review_num from temp "
//				+ "where shop_id not in ( "
//				+ "	select temp.shop_id "
//				+ "	from temp RIGHT join comment  "
//				+ "	on temp.shop_id = comment.shop_id  "
//				+ "	and review_num <= num  "
//				+ "	where rn = 1 "
//				+ ") and rn = 1 order by review_num desc");
		
		sql.append("with temp as ( "
				+ "	select row_number() over (partition by shop_id order by review_num desc) rn, * "
				+ "	from dbo.Dianping_ShopInfo_Cargill where review_num > 20"
				+ ") select distinct shop_id, review_num from temp "
				+ "where shop_id not in ( "
				+ "select distinct shop_id from dbo.Dianping_Shop_Comment "
				+ ") and rn = 1 order by review_num asc");
		
		List<DianpingShopInfo> shopList = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingShopInfo.class);
		
		ExecutorService pool = Executors.newFixedThreadPool(10);
//		
////		DianPingCommonRequest.refreshShopCommentCookie();
		
		for (DianpingShopInfo shop : shopList) {
			pool.execute(new DianPingShopCommentCrawl(shop, false));
//			new DianPingShopCommentCrawl(shop, false).run();
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
