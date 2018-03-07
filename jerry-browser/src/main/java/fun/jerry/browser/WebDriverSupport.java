package fun.jerry.browser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import fun.jerry.common.ProxyType;
import fun.jerry.common.LogSupport;
import fun.jerry.proxy.ProxyIpSupport;
import fun.jerry.proxy.entity.Proxy;

@Component
public class WebDriverSupport {
	
	public static Logger log = LogSupport.getHttplog();
	
	public final static String HTML = "html";
	
	public final static String DRIVER = "driver";
	
	/* WebDriver页面加载超时时间 */
	public static final int PAGE_LOAD_TIMEOUT = 1000;
	
	private static final int ChromeDriverCapacity = 1;

	private static boolean ChromeDriverCapacityFull = false;

	private static final int PhantomJSDriverCapacity = 10;
	
	public static final ArrayBlockingQueue<WebDriver> ChromeDriver_List = new ArrayBlockingQueue<>(ChromeDriverCapacity);
	
	public static final ArrayBlockingQueue<WebDriver> PhantomJS_List = new ArrayBlockingQueue<>(PhantomJSDriverCapacity);
	
	private static String phantomjs_path = null;

	private static String chromedriver_path = null;
	
	static {
		
		String os = System.getenv("OS");
		
		if (StringUtils.isNotEmpty(os) && os.contains("Windows")) {
			phantomjs_path = WebDriverSupport.class.getClassLoader().getResource("").getPath() + "browserDriver/phantomjs.exe";
			chromedriver_path = WebDriverSupport.class.getClassLoader().getResource("").getPath() + "browserDriver/chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", chromedriver_path);
			System.setProperty("phantomjs.binary.path", phantomjs_path);
		} else {
			phantomjs_path = WebDriverSupport.class.getClassLoader().getResource("").getPath() + "browserDriver/phantomjs";
			System.setProperty("phantomjs.binary.path", phantomjs_path);
		}
	}
	
	/**
	 * 获取一个ChromeDriver对象，用于加载页面，执行js
	 * @return
	 */
	public synchronized static WebDriver getChromeDriver(WebDriverConfig config) {
		WebDriver driver = null;
		while (!ChromeDriverCapacityFull) {
			if (ChromeDriver_List.remainingCapacity() > 0) {
				try {
					WebDriver driver_ = getChromeDriverInstance(config);
					driver_.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
					ChromeDriver_List.add(driver_);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				ChromeDriverCapacityFull = true;
				break;
			}
		}
//		driver = ChromeDriver_List.poll();
		try {
			driver = ChromeDriver_List.take();
		} catch (InterruptedException e) {
			log.error("获取Chrome实例失败：", e);
		}
		
		return driver;
	}
	
	/**
	 * 获取一个PhantomJSDriver对象，用于加载页面，执行js
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
		
		if (config.getProxyType().equals(ProxyType.PROXY_TYPE_STATIC)) {
			Proxy proxy = ProxyIpSupport.getStaticProxy();
			String proxyIpAndPort = proxy.getIp() + ":" + proxy.getPort();
			org.openqa.selenium.Proxy driverProxy = new org.openqa.selenium.Proxy();
			driverProxy.setHttpProxy(proxyIpAndPort)
				.setFtpProxy(proxyIpAndPort)
				.setSslProxy(proxyIpAndPort);
			// 以下三行是为了避免localhost和selenium driver的也使用代理，务必要加，否则无法与chromedriver通讯
			capability.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
			capability.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
			System.setProperty("http.nonProxyHosts", "localhost");

			capability.setCapability(CapabilityType.PROXY, driverProxy);
			
//			options.addArguments("--proxy-server=http://" + proxyIpAndPort);
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
			//TimeUnit.SECONDS.sleep(2);
		} catch (TimeoutException e) {
			log.error(url + " ####################### 页面加载超时  #######################");
			stop(driver);
			driver.navigate().refresh();
		}
		
		html = driver.getPageSource();
		
		return html;
	}
	
	public static Map<String, Object> load(WebDriverConfig config) {
		
		Map<String, Object> result = new HashMap<>();
		WebDriver driver = null;
		try {
			driver = getChromeDriverInstance(config);
			driver.get(config.getUrl());
		} catch (TimeoutException e) {
			stop(driver);
			driver.navigate().refresh();
		}
		
		result.put(HTML, driver.getPageSource());
		result.put(DRIVER, driver);
		
		return result;
	}
	
	/**
	 * 页面超时，停止加载
	 * @param driver
	 */
	private static void stop(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.stop()");
	}
	
	/**
	 * 在一个线程中使用完一个driver后，回收以便其他线程使用
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

}
