package fun.jerry.proxy;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.config.SocketConfig.Builder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.proxy.entity.Proxy;

public class StaticProxySupport {
	
	private static Logger log = Logger.getLogger(StaticProxySupport.class);

	private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	static {
		Builder buider = SocketConfig.custom().setSoTimeout(10000);
		LayeredConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslsf)
				.register("http", new PlainConnectionSocketFactory()).build();
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(50);
		cm.setDefaultSocketConfig(buider.build());
	}

	public static Proxy getStaticProxy(ProxyType proxyType, Project project, Site site) {
		Proxy proxy = null;
		
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

		// CloseableHttpClient httpclient = HttpClients.createDefault();
//		HttpGet httpget = new HttpGet("http://192.168.3.236:9090/ip/proxy/get/" + proxyType + "/" + project + "/" + site);
	//	HttpGet httpget = new HttpGet("http://122.226.223.139:9091/ip/proxy/get/" + proxyType + "/" + project + "/" + site);
		HttpGet httpget = new HttpGet("http://192.168.0.31:9091/ip/proxy/get/" + proxyType + "/" + project + "/" + site);
//		HttpGet httpget = new HttpGet("http://192.168.0.49:9092/ip/proxy/get/" + proxyType + "/" + project + "/" + site);
		String json=null;
		CloseableHttpResponse response=null;
		try {
			response = httpclient.execute(httpget);
			
			HttpEntity entity = response.getEntity();
			json = entity != null ? EntityUtils.toString(entity) : null;
			
			// System.out.println("Executing request " +
			// httpget.getRequestLine());

			// Create a custom response handler
//			InputStream in=response.getEntity().getContent();
//            json=IOUtils.toString(in, StandardCharsets.UTF_8);
//            in.close();
//			String responseBody = httpclient.execute(httpget, responseHandler);
			// System.out.println("----------------------------------------");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			log.info(e.getMessage());
		} finally {
			try {
//				httpclient.close();
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		proxy = JSONObject.parseObject(json, Proxy.class);
		log.info(proxy.getIp() + ":" + proxy.getPort());
//		if (null == proxy) {
//			proxy = new Proxy();
//			proxy.setIp("127.0.0.1");
//			proxy.setPort(8080);
//		}
		return proxy;
	}
	
	public static void main(String[] args) {
		log.info("start");
		StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY, Project.CARGILL, Site.DIANPING);
//		StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY);
//		StaticProxySupport.getUserInfo();
//		ExecutorService pool = Executors.newScheduledThreadPool(200);
//		int count = 0;
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		for (int i = 0; i < 100; i++) {
//			
//			pool.submit(new Runnable() {
//
//				@Override
//				public void run() {
//					StaticProxySupport.getStaticProxy(ProxyType.PROXY_STATIC_DLY, Project.CARGILL, Site.DIANPING);
//				}
//			});
//
//			count++;
//			System.out.println("$$$$$$$$$$$$$$$ " + count);
//		}
//		pool.shutdown();
//		while (true) {
//			if (!pool.isTerminated()) {
//				try {
//					TimeUnit.SECONDS.sleep(20);
//					log.info("#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$ not stop");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} else {
//				stopWatch.stop();
//				System.out.println("########## " + stopWatch.getTotalTimeMillis());
//				break;
//			}
//		}
	}

}