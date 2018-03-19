package com.edmi.site.dianping.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingUserInfo;

import fun.jerry.common.LogSupport;

public class DianpingParser {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 解析店铺推荐菜
	 * @param doc
	 * @param shop
	 * @param page
	 * @return
	 */
	public static List<DianpingShopRecommendInfo> parseShopRecommend(Document doc, DianpingShopInfo shop, int page) {
		List<DianpingShopRecommendInfo> list = new ArrayList<>();
		Elements eles = doc.select(".list-desc ul a");
		for (Element ele : eles) {
			DianpingShopRecommendInfo recommend = new DianpingShopRecommendInfo();
			recommend.setShopId(shop.getShopId());
			recommend.setPage(page);
			
			try {
				String dishUrl = ele.attr("href");
				recommend.setDishUrl(dishUrl);
				recommend.setDishId(dishUrl.substring(dishUrl.lastIndexOf("/") + 1));
				
				Element img = ele.select(".shop-food-img img").first();
				recommend.setDishImageUrl(null != img ? (img.hasAttr("src") ? img.attr("src") : "") : "");
				
				Element dishEle = ele.select(".shop-food-name").first();
				recommend.setDish(null != dishEle ? dishEle.text().trim() : "");
				
				Element recommendCountEle = ele.select(".recommend-count").first();
				recommend.setRecommendCount(
						null != recommendCountEle ? Integer.parseInt(recommendCountEle.text().replace("人推荐", "")) : 0);
				
				StringBuilder recommendTag = new StringBuilder();
				Elements recommendTagEles = ele.select(".recommend-reson .recommend-reson-item");
				if (CollectionUtils.isNotEmpty(recommendTagEles)) {
					for (Element tag : recommendTagEles) {
						String tagText = tag.text().trim();
						if (StringUtils.isNotEmpty(tagText)) {
							recommendTag.append(tag.text().trim()).append("|");
						}
					}
					recommend.setRecommendTag(recommendTag.toString());
				} else {
					recommend.setRecommendTag("");
				}
				
				Element priceEle = ele.select(".shop-food-money").first();
				recommend.setPrice(null != priceEle ? priceEle.text().replace("￥", "").trim() : "");
			} catch (Exception e) {
				log.error("dianping shop recommend parse error", e);
			}
			
			list.add(recommend);
		}
		return list;
	}
	
	/**
	 * 解析店铺推荐菜的页数
	 * @param doc
	 * @return
	 */
	public static int parseShopRecommendTotalPage(Document doc) {
		int totalPage = -1;
		try {
			if (null != doc.select(".list-desc")) {
				// 发现有推荐菜列表的，看是否包含推荐菜
				if (CollectionUtils.isNotEmpty(doc.select(".list-desc ul a"))) {
					Element pageEle = doc.select(".shop-food-list-page .next").last();
					Element totalPageEle = null != pageEle ? pageEle.previousElementSibling() : null;
					totalPage = null != totalPageEle ? Integer.parseInt(totalPageEle.text().trim()) : 1;
				} else {
					totalPage = 0;
				}
			} else {
				// 未发现评论列表的，没有评论，总页数为0
				totalPage = 0;
			}
		} catch (Exception e) {
			log.error("dianping shop recommend parse total page error", e);
		}
		return totalPage;
	}
	
	public static DianpingUserInfo parseUserInfo_PC(Document doc, DianpingShopComment comment) {
		DianpingUserInfo user = new DianpingUserInfo();
		user.setUserId(comment.getUserId());
		try {
			
			Element nameEle = doc.select(".name").first();
			user.setUserName(null != nameEle ? nameEle.text().trim() : "");
			
			Element vipEle = doc.select(".vip .icon-vip").first();
			user.setIsVip(null != vipEle ? 1 : 0);
			
			Element userLevelEle = doc.select(".user-info .user-rank-rst").first();
			user.setUserLevel(null != userLevelEle ? userLevelEle.attr("class") : "");
			
			Element cityEle = doc.select(".user-groun").first();
			user.setCity(null != cityEle ? cityEle.text().trim(): "");
			
			Element sexEle = doc.select(".user-groun i").first();
			user.setSex(null != sexEle ? sexEle.attr("class") : "");
			
			Elements group1 = doc.select(".container .aside .user_atten ul li");
			for (Element ele : group1) {
				String text = ele.text().trim();
				String strongText = null != ele.select("strong").first() ? ele.select("strong").first().text() : "0";
				int num = Integer.parseInt(strongText);
				if (text.contains("关注")) {
					user.setFocusNum(num);
				} else if (text.contains("粉丝")) {
					user.setFansNum(num);
				} else if (text.contains("互动")) {
					user.setInteractionNum(num);
				}
			}
			
			Elements group2 = doc.select(".container .aside .user-time p");
			for (Element ele : group2) {
				String text = ele.text().trim();
				if (text.contains("贡献值")) {
					user.setContribution(text.replace("贡献值：", "").replace("\"", ""));
				} else if (text.contains("社区等级")) {
					user.setCommunityLevel(text.replace("社区等级：", "").replace("\"", ""));
				} else if (text.contains("注册时间")) {
					user.setRegistTime(text.replace("注册时间：", "").replace("\"", ""));
				}
			}
	
			Elements group3 = doc.select(".container .aside .user-message ul li");
			for (Element ele : group3) {
				String text = ele.text().trim();
				if (text.contains("恋爱状况")) {
					user.setLoveSituation(text.replace("恋爱状况：", "").replace("\"", ""));
				} else if (text.contains("生日")) {
					user.setBirthday(text.replace("生日：", "").replace("\"", ""));
				} else if (text.contains("星座")) {
					user.setStar(text.replace("星座：", "").replace("\"", ""));
				}
			}
			
			
		} catch (Exception e) {
			log.error("dianping user info parse error", e);
		}
		return user;
	}
	
	public static DianpingUserInfo parseUserInfo_Mobile(Document doc, DianpingShopComment comment) {
		DianpingUserInfo user = new DianpingUserInfo();
		user.setUserId(comment.getUserId());
		try {
			
		} catch (Exception e) {
			log.error("dianping user info parse error", e);
		}
		return user;
	}
}
