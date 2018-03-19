package fun.jerry.browser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.proxy.StaticProxySupport;
import fun.jerry.proxy.entity.Proxy;
import fun.jerry.proxy.enumeration.ProxyType;

@Component
public class WebDriverSupport {

	public static Logger log = LogSupport.getHttplog();

	final static String proxyUser = "H26U3Y18CA6L02YD";
	final static String proxyPass = "0567219ED7DF3592";

	public final static String HTML = "html";

	public final static String DRIVER = "driver";

	/* WebDriver页面加载超时时间 */
	public static final int PAGE_LOAD_TIMEOUT = 60;

	private static final int CHROMEDRIVER_CAPACITY = 3;

	private static final int PhantomJSDriver_Capacity = 10;

	public static final ArrayBlockingQueue<WebDriver> ChromeDriver_List = new ArrayBlockingQueue<>(
			CHROMEDRIVER_CAPACITY);

	public static final ArrayBlockingQueue<WebDriver> PhantomJS_List = new ArrayBlockingQueue<>(
			PhantomJSDriver_Capacity);

	private static String phantomjs_path = null;

	private static String chromedriver_path = null;

	static {

		String os = System.getenv("OS");

		if (StringUtils.isNotEmpty(os) && os.contains("Windows")) {
			phantomjs_path = WebDriverSupport.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/phantomjs.exe";
			chromedriver_path = WebDriverSupport.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", chromedriver_path);
			System.setProperty("phantomjs.binary.path", phantomjs_path);
		} else {
			phantomjs_path = WebDriverSupport.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/phantomjs";
			System.setProperty("phantomjs.binary.path", phantomjs_path);
		}
	}

