package fun.jerry.browser;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import fun.jerry.common.UserAgentSupport;
import fun.jerry.common.enumeration.DriverType;
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
	
	public static WebDriver getDriver(WebDriverConfig config) {
		if (config.getDriverType() == DriverType.DRIVER_TYPE_CHROME) {
			return getChromeDriverInstance(config);
		} else if (config.getDriverType() == DriverType.DRIVER_TYPE_FIREFOX) {
			return getFirefoxDriverInstance(config);
		} else if (config.getDriverType() == DriverType.DRIVER_TYPE_PHANTOMJS) {
			return getPhantomJSDriverInstance(config);
		} else {
			return getChromeDriverInstance(config);
		}
	}

	/**
	 * 获取一个ChromeDriver实例
	 * @param webDriverConfig {@link fun.jerry.browser.entity.WebDriverConfig}
	 * @return {@link org.openqa.selenium.WebDriver}
	 */
	public static WebDriver getChromeDriverInstance(WebDriverConfig webDriverConfig) {

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
		prefs.put("download.default_directory", (null != webDriverConfig ? webDriverConfig.getDownloadPath() : ""));
		options.setExperimentalOption("prefs", prefs);

		if (null != webDriverConfig && (webDriverConfig.getProxyType().equals(ProxyType.PROXY_STATIC_AUTO)
				|| webDriverConfig.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN))) {
			Proxy proxy = StaticProxySupport.getStaticProxy(webDriverConfig.getProxyType());
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

		driver.manage().timeouts().pageLoadTimeout(null != webDriverConfig ? webDriverConfig.getTimeOut() : 60, TimeUnit.SECONDS);

		return driver;
	}
	
	/**
	 * 获取一个FirefoxDriver实例
	 * @param webDriverConfig {@link fun.jerry.browser.entity.WebDriverConfig}
	 * @return {@link org.openqa.selenium.WebDriver}
	 */
	public static WebDriver getFirefoxDriverInstance(WebDriverConfig webDriverConfig) {

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
		prefs.put("download.default_directory", (null != webDriverConfig ? webDriverConfig.getDownloadPath() : ""));
		options.setExperimentalOption("prefs", prefs);

		if (webDriverConfig.getProxyType().equals(ProxyType.PROXY_STATIC_AUTO)
				|| webDriverConfig.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN)) {
			Proxy proxy = StaticProxySupport.getStaticProxy(webDriverConfig.getProxyType());
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

		driver.manage().timeouts().pageLoadTimeout(webDriverConfig.getTimeOut(), TimeUnit.SECONDS);

		return driver;
	}
	
	/**
	 * 获取一个PhantomJSDriver实例
	 * @param webDriverConfig {@link fun.jerry.browser.entity.WebDriverConfig}
	 * @return {@link org.openqa.selenium.WebDriver}
	 */
	public static WebDriver getPhantomJSDriverInstance(WebDriverConfig webDriverConfig) {

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
		prefs.put("download.default_directory", (null != webDriverConfig ? webDriverConfig.getDownloadPath() : ""));
		options.setExperimentalOption("prefs", prefs);

		if (webDriverConfig.getProxyType().equals(ProxyType.PROXY_STATIC_AUTO)
				|| webDriverConfig.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN)) {
			Proxy proxy = StaticProxySupport.getStaticProxy(webDriverConfig.getProxyType());
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

		driver.manage().timeouts().pageLoadTimeout(webDriverConfig.getTimeOut(), TimeUnit.SECONDS);

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

	public static Map<String, Object> load(WebDriverConfig config, WebDriver driver) {
		return load(config, driver, 0);
	}

	private static Map<String, Object> load(WebDriverConfig config, WebDriver driver, int count) {

		Map<String, Object> result = new HashMap<>();
		result.put(HTML, "");
		result.put(DRIVER, null);

		driver = null == driver ? getDriver(config) : driver;
		try {
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
					load(config, driver, count);
				}
			}
			result.put(HTML, html);
		} catch (TimeoutException e) {
			stop(driver);
		} finally {
			String html = driver.getPageSource();
			result.put(HTML, html);
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

	public static String getChromeDriverPath() {
		return chromedriver_path;
	}

	public static String getCookies(String url) {
		StringBuilder cookie = new StringBuilder();
		WebDriver driver = null;
		try {
			if (null == driver) {
				
				DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
//				desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
//				desiredCapabilities.setCapability("phantomjs.page.customHeaders.User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
				String ua = UserAgentSupport.getPCUserAgent();
				desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", ua);
				desiredCapabilities.setCapability("phantomjs.page.customHeaders.User-Agent", ua);
				if (1 > 0) {//是否使用代理
					org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
					proxy.setProxyType(org.openqa.selenium.Proxy.ProxyType.MANUAL);
					proxy.setAutodetect(false);
					Proxy staticProxy = StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY);//自定义函数，返回代理ip及端口
					proxy.setHttpProxy(staticProxy.getIp() + ":" + staticProxy.getPort());
					System.out.println(staticProxy.getIp() + ":" + staticProxy.getPort());
					desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
				}
				
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
			System.out.println(driver.getPageSource());
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
			System.out.println(WebDriverSupport.getCookies("http://www.dianping.com/shop/91018291/dishlist"));
//			System.out.println(WebDriverSupport.getCookies("http://www.dianping.com/ajax/member/checkin/checkinList?memberId=4426996"));
//			System.out.println(DateFormatUtils.format(new Date(1483200000000L - 1000), "yyyy-MM-dd HH:mm:ss"));
//			System.out.println(1483200000000L - 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
