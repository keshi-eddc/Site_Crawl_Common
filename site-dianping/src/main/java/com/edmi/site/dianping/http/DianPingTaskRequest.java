package com.edmi.site.dianping.http;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.edmi.site.dianping.entity.DianpingShopComment;

import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.enumeration.ProxyType;

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
	
	public static void main(String[] args) {
		getUserInfoTask();
	}
}