package fun.jerry.httpclient.bean;

import java.util.List;

import fun.jerry.common.RequestType;

public class HttpRequestHeader {
	
	private String url;
	
	private String accept;
	
	private String acceptEncoding;
	
	private String acceptLanguage;
	
	private String cacheControl;
	
	private String connection;
	
	private String contentType;
	
	private String cookie;
	
	private String encode;
	
	private String host;
	
	private String origin;
	
	private String referer;
	
	private String upgradeInsecureRequests;
	
	private String userAgent;
	
	private String xrequestedWith;
	
	/** 抓取的具体对象 */
	private String obj;
	
	/**
	 * Post请求的时候需要这是该参数
	 */
	private List<NameValue> values;
	
	/** 页面加载时候的超时时间 */
	private int timeOut = 2000;
	
	private String proxyType;
	
	/**
	 * 请求最大重试次数
	 */
	private int maxTryTimes = 3;
	
	/**
	 * 请求休眠时间
	 */
	private int requestSleepTime;
	
	private RequestType requestType = RequestType.HTTP_GET;
	
	public HttpRequestHeader() {
		super();
	}

	@Override
	public String toString() {
		return "HttpRequestHeader [url=" + url + ", accept=" + accept + ", acceptEncoding=" + acceptEncoding
				+ ", acceptLanguage=" + acceptLanguage + ", cacheControl=" + cacheControl + ", connection=" + connection
				+ ", contentType=" + contentType + ", cookie=" + cookie + ", encode=" + encode + ", host=" + host
				+ ", origin=" + origin + ", referer=" + referer + ", upgradeInsecureRequests=" + upgradeInsecureRequests
				+ ", userAgent=" + userAgent + ", xrequestedWith=" + xrequestedWith + ", values=" + values + "]";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAcceptEncoding() {
		return acceptEncoding;
	}

	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public String getCacheControl() {
		return cacheControl;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getUpgradeInsecureRequests() {
		return upgradeInsecureRequests;
	}

	public void setUpgradeInsecureRequests(String upgradeInsecureRequests) {
		this.upgradeInsecureRequests = upgradeInsecureRequests;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getXrequestedWith() {
		return xrequestedWith;
	}

	public void setXrequestedWith(String xrequestedWith) {
		this.xrequestedWith = xrequestedWith;
	}

	public List<NameValue> getValues() {
		return values;
	}

	public void setValues(List<NameValue> values) {
		this.values = values;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public int getMaxTryTimes() {
		return maxTryTimes;
	}

	public void setMaxTryTimes(int maxTryTimes) {
		this.maxTryTimes = maxTryTimes;
	}

	public String getProxyType() {
		return proxyType;
	}

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}

	public int getRequestSleepTime() {
		return requestSleepTime;
	}

	public void setRequestSleepTime(int requestSleepTime) {
		this.requestSleepTime = requestSleepTime;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public class NameValue {
		
		private String key;
		
		private String value;

		public NameValue() {
			super();
		}

		public NameValue(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
