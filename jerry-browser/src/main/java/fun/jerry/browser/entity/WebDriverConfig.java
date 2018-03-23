package fun.jerry.browser.entity;

import fun.jerry.common.enumeration.DriverType;
import fun.jerry.proxy.enumeration.ProxyType;

public class WebDriverConfig {
	
	private String url;
	
	private String downloadPath;
	
	private DriverType driverType;
	
	/**
	 * driver使用哪种代理IP，默认不使用任何代理IP<br>
	 * 如需使用代理，参考 {@link fun.jerry.proxy.enumeration.ProxyType}
	 */
	private ProxyType proxyType = ProxyType.NONE;
	
	/**
	 * 是否加载图片，默认值 false<br/>
	 * 有的需要对图片截图，需将该属性设置为true
	 * 
	 */
	private boolean loadImg = false;
	
	/**
	 * WebDriver页面加载超时时间，单位秒，默认60秒
	 */
	private int timeOut = 60;
	
	private int maxTryTimes = 3;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isLoadImg() {
		return loadImg;
	}

	public void setLoadImg(boolean loadImg) {
		this.loadImg = loadImg;
	}
	
}
