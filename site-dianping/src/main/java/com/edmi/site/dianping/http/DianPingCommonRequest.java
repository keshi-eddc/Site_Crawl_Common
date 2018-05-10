package com.edmi.site.dianping.http;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.scheduling.annotation.Scheduled;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.common.UserAgentSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.RequestType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;

//@Component
public class DianPingCommonRequest extends HttpClientSupport {

	private static Logger log = LogSupport.getDianpinglog();

	private final static BlockingQueue<String> COOKIES = new ArrayBlockingQueue<String>(200);

	private final static BlockingQueue<String> COOKIES_SHOPLIST = new ArrayBlockingQueue<String>(1);

	public final static BlockingQueue<String> COOKIES_SHOPCOMMENT = new ArrayBlockingQueue<String>(5);

	public final static BlockingQueue<String> COOKIES_SHOPRECOMMEND = new ArrayBlockingQueue<String>(5);
	
	public final static BlockingQueue<String> COOKIES_USERINFO = new ArrayBlockingQueue<String>(2);
	
	static {
		COOKIES.add(
				"showNav=#nav-tab|0|1; navCtgScroll=300; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=16; cye=wuhan; s_ViewType=10; _lxsdk_s=160badf2b55-705-8a3-e77%7C%7C431");
		// COOKIES_Search.add("showNav=#nav-tab|0|1; navCtgScroll=100;
		// showNav=#nav-tab|0|1; navCtgScroll=0;
		// _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333;
		// __utma=1.780503649.1510041292.1510041292.1510041292.1;
		// __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);
		// _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8;
		// _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8;
		// aburl=1; cy=1; cye=shanghai; s_ViewType=10;
		// _lxsdk_s=161e55c7af9-bc2-a76-b58%7C%7C29");
//		COOKIES_SHOPLIST.add(
//				"navCtgScroll=0; showNav=#nav-tab|0|1; cy=1; cye=shanghai; _lxsdk_cuid=16343eafd7cc8-001136ed022711-3f3c5501-100200-16343eafd7dc8; _lxsdk=16343eafd7cc8-001136ed022711-3f3c5501-100200-16343eafd7dc8; _hc.v=3de1209d-8b3c-28f3-c95e-2e5e2f238af6.1525852864; ua=17080236415; ctu=8547636063072e202fea44548b5b3241979c905f0c71e7449133fea379a43c40; s_ViewType=10; _lxsdk_s=16347c3eb03-734-41d-764%7C%7C31");
		COOKIES_SHOPLIST.add(
				"showNav=#nav-tab|0|1; navCtgScroll=300; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=16; cye=wuhan; s_ViewType=10; _lxsdk_s=160badf2b55-705-8a3-e77%7C%7C431");
//		COOKIES_SHOPLIST.add(
//				"_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; s_ViewType=10; cityid=1; __utma=1.1005717286.1524907683.1524907683.1524907683.1; __utmz=1.1524907683.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); cy=1; cye=shanghai; _lxsdk_s=16338ac8183-ca-250-c20%7C%7C85");
//		COOKIES_SHOPLIST.add(
//				"s_ViewType=10; _lxsdk_cuid=163480ddcf9c8-0b11f9fac3033b-3f3c5501-100200-163480ddcf9c8; _lxsdk=163480ddcf9c8-0b11f9fac3033b-3f3c5501-100200-163480ddcf9c8; _hc.v=4f22ca3b-c915-4b3a-50d3-bdcc7e9602db.1525922258; _lxsdk_s=163480ddcfb-48-d6f-804%7C%7C6");
//		COOKIES_SHOPCOMMENT.add(
//				"_lxsdk_cuid=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; _lxsdk=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; _hc.v=acf7f16c-257a-70d5-d40f-91620a75bb6b.1520571532; s_ViewType=10; cy=1; cye=shanghai; _lxsdk_s=16222b33752-6b5-61f-038%7C%7C21");
//		COOKIES_SHOPCOMMENT.add(
//				"_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; s_ViewType=10; cityid=1; __utma=1.1005717286.1524907683.1524907683.1524907683.1; __utmz=1.1524907683.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); cy=1; cye=shanghai; ctu=946223b20ade88cd1373a6270d8145bf597317dc26283c0037951afea594f4f5; uamo=13651952625; ctu=57f4fba19c4400d8ada2e815a0bacf8fce54e87a3d02c10b2aed3825d4e628c438a9e5c34a19d907eff29da2985c6199; _dp.ac.v=569d57b1-f0d5-487d-9abb-64ec5135020a; dper=2b77b9d675a89e5d46ca3857f188dee3f74f858b7a38eb5a5d614c007bcff76fb023b2baefbd4d627c772d98e210e42d2ce9409f5581626456da1cf789f6809b2bf8c760e2d4109773b0b389e97d46082ae41522b7e741e557bff80863834088; ll=7fd06e815b796be3df069dec7836c3df; ua=17080236415; _lxsdk_s=1633dcba184-648-246-fab%7C%7C644");
//		COOKIES_SHOPCOMMENT.add(
//				"cy=1; cye=shanghai; _lxsdk_cuid=16343eafd7cc8-001136ed022711-3f3c5501-100200-16343eafd7dc8; _lxsdk=16343eafd7cc8-001136ed022711-3f3c5501-100200-16343eafd7dc8; _hc.v=3de1209d-8b3c-28f3-c95e-2e5e2f238af6.1525852864; lgtoken=0c3b4ed96-79a5-4f08-9938-45536d36d385; dper=2b77b9d675a89e5d46ca3857f188dee3b2b0829aee92184261191c5201d44d469ababefc3b54bc5de667be00b1a94f6b30419786a500b826bd95ae9aec5cb23c2e5f8e402ce22c13fba01e8e9116d9e863c704e4f77fc7b9060dac5895ab269e; ll=7fd06e815b796be3df069dec7836c3df; ua=17080236415; ctu=8547636063072e202fea44548b5b3241979c905f0c71e7449133fea379a43c40; s_ViewType=10; _lxsdk_s=16343eafd7e-f54-ef7-664%7C%7C337");
		
		COOKIES_SHOPRECOMMEND.add(
				"s_ViewType=10; _lxsdk_cuid=1626676e642c8-0b365ec638e135-3b7c015b-100200-1626676e642c8; _lxsdk=1626676e642c8-0b365ec638e135-3b7c015b-100200-1626676e642c8; _hc.v=16d8fc83-49b3-4a56-b0c4-b123697eafd8.1522137491; cy=1; cye=shanghai; _lxsdk_s=1626b212b0d-25b-ad6-093%7C%7C70");
//		COOKIES_USERINFO.add(
//				"_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; _lxsdk_s=1622d721abf-53e-b60-b17%7C%7C12");
	}

//	@Scheduled(cron = "0 0/5 * * * ?")
	public static void refreshShopListCookie(String url) {
		log.info("开始刷新Cookie");
		// url = "http://www.dianping.com/shanghai/ch10/g110r2";
		synchronized (COOKIES_SHOPLIST) {
			StringBuilder cookies = new StringBuilder();
			WebDriverConfig config = new WebDriverConfig();
			config.setProxyType(ProxyType.PROXY_STATIC_AUTO);
			// config.setProxyType(ProxyType.PROXY_TYPE_ABUYUN);
			WebDriver driver = WebDriverSupport.getChromeDriverInstance(config);
			int count = 0;
			while (StringUtils.isEmpty(cookies)) {
				try {
					driver.get(url);
					// driver.get("http://H26U3Y18CA6L02YD:0567219ED7DF3592@" +
					// url.substring(url.indexOf("://") + 3));

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
					count++;
					driver.close();
					driver.quit();
				}
			}
		}
	}

//	@Scheduled(cron = "0 0/10 * * * ?")
	public static void refreshShopCommentCookie() {
		synchronized (COOKIES_SHOPCOMMENT) {
			WebDriverConfig config = new WebDriverConfig();
			config.setProxyType(ProxyType.NONE);
//			WebDriver driver = WebDriverSupport.getPhantomJSDriverInstance(config);
			WebDriver driver = WebDriverSupport.getChromeDriverInstance(config);
			try {
				driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
				driver.get("https://www.dianping.com/login?redir=http%3A%2F%2Fwww.dianping.com%2F");
				
				WebElement passwordLogin = driver.findElement(By.className("bottom-password-login"));
				if (null != passwordLogin) {
					passwordLogin.click();
					
					WebElement tab_account = driver.findElement(By.id("tab-account"));
					if (null != tab_account) {
						tab_account.click();
					}
				}
				
				WebElement userName = driver.findElement(By.id("account-textbox"));
				userName.sendKeys("17080236415");
				
				// 密码
				WebElement password = driver.findElement(By.id("password-textbox"));
				password.sendKeys("123abc123");
				//20s用于输入验证码
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				WebElement btnLogin = driver.findElement(By.id("login-button-account"));
				btnLogin.click();
		        
		        driver.get("http://www.dianping.com/shop/2278378/review_all/p2?queryType=sortType&queryVal=latest");
		        
		        StringBuilder cookies = new StringBuilder();
				Set<Cookie> cookieSet = driver.manage().getCookies();
		        for (Cookie temp : cookieSet) {
		        	cookies.append(temp.getName() + "=" + temp.getValue()).append("; ");
		        }
		        COOKIES_SHOPCOMMENT.add(cookies.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				driver.quit();
			}
		}
	}
	
//	@Scheduled(cron = "0 0/5 * * * ?")
	public static void refreshShopRecommendCookie() {
		String url = "http://www.dianping.com/shop/72351070/dishlist/p3";
		synchronized (COOKIES_SHOPRECOMMEND) {
			try {
				while (COOKIES_SHOPRECOMMEND.size() < 5) {
					String cookie = WebDriverSupport.getCookies(url);
					COOKIES_SHOPRECOMMEND.offer(cookie);
					log.info("当前cookie size " + COOKIES_SHOPRECOMMEND.size());
				}
			} catch (Exception e) {
				log.error("refresh shop recommend cookie error, ", e);
			}
		}
		
	}
	
//	@Scheduled(cron = "0 0/5 * * * ?")
	public static void refreshUserInfoCookie() {
		String url = "http://www.dianping.com/member/20192274";
		synchronized (COOKIES_USERINFO) {
			try {
				while (COOKIES_USERINFO.size() < 2) {
					String cookie = WebDriverSupport.getCookies(url);
					COOKIES_USERINFO.offer(cookie);
					log.info("当前cookie size " + COOKIES_USERINFO.size());
				}
			} catch (Exception e) {
				log.error("refresh shop recommend cookie error, ", e);
			}
		}
		
	}
	
	/**
	 * 获取Cookie
	 */
	private static String getCookie(BlockingQueue<String> queue) {
		String cookie = "";
		while (StringUtils.isEmpty(cookie)) {
			cookie = queue.poll();
			if (StringUtils.isNotEmpty(cookie)) {
				queue.add(cookie);
			}
		}
		return cookie;
	}
	
	/**
	 * 将失效的Cookie移除
	 */
	public static void removeInvalideCookie(BlockingQueue<String> queue, String cookie) {
		synchronized (queue) {
			log.error(queue + " 移除失效的Cookie" + cookie);
			queue.remove(cookie);
		}
	}

	public static String getSubCategorySubRegion(HttpRequestHeader header) {
		header.setRequestType(RequestType.HTTP_GET);
		header.setProxyType(ProxyType.PROXY_STATIC_AUTO);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setAutoPcUa(true);
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
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
		header.setCookie(COOKIES_SHOPLIST.element());
		header.setUserAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setUserAgent(UserAgentSupport.getPCUserAgent());
//		header.setUserAgent(UserAgentSupport.getPCUserAgent());
//		header.setAutoPcUa(true);
//		header.setAutoMobileUa(true);
		header.setRequestSleepTime(1000);
		if (header.getProject() == Project.CARGILL) {
			header.setMaxTryTimes(10);
		}
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
		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
		// header.setProxyType(ProxyType.PROXY_TYPE_STATIC);
		header.setCookie(
				"_lxsdk_cuid=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; _lxsdk=162092006a9c8-0ff1a8d5dd70ec-393d5f0e-1fa400-162092006aac8; cy=105; cye=jinhua; _hc.v=acf7f16c-257a-70d5-d40f-91620a75bb6b.1520571532; s_ViewType=10; _lxsdk_s=162094d01cf-011-a41-6f5%7C%7C44");
		header.setAutoPcUa(true);
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

	public static String getShopComment(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_AUTO);
		header.setCookie(getCookie(COOKIES_SHOPCOMMENT));
		header.setAutoPcUa(true);
		header.setRequestSleepTime(2000);
		header.setMaxTryTimes(10);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			removeInvalideCookie(COOKIES_SHOPCOMMENT, header.getCookie());
			header.setCookie(getCookie(COOKIES_SHOPCOMMENT));
			return getShopComment(header);
		} else {
			return "";
		}
	}
	
