package com.edmi.site.dianping.http;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.common.ProxyType;
import fun.jerry.common.RequestType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.httpclient.core.UserAgentSupport;
@Component
public class DianPingCommonRequest extends HttpClientSupport {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private final static BlockingQueue<String> COOKIES = new ArrayBlockingQueue<String>(200);

	private final static BlockingQueue<String> COOKIES_SHOPLIST = new ArrayBlockingQueue<String>(1);
	
	static {
		COOKIES.add("showNav=#nav-tab|0|1; navCtgScroll=300; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=16; cye=wuhan; s_ViewType=10; _lxsdk_s=160badf2b55-705-8a3-e77%7C%7C431");
//		COOKIES_Search.add("showNav=#nav-tab|0|1; navCtgScroll=100; showNav=#nav-tab|0|1; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=1; cye=shanghai; s_ViewType=10; _lxsdk_s=161e55c7af9-bc2-a76-b58%7C%7C29");
		COOKIES_SHOPLIST.add("showNav=javascript:; navCtgScroll=200; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); JSESSIONID=26CEC1B3FCEF371EED7A05E9969BEE13; _lxsdk_cuid=161fa3115fcc8-0538dde0ed830a-5e183017-100200-161fa3115fdc8; _lxsdk=161fa3115fcc8-0538dde0ed830a-5e183017-100200-161fa3115fdc8; _lx_utm=utm_source%3Ddp_pc_other; cy=1; cye=shanghai; s_ViewType=10; _lxsdk_s=161ff1ee5c3-e08-518-197%7C%7C10");
	}
	
	@Scheduled(cron="0 0/5 * * * ?")
	public static void refreshShopListCookie(String url) {
		log.info("开始刷新Cookie");
		// url = "http://www.dianping.com/shanghai/ch10/g110r2";
		synchronized (COOKIES_SHOPLIST) {
			StringBuilder cookies = new StringBuilder();
			WebDriverConfig config = new WebDriverConfig();
//					config.setProxyType(ProxyType.PROXY_TYPE_STATIC);
			config.setProxyType(ProxyType.PROXY_TYPE_ABUYUN);
			WebDriver driver = WebDriverSupport.getChromeDriverInstance(config);
			int count = 0;
			while (StringUtils.isEmpty(cookies) || count < 10) {
				try {
					driver.get(url);
//					driver.get("http://H26U3Y18CA6L02YD:0567219ED7DF3592@" + url.substring(url.indexOf("://") + 3));

					String html = driver.getPageSource();
					if (html.contains("未连接到互联网") || html.contains("代理服务器出现问题，或者地址有误")
							|| html.contains("ERR_PROXY_CONNECTION_FAILED")) {
						continue;
					}

					Set<Cookie> cookieSet = driver.manage().getCookies();
					for (Cookie temp : cookieSet) {
						cookies.append(temp.getName() + "=" + temp.getValue()).append("; ");
						// System.out.println(temp.getName() + " " +
						// temp.getValue());
						// if (ArrayUtils.contains(new String[] {"JSESSIONID",
						// "cid", "sut", "saut"}, temp.getName())) {
						// cookies.append(temp.getName() + "=" +
						// temp.getValue()).append("; ");
						// }
					}
					COOKIES_SHOPLIST.poll();
					COOKIES_SHOPLIST.add(cookies.toString());
					log.info("##################  " + COOKIES_SHOPLIST.size());
				} catch (Exception e) {
					log.info("refreshShopListCookie error ", e);
				} finally {
					count ++;
//					driver.close();
//					driver.quit();
				}
			}
		}
	}
	
	public static String getSubCategorySubRegion(HttpRequestHeader header) {
		header.setRequestType(RequestType.HTTP_GET);
		header.setProxyType(ProxyType.PROXY_TYPE_STATIC);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setCookie(COOKIES_SHOPLIST.element());
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(2);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			return getSubCategorySubRegion(header);
		} else {
			return "";
		}
	}
	
	public static String getShopList(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_TYPE_STATIC);
		header.setCookie(COOKIES_SHOPLIST.element());
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
		header.setRequestSleepTime(2000);
		header.setMaxTryTimes(2);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			return getShopList(header);
		} else {
			return "";
		}
	}
	
	public static String getShopDetail(HttpRequestHeader header) {
		header.setAccept("application/json, text/javascript, */*; q=0.01");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_TYPE_ABUYUN);
//		header.setProxyType(ProxyType.PROXY_TYPE_STATIC);
		header.setCookie("_lxsdk_cuid=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; _lxsdk=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; cy=105; cye=jinhua; _hc.v=acf7f16c-257a-70d5-d40f-91620a75bb6b.1520571532; s_ViewType=10; _lxsdk_s=162094d01cf-011-a41-6f5%7C%7C44");
		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setRequestSleepTime(2000);
		header.setReferer("http://www.dianping.com/shop/98350001");
		header.setXrequestedWith("XMLHttpRequest");
		header.setMaxTryTimes(2);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			return getShopList(header);
		} else {
			return "";
		}
	}
}