package fun.jerry.proxy.entity;

/**
 * 代理IP
 * @author conner
 *
 */
public class Proxy {
	
	private String ip;
	
	private int port;
	
	private String vendor;
	
	public Proxy() {
		super();
	}

	public Proxy(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return "Proxy [ip=" + ip + ", port=" + port + "]";
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
}
