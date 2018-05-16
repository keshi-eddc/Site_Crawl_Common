package com.edmi.site.dianping.http;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.conn.ConnectTimeoutException;

import com.alibaba.fastjson.JSONArray;
import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;

import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

public class DianPingTaskRequest extends HttpClientSupport {
	
	public static List<DianpingShopComment> getUserInfoTask() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://localhost:9091/task/dianping/user/get");
		header.setProxyType(ProxyType.NONE);
		List<DianpingShopComment> list = new ArrayList<>();
		String html = get(header).getContent();
		list = JSONArray.parseArray(html, DianpingShopComment.class);
		return list;
	}
	
	@SuppressWarnings("finally")
	public static List<DianpingSubCategorySubRegion> getSubCategorySubRegionTask() {
		List<DianpingSubCategorySubRegion> list = new ArrayList<>();
		try {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("http://101.231.74.144:9091/task/dianping/shoplist/get");
			header.setProxyType(ProxyType.NONE);
			header.setProject(Project.BUDWEISER);
			header.setSite(Site.DIANPING);
			String html = get(header).getContent();
			list = JSONArray.parseArray(html, DianpingSubCategorySubRegion.class);
		} catch (Exception e) {
//			e.printStackTrace();
			list = new ArrayList<>();
		} finally {
			if (CollectionUtils.isEmpty(list)) {
				return new ArrayList<>();
			} else {
				return list;
			}
		}
//		return list;
	}
	
	@SuppressWarnings("finally")
	public static List<DianpingShopInfo> getCommentShop() {
		List<DianpingShopInfo> list = new ArrayList<>();
		try {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("http://101.231.74.144:9091/task/dianping/comment/get");
			header.setProxyType(ProxyType.NONE);
			header.setProject(Project.BUDWEISER);
			header.setSite(Site.DIANPING);
			String html = get(header).getContent();
			list = JSONArray.parseArray(html, DianpingShopInfo.class);
		} catch (Exception e) {
//			e.printStackTrace();
			list = new ArrayList<>();
		} finally {
			if (CollectionUtils.isEmpty(list)) {
				return new ArrayList<>();
			} else {
				return list;
			}
		}
//		return list;
	}
	
	public static void main(String[] args) {
		getUserInfoTask();
	}
}