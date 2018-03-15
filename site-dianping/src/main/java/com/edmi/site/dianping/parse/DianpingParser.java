package com.edmi.site.dianping.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;

import fun.jerry.common.LogSupport;

public class DianpingParser {
	
	private static Logger log = LogSupport.getDianpinglog();
	
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
	
	public static int parseShopRecommendTotalPage(Document doc) {
		int totalPage = -1;
		if (null != doc.select(".list-desc")) {
			// 发现有推荐菜列表的，看是否包含推荐菜
			if (CollectionUtils.isNotEmpty(doc.select(".list-desc ul a"))) {
				Element pageEle = doc.select(".shop-food-list-page .next").last();
				Element totalPageEle = pageEle.previousElementSibling();
				totalPage = null != totalPageEle ? Integer.parseInt(totalPageEle.text().trim()) : 1;
			} else {
				totalPage = 0;
			}
		} else {
			// 未发现评论列表的，没有评论，总页数为0
			totalPage = 0;
		}
		return totalPage;
	}
}