	public static String getShopRecommend(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setCookie(COOKIES_SHOPRECOMMEND.element());
		header.setAutoPcUa(true);
		header.setRequestSleepTime(2000);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
//				removeInvalideCookie(COOKIES_SHOPRECOMMEND, header.getCookie());
//				header.setCookie(COOKIES_SHOPRECOMMEND.element());
				html = getShopRecommend(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static String getUserInfo(HttpRequestHeader header) {
//		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//		header.setAcceptEncoding("gzip, deflate, br");
//		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
//		header.setCacheControl("no-cache");
//		header.setConnection("keep-alive");
//		header.setHost("www.dianping.com");
//		header.setPragma("no-cache");
//		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.NONE);
//		header.setCookie(COOKIES_USERINFO.element());
//		header.setCookie(COOKIES_SHOPRECOMMEND.element());
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
//		header.setAutoPcUa(false);
//		header.setAutoUa(false);
		header.setAutoMobileUa(true);
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.162 Safari/537.36");
		header.setRequestSleepTime(2000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static String getUserCheckInfo(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.NONE);
		header.setAutoPcUa(true);
//		header.setCookie(COOKIES_USERINFO.element());
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d." + ((System.currentTimeMillis() / 1000) - 6666) + "\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
		header.setRequestSleepTime(2000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
				html = getUserCheckInfo(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static String getRealTimeRank(HttpRequestHeader header) {
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setAutoPcUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
				html = getUserCheckInfo(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static String getDishRank(HttpRequestHeader header) {
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setAutoPcUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
				html = getUserCheckInfo(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static void main(String[] args) {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shanghai/ch10/g110r2");
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		getShopList(header);
	}

	private static String test() {
		StringBuilder sb = new StringBuilder();
		int maxNum = 36;
		int i;
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'w', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < 8) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		sb.append(pwd).append("-");
		pwd = new StringBuffer();
		count = 0;
		while (count < 4) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		sb.append(pwd).append("-");
		pwd = new StringBuffer();
		count = 0;
		while (count < 4) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		sb.append(pwd).append("-");
		pwd = new StringBuffer();
		count = 0;
		while (count < 4) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		sb.append(pwd).append("-");
		pwd = new StringBuffer();
		count = 0;
		while (count < 12) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		sb.append(pwd);
		return sb.toString();
	}
}