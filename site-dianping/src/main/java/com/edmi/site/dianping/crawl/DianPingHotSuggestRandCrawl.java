package com.edmi.site.dianping.crawl;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 抓取点评APP端的实时榜，菜品榜
 * @author conner
 *
 */
@Component
public class DianPingHotSuggestRandCrawl {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingHotSuggestRandCrawl() {
		super();
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}
	
	public void realTimeRank() {
		for (String cityId : new String [] {"1", "2", "4", "7"}) {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("https://mapi.dianping.com/mapi/hotsuggestranklist.json?categoryid=0&cityid=" + cityId + "&type=1&mylng=&mylat=0&_=" + System.currentTimeMillis());
			String html = DianPingCommonRequest.getRealTimeRank(header);
			log.info(html);
		}
	}
	
	public void dishRank() {
		for (String cityId : new String [] {"1", "2", "4", "7"}) {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("https://mapi.dianping.com/mapi/hotsuggestranklist.json?categoryid=0&cityid=" + cityId + "&type=2&mylng=&mylat=0&_=" + System.currentTimeMillis());
			String html = DianPingCommonRequest.getDishRank(header);
			log.info(html);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		new DianPingHotSuggestRandCrawl().realTimeRank();
//		new DianPingHotSuggestRandCrawl().dishRank();
	}
	
}