	/**
	 * 获取一个ChromeDriver对象，用于加载页面，执行js
	 * 
	 * @return
	 */
	private synchronized static WebDriver initChromeDriverList(WebDriverConfig config) {
		WebDriver driver = null;
		while (ChromeDriver_List.remainingCapacity() > 0) {
			try {
				WebDriver driver_ = getChromeDriverInstance(config);
				driver_.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
				ChromeDriver_List.add(driver_);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// driver = ChromeDriver_List.poll();
		try {
			driver = ChromeDriver_List.take();
		} catch (InterruptedException e) {
			log.error("获取Chrome实例失败：", e);
		}

		return driver;
	}

	/**
	 * 获取一个PhantomJSDriver对象，用于加载页面，执行js
	 * 
	 * @return
	 */
	public static WebDriver getPhantomJSDriver() {
		WebDriver driver = null;
		while (true) {
			if (PhantomJS_List.remainingCapacity() > 0) {
				try {
					WebDriver driver_ = new PhantomJSDriver();
					driver_.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
					PhantomJS_List.add(driver_);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		driver = PhantomJS_List.poll();
		return driver;
	}

	/**
	 * 获取一个ChromeDriver实例
	 * 
	 * @param config
	 * @return
	 */
	public static WebDriver getChromeDriverInstance(WebDriverConfig config) {

		WebDriver driver = null;

		String filePath = chromedriver_path;
		File file = new File(filePath);
		if (!file.exists()) {
			filePath = "browserDriver/chromedriver.exe";
		}
		System.setProperty("webdriver.chrome.driver", filePath);

		DesiredCapabilities capability = null;
		capability = DesiredCapabilities.chrome();

		ChromeOptions options = new ChromeOptions();
		options.addArguments(Arrays.asList("allow-running-insecure-content", "ignore-certificate-errors"));
		options.addArguments("test-type");

		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.default_directory", (null != config ? config.getDownloadPath() : ""));
		options.setExperimentalOption("prefs", prefs);

		if (config.getProxyType().equals(ProxyType.PROXY_STATIC_AUTO)
				|| config.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN)) {
			Proxy proxy = StaticProxySupport.getStaticProxy(config.getProxyType());
			String proxyIpAndPort = proxy.getIp() + ":" + proxy.getPort();
			// String proxyIpAndPort =
			// "http://H26U3Y18CA6L02YD:0567219ED7DF3592@http-dyn.abuyun.com:9020";
			org.openqa.selenium.Proxy driverProxy = new org.openqa.selenium.Proxy();
			driverProxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
			// 以下三行是为了避免localhost和selenium driver的也使用代理，务必要加，否则无法与chromedriver通讯
			capability.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
			capability.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
			System.setProperty("http.nonProxyHosts", "localhost");

			capability.setCapability(CapabilityType.PROXY, driverProxy);

			// options.addArguments("--proxy-server=http://" + proxyIpAndPort);
		}

		capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		capability.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(capability);

		driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);

		return driver;
	}

	public static String load(WebDriver driver, String url) {

		String html = "";

		try {
			driver.get(url);
			// TimeUnit.SECONDS.sleep(2);
		} catch (TimeoutException e) {
			log.error(url + " ####################### 页面加载超时  #######################");
			stop(driver);
			driver.navigate().refresh();
		}

		html = driver.getPageSource();

		return html;
	}

	public static Map<String, Object> load(WebDriverConfig config) {
		return load(config, 0);
	}

	private static Map<String, Object> load(WebDriverConfig config, int count) {

		Map<String, Object> result = new HashMap<>();
		result.put(HTML, "");
		result.put(DRIVER, null);

		WebDriver driver = null;
		try {
			driver = ChromeDriver_List.isEmpty() ? getChromeDriverInstance(config) : ChromeDriver_List.poll();
			count++;
			driver.get(config.getUrl());
			String html = driver.getPageSource();
			if (html.contains("未连接到互联网") || html.contains("代理服务器出现问题，或者地址有误")
					|| html.contains("ERR_PROXY_CONNECTION_FAILED")) {
				driver.close();
				driver.quit();
				driver = null;
				if (count > config.getMaxTryTimes()) {
					return result;
				} else {
					load(config, count);
				}
			}
			result.put(HTML, html);
		} catch (TimeoutException e) {
			stop(driver);
		} finally {
			if (null != driver) {
				ChromeDriver_List.add(driver);
			}
			String html = driver.getPageSource();
			result.put(HTML, html);
			// driver.close();
			// driver.quit();
		}

		result.put(DRIVER, driver);

		return result;
	}

	/**
	 * 页面超时，停止加载
	 * 
	 * @param driver
	 */
	private static void stop(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.stop()");
	}

	/**
	 * 在一个线程中使用完一个driver后，回收以便其他线程使用
	 * 
	 * @param driver
	 */
	public static void recycle(WebDriver driver) {
		if (driver instanceof PhantomJSDriver) {
			PhantomJS_List.add(driver);
		} else if (driver instanceof ChromeDriver) {
			ChromeDriver_List.add(driver);
		}
	}

	public static String getChromeDriverPath() {
		return chromedriver_path;
	}

	public static String getCookies(String url) {
		StringBuilder cookie = new StringBuilder();
		WebDriver driver = null;
		try {
			if (null == driver) {
				
				DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
				desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
				desiredCapabilities.setCapability("phantomjs.page.customHeaders.User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
//				if (1 > 0) {//是否使用代理
//					org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
//					proxy.setProxyType(org.openqa.selenium.Proxy.ProxyType.MANUAL);
//					proxy.setAutodetect(false);
//					Proxy staticProxy = StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY);//自定义函数，返回代理ip及端口
//					proxy.setHttpProxy(staticProxy.getIp() + ":" + staticProxy.getPort());
//					desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
//				}
				
				driver = new PhantomJSDriver(desiredCapabilities);
				// DesiredCapabilities capability = null;
				// capability = DesiredCapabilities.chrome();
				//
				// ChromeOptions options = new ChromeOptions();
				// options.addArguments(Arrays.asList("allow-running-insecure-content",
				// "ignore-certificate-errors"));
				// options.addArguments("test-type");
				//
				// Map<String, Object> prefs = new HashMap<String, Object>();
				// prefs.put("profile.default_content_settings.popups", 0);
				// options.setExperimentalOption("prefs", prefs);
				//
				// capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				//
				// capability.setCapability(ChromeOptions.CAPABILITY, options);
				// driver = new ChromeDriver(capability);
			}
			driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
			driver.get(url);
//			System.out.println(driver.getPageSource());
			// 20s用于输入验证码
			Set<Cookie> cookieSet = driver.manage().getCookies();
			for (Cookie temp : cookieSet) {
				cookie.append(temp.getName() + "=" + temp.getValue()).append("; ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
		}
		log.info(url + " get cookie : " + cookie.toString());
		return cookie.toString();
	}

	public static void main(String[] args) {
		try {
//			System.out.println(WebDriverSupport.getCookies("http://www.dianping.com/shop/10005596/review_all/p2?queryType=sortType&queryVal=latest"));
//			System.out.println(WebDriverSupport.getCookies("http://www.dianping.com/shop/10005596/review_all/p2?queryType=sortType&queryVal=latest"));
			System.out.println(WebDriverSupport.getCookies("http://www.dianping.com/member/1065520725"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
