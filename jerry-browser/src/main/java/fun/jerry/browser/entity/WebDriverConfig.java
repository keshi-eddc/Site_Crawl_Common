package fun.jerry.browser.entity;

import org.openqa.selenium.WebDriver;

import fun.jerry.common.ProxyType;

public class WebDriverConfig {
	
	private String url;
	
	private String browserName;
	
	private String downloadPath;
	
	private ProxyType proxyType = ProxyType.PROXY_TYPE_STATIC;
	
	private int maxTryTimes = 3;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public ProxyType getProxyType() {
		return proxyType;
	}

	public void setProxyType(ProxyType proxyType) {
		this.proxyType = proxyType;
	}

	public int getMaxTryTimes() {
		return maxTryTimes;
	}

	public void setMaxTryTimes(int maxTryTimes) {
		this.maxTryTimes = maxTryTimes;
	}
	
}
