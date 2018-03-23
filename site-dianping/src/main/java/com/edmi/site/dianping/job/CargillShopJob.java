package com.edmi.site.dianping.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.CargillDianPingShopListCrawl;
import com.edmi.site.dianping.crawl.DianPingSubCategorySubRegionCrawl;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-嘉吉
 * 17个城市，按城市，品类，人气抓取点评店铺Top30页
 * @author conner
 *
 */
public class CargillShopJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	public static void main(String[] args) {
		
		String[] keywords = new String[] {"汉堡", "鸡排", "鸡块", "鸡翅", "鸡柳"};
		
		// 韩国料理,咖啡厅,面包甜点,日本菜,台湾菜,西餐,粤菜
		String[] categories = new String[] {"g114", "g132", "g117", "g113", "g107", "g116", "g103"};
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils<?> iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		List<Map<String, Object>> mapList = iGeneralJdbcUtils.queryForListMap(new SqlEntity(
				"select * from dbo.City_DianPing where cityName in ("
				+ "'北京', '上海', '广州', '深圳', '南昌', '太原', '沈阳', '西安', '南宁', '成都', '杭州', '泉州', '潍坊', '吉林', '洛阳', '贵阳', '兰州')",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		
		ExecutorService pool = Executors.newFixedThreadPool(5);
		
		for (Map<String, Object> map : mapList) {
			
			for (String keyword : keywords) {
				pool.submit(new CargillDianPingShopListCrawl(2, keyword, map.get("cityId").toString(), null, null, null, "ch10", "", ""));
			}
			
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("嘉吉-点评-关键词搜索店铺-抓去完成");
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
