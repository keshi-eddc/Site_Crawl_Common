package fun.jerry.browser.entity;

import fun.jerry.common.entity.StatisticsCommon;
import fun.jerry.common.enumeration.DriverType;

public class WebDriverConfig extends StatisticsCommon{
	
	private String url;
	
	private String downloadPath;
	
	private String userDataDir;
	
	private DriverType driverType;
	
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

	public String getUserDataDir() {
		return userDataDir;
	}

	public void setUserDataDir(String userDataDir) {
		this.userDataDir = userDataDir;
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
