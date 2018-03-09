package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_ShopInfo")
public class DianpingShopInfo extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("shop_name")
	private String shopName;
	
	@ColumnMapping("shop_url")
	private String shopUrl;
	
	@ColumnMapping("tuan_support")
	private Integer tuanSupport;
	
	@ColumnMapping("out_support")
	private Integer outSupport;
	
	@ColumnMapping("promotion_support")
	private Integer promotionSupport;
	
	@ColumnMapping("book_support")
	private Integer bookSupport;
	
	@ColumnMapping("has_branch")
	private Integer hasBranch;
	
	@ColumnMapping("brand_url")
	private String brandUrl;
	
	@ColumnMapping("star_level")
	private String starLevel;
	
	@ColumnMapping("review_num")
	private Integer reviewNum;
	
	@ColumnMapping("avg_price")
	private String avgPrice;
	
	@ColumnMapping("address")
	private String address;
	
	@ColumnMapping("taste_score")
	private String tasteScore;
	
	@ColumnMapping("environment_score")
	private String environmentScore;
	
	@ColumnMapping("service_score")
	private String serviceScore;
	
	@ColumnMapping("sub_category_id")
	private String subCategoryId;

	@ColumnMapping("sub_region_id")
	private String subRegionId;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public Integer getTuanSupport() {
		return tuanSupport;
	}

	public void setTuanSupport(Integer tuanSupport) {
		this.tuanSupport = tuanSupport;
	}

	public Integer getOutSupport() {
		return outSupport;
	}

	public void setOutSupport(Integer outSupport) {
		this.outSupport = outSupport;
	}

	public Integer getPromotionSupport() {
		return promotionSupport;
	}

	public void setPromotionSupport(Integer promotionSupport) {
		this.promotionSupport = promotionSupport;
	}

	public Integer getBookSupport() {
		return bookSupport;
	}

	public void setBookSupport(Integer bookSupport) {
		this.bookSupport = bookSupport;
	}

	public String getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(String starLevel) {
		this.starLevel = starLevel;
	}

	public Integer getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(String avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String getTasteScore() {
		return tasteScore;
	}

	public void setTasteScore(String tasteScore) {
		this.tasteScore = tasteScore;
	}

	public String getEnvironmentScore() {
		return environmentScore;
	}

	public void setEnvironmentScore(String environmentScore) {
		this.environmentScore = environmentScore;
	}

	public String getServiceScore() {
		return serviceScore;
	}

	public void setServiceScore(String serviceScore) {
		this.serviceScore = serviceScore;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getSubRegionId() {
		return subRegionId;
	}

	public void setSubRegionId(String subRegionId) {
		this.subRegionId = subRegionId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public Integer getHasBranch() {
		return hasBranch;
	}

	public void setHasBranch(Integer hasBranch) {
		this.hasBranch = hasBranch;
	}

	public String getBrandUrl() {
		return brandUrl;
	}

	public void setBrandUrl(String brandUrl) {
		this.brandUrl = brandUrl;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
