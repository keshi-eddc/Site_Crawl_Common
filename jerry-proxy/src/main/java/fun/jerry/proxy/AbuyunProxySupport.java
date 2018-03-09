package fun.jerry.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;

public class AbuyunProxySupport {
	// 代理服务器
//	final static String proxyHost = "http-dyn.abuyun.com";
	final static String proxyHost = "http-dyn.abuyun.com";
	final static Integer proxyPort = 9020;

	// 代理隧道验证信息
	final static String proxyUser = "H26U3Y18CA6L02YD";
	final static String proxyPass = "0567219ED7DF3592";

	private static PoolingHttpClientConnectionManager cm = null;
	private static HttpRequestRetryHandler httpRequestRetryHandler = null;
	private static HttpHost proxy = null;

	private static CredentialsProvider credsProvider = null;
	private static RequestConfig reqConfig = null;

	static {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

		Registry registry = RegistryBuilder.create().register("http", plainsf).register("https", sslsf).build();

		cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(20);
		cm.setDefaultMaxPerRoute(5);

		proxy = new HttpHost(proxyHost, proxyPort, "http");

		credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

		reqConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
				.setSocketTimeout(5000).setExpectContinueEnabled(false).setProxy(new HttpHost(proxyHost, proxyPort))
				.build();
	}
	
	private static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder()
                                .loadTrustMaterial(null, new TrustStrategy() {
				//信任所有
				public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
					return true;
				}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCredentialsProvider(credsProvider).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return  HttpClients.createDefault();
	}

	public static void doRequest(HttpRequestBase httpReq) {
		CloseableHttpResponse httpResp = null;

		try {
			setHeaders(httpReq);

			httpReq.setConfig(reqConfig);

//			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
//					.setDefaultCredentialsProvider(credsProvider).build();
			CloseableHttpClient httpClient = createSSLInsecureClient();

			AuthCache authCache = new BasicAuthCache();
			authCache.put(proxy, new BasicScheme());

			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			httpResp = httpClient.execute(httpReq, localContext);

			int statusCode = httpResp.getStatusLine().getStatusCode();

			System.out.println(statusCode);

			BufferedReader rd = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpResp != null) {
					httpResp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置请求头
	 *
	 * @param httpReq
	 */
	private static void setHeaders(HttpRequestBase httpReq) {
		httpReq.setHeader("Accept-Encoding", null);
	}

	public static void doPostRequest() {
		try {
			// 要访问的目标页面
			HttpPost httpPost = new HttpPost("https://test.abuyun.com/proxy.php");

			// 设置表单参数
			List params = new ArrayList();
			params.add(new BasicNameValuePair("method", "next"));
			params.add(
					new BasicNameValuePair("params", "{\"broker\":\"abuyun\":\"site\":\"https://www.abuyun.com.cn\"}"));

			httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			doRequest(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doGetRequest() {
		// 要访问的目标页面
//		String targetUrl = "https://test.abuyun.com/proxy.php";
		String targetUrl = "http://www.dianping.com/ajax/json/shopDynamic/shopTabs?shopId=96737681&cityId=1&shopName=MOLI烧肉&power=5&mainCategoryId=225&shopType=10&shopCityId=1&_token=eJx1T9FugkAQ/Jd7LYE74A7lrSAipmqDSFIaHw6kQConcBSqTf+9a2qT9qHJJrM7OzPZ/UBdcEA2wRibREFD3iEbERWrDCmol7ChOqYMY6IT01JQ9pfTLTClXTxD9jMxGFMMTPZXJgTim7HYZK/8anUT6qoJQILKvm9sTRvHUT1UXDSVKNTsVGuyPDXalFkGeAic8r+Oi6LklZaVBGsFIQaC7DqCbMDXG/Ib9j/zCr6EVFkVArp8+R5tpSnbl3Alo3h3xsbqslhvHrzj+nKeuG5YJDU/xXSZi3lf87aMBXUyc5xunY3n+k1SU5nWifMUDUdapfxt6Ky7RzosBK1YspHzMkh2fttx3/EKd3a/Xbbo8wtahG63&uuid=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333&platform=1&partner=150&originUrl=http://www.dianping.com/shop/96737681";
		// String targetUrl = "http://proxy.abuyun.com/switch-ip";
		// String targetUrl = "http://proxy.abuyun.com/current-ip";

		try {
			HttpGet httpGet = new HttpGet(targetUrl);

			doRequest(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		doGetRequest();

		// doPostRequest();
	}
}