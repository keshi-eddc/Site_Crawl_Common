package fun.jerry.browser.entity;

import fun.jerry.common.enumeration.DriverType;
import fun.jerry.proxy.enumeration.ProxyType;

public class WebDriverConfig {
	
	private String url;
	
	private String browserName;
	
	private String downloadPath;
	
	private DriverType driverType;
	
	private ProxyType proxyType = ProxyType.PROXY_STATIC_AUTO;
	
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

	public DriverType getDriverType() {
		return driverType;
	}

	public void setDriverType(DriverType driverType) {
		this.driverType = driverType;
	}
	
}
