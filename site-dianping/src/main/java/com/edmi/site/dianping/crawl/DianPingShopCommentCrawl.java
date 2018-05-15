package com.edmi.site.dianping.crawl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.DateFormatSupport;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 店铺-评论抓取
 * @author conner
 *
 */
public class DianPingShopCommentCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 是否开启增量抓取
	 */
	private boolean increment;
	
	private DianpingShopInfo dianpingShopInfo;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopCommentCrawl(DianpingShopInfo dianpingShopInfo, boolean increment) {
		super();
		this.dianpingShopInfo = dianpingShopInfo;
		this.increment = increment;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		int totalPage = getTotalPage();
		List<Integer> pageList = new ArrayList<>();
		for (int page = 1; page <= totalPage; page ++) {
			pageList.add(page);
		}
		for (int page = 2; page <= totalPage; page ++) {
			if (parseComment(page, totalPage)) {
				break;
			}
		}
		
	}
	
	private boolean parseComment(int page, int totalPage) {
		boolean flag = false;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all/p" + page + "?queryType=sortType&queryVal=latest");
		if (page == 1) {
			header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all?queryType=sortType&queryVal=latest");
		} else {
			header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all/p" + (page - 1) + "?queryType=sortType&queryVal=latest");
		}
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		String html = DianPingCommonRequest.getShopComment(header);
		Document doc = Jsoup.parse(html);
		Elements commentList = doc.select(".reviews-items ul li .main-review");
		if (CollectionUtils.isNotEmpty(commentList)) {
			
			log.info("总页数 " + totalPage + " 当前页数 " + page + " 该页有 " + commentList.size() + " 条记录");
			
			for (Element shop : commentList) {
				try {

					DianpingShopComment comment = new DianpingShopComment();
					comment.setShopId(dianpingShopInfo.getShopId());
					
					comment.setPage(page);
					
					Element userIdEle = shop.select("a[href*=member]").first();
					comment.setUserId(null != userIdEle ? userIdEle.attr("href").replace("/member/", "") : "");
					comment.setUserName(null != userIdEle ? userIdEle.text() : "");
					
					Element userLevelEle = shop.select(".dper-info .user-rank-rst").first();
					comment.setUserLevel(null != userLevelEle ? userLevelEle.classNames().toString() : "");
					
					Element isVipEle = shop.select(".dper-info .vip").first();
					comment.setIsVip(null != isVipEle ? 1 : 0);
					
					Element commentStar = shop.select(".review-rank .star").first();
					comment.setCommentStar(null != commentStar ? commentStar.classNames().toString() : "");
					
					Elements scores = shop.select(".review-rank .score .item");
					comment.setTasteComment("");
					comment.setEnvironmentComment("");
					comment.setServiceComment("");
					if (CollectionUtils.isNotEmpty(scores)) {
						
						for (Element score : scores) {
							String text = score.text().trim();
							if (text.contains("口味")) {
								comment.setTasteComment(text.replace("口味：", "").trim());
							} else if (text.contains("环境")) {
								comment.setEnvironmentComment(text.replace("环境：", "").trim());
							} else if (text.contains("服务")) {
								comment.setServiceComment(text.replace("服务：", "").trim());
							} else if (text.contains("人均")) {
								comment.setAvgPrice(text.replace("人均：", "").trim());
							}
						}
					}
					
					Element commentEle = shop.select(".review-words").first();
					comment.setComment(null != commentEle ? commentEle.html().replace("&nbsp;", ",").trim() : "");
					
					Elements recommentDishEles = shop.select(".review-recommend a");
					if (CollectionUtils.isNotEmpty(recommentDishEles)) {
						StringBuilder dish = new StringBuilder();
						for (Element dishEle : recommentDishEles) {
							dish.append(dishEle.text()).append(" ");
						}
						comment.setRecommendDish(dish.toString());
					} else {
						comment.setRecommendDish("");
					}
					
					Element commentTimeEle = shop.select(".misc-info .time").first();
					String commentTime = null != commentTimeEle ? commentTimeEle.text().trim() : "";
					if (commentTime.contains("更新于")) {
						comment.setFirstCommentTime(commentTime.substring(0, 10).trim());
						comment.setCommentTime(commentTime.substring(commentTime.indexOf("更新于") + 3).trim());
					} else {
						comment.setCommentTime(commentTime);
					}
					
					if (DateFormatSupport.before(comment.getCommentTime(), DateFormatSupport.YYYY_MM_DD, new java.util.Date(1483200000000L))) {
						flag = true;
						log.info(dianpingShopInfo.getShopId() + " 页数 " + page + " 评论时间早于指定截止时间，中断抓取 " + comment.getCommentTime());
						break;
					}
					
					Element commentIdEle = shop.select(".actions a[data-id]").first();
					comment.setCommentId(null != commentIdEle ? commentIdEle.attr("data-id") : "");
					
					Element praiseEle = shop.select(".actions .praise").first().nextElementSibling();
					comment.setFavoriteNum(praiseEle.hasClass("col-exp")
							? NumberUtils.toInt(praiseEle.text().replace("(", "").replace(")", ""), 0) : 0);
					
					Element replyEle = shop.select(".actions .reply").first().nextElementSibling();
					comment.setReplyNum(replyEle.hasClass("col-exp")
							? NumberUtils.toInt(replyEle.text().replace("(", "").replace(")", ""), 0) : 0);
					
					Element collectionEle = shop.select(".actions .favor").first().nextElementSibling();
					comment.setCollectNum(collectionEle.hasClass("col-exp")
							? NumberUtils.toInt(collectionEle.text().replace("(", "").replace(")", ""), 0) : 0);
					
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(comment, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			log.info("总页数 " + totalPage + " 当前页数 " + page + " 该页应该有数据，但是没有发现, 重新请求");
			parseComment(page, totalPage);
		}
		
		return flag;
	}
	
	private int getTotalPage () {
		int totalPage = -1;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() +"/review_all?queryType=sortType&queryVal=latest");
		header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() + "/review_all");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		header.setMaxTryTimes(10);
		String html = DianPingCommonRequest.getShopComment(header);
		Document doc = Jsoup.parse(html);
//		log.info(html);
		if (null == doc.select(".no-review-item")) {
			totalPage = 0;
		} else if (null != doc.select(".reviews-items")) {
			// 发现有评论列表的，看是否包含评论
			if (CollectionUtils.isNotEmpty(doc.select(".reviews-items ul li"))) {
				Element pageEle = doc.select(".reviews-pages .PageLink").last();
				totalPage = null != pageEle ? Integer.parseInt(pageEle.text().trim()) : 1;
				parseComment(1, totalPage);
			} else {
				log.info(header.getUrl() + " 应该有评论，但是没有找到，重新请求");
				log.info(html);
				// 未发现评论列表的，没有评论，总页数为0
				totalPage = getTotalPage();
			}
		} else {
			log.info(header.getUrl() + " 应该有页数，但是没有找到，重新请求");
			log.info(html);
			// 未发现评论列表的，没有评论，总页数为0
			totalPage = getTotalPage();
		}
		return totalPage;
	}
}
