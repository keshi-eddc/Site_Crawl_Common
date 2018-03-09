package fun.jerry.browser;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

public class HtmlUnitDriverSupport {
	// 代理隧道验证信息
	final static String proxyUser = "H26U3Y18CA6L02YD";
	final static String proxyPass = "0567219ED7DF3592";

	// 代理服务器
	final static String proxyServer = "http-dyn.abuyun.com:9020";

	public static void main(String[] args) {
		for (int i = 1; i < 10; i++) {
			try {
				HtmlUnitDriver driver = getHtmlUnitDriver();

//				driver.get("https://test.abuyun.com/test.php");
				driver.get("http://www.dianping.com/shop/98350001");

//				String title = driver.getTitle();
				String title = driver.getPageSource();
				System.out.println(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public static HtmlUnitDriver getHtmlUnitDriver() {
		HtmlUnitDriver driver = null;

		Proxy proxy = new Proxy();
		// 设置代理服务器地址
		proxy.setHttpProxy(proxyServer);

		DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
		capabilities.setCapability(CapabilityType.PROXY, proxy);
		capabilities.setJavascriptEnabled(true);
		capabilities.setPlatform(Platform.WIN8_1);

		driver = new HtmlUnitDriver(capabilities) {
			@Override
			protected WebClient modifyWebClient(WebClient client) {
				DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
				creds.addCredentials(proxyUser, proxyPass);
				client.setCredentialsProvider(creds);
				return client;
			}
		};

		driver.setJavascriptEnabled(true);

		return driver;
	}
}