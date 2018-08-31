package fun.jerry.browser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.common.UserAgentSupport;
import fun.jerry.common.enumeration.DriverType;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.proxy.StaticProxySupport;
import fun.jerry.proxy.entity.Proxy;

public class WebDriverSupport2 {

	public static Logger log = LogSupport.getHttplog();
	
	public static final BlockingQueue<ChromeDriverService> ChromeDriverServiceList = new ArrayBlockingQueue<>(10);

	final static String proxyUser = "H26U3Y18CA6L02YD";
	final static String proxyPass = "0567219ED7DF3592";

	private static String phantomjs_path = null;

	private static String chromedriver_path = null;

	static {

		String os = System.getenv("OS");

		if (StringUtils.isNotEmpty(os) && os.contains("Windows")) {
			phantomjs_path = WebDriverSupport2.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/phantomjs.exe";
			chromedriver_path = WebDriverSupport2.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", chromedriver_path);
			System.setProperty("phantomjs.binary.path", phantomjs_path);
		} else {
			phantomjs_path = WebDriverSupport2.class.getClassLoader().getResource("").getPath()
					+ "browserDriver/phantomjs";
			System.setProperty("phantomjs.binary.path", phantomjs_path);
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
		// options.addArguments("user-data-dir=C:/Users/user_name/AppData/Local/Google/Chrome/User Data");
		options.addArguments("user-data-dir=" + webDriverConfig.getUserDataDir());

		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.default_directory", (null != webDriverConfig ? webDriverConfig.getDownloadPath() : ""));
		options.setExperimentalOption("prefs", prefs);

		if (null != webDriverConfig && (webDriverConfig.getProxyType().equals(ProxyType.PROXY_STATIC_AUTO)
				|| webDriverConfig.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN)
				|| webDriverConfig.getProxyType().equals(ProxyType.PROXY_STATIC_DLY))) {
			Proxy proxy = StaticProxySupport.getStaticProxy(webDriverConfig.getProxyType(), webDriverConfig.getProject(), webDriverConfig.getSite());
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
		} else if (null != webDriverConfig && (webDriverConfig.getProxyType().equals(ProxyType.PROXY_CLOUD_ABUYUN))) {
			Proxy proxy = StaticProxySupport.getStaticProxy(webDriverConfig.getProxyType(), webDriverConfig.getProject(), webDriverConfig.getSite());
			String proxyIpAndPort = proxy.getIp() + ":" + proxy.getPort();
//			 String proxyIpAndPort =
//			 "http://HN54N0TZA3IO945D:3524EC2B27DDDDF4@http-dyn.abuyun.com:9020";
			org.openqa.selenium.Proxy driverProxy = new org.openqa.selenium.Proxy();
			driverProxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
			// 以下三行是为了避免localhost和selenium driver的也使用代理，务必要加，否则无法与chromedriver通讯
			capability.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
			capability.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
			System.setProperty("http.nonProxyHosts", "localhost");

			capability.setCapability(CapabilityType.PROXY, driverProxy);
		}

		capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		capability.setCapability(ChromeOptions.CAPABILITY, options);
//		driver = new ChromeDriver(capability);
		ChromeDriverService service = new ChromeDriverService.Builder()
		        .usingDriverExecutable(new File(chromedriver_path))
		        .usingAnyFreePort()
		        .build();
		try {
			service.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver = new RemoteWebDriver(service.getUrl(), capability);

		driver.manage().timeouts().pageLoadTimeout(null != webDriverConfig ? webDriverConfig.getTimeOut() : 60, TimeUnit.SECONDS);

		return driver;
	}

	public static String load(WebDriver driver, String url) {}

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
	
}
