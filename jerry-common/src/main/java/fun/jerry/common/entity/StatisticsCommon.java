package fun.jerry.common.entity;

import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;

/**
 * 统计
 * @author conner
 */
public class StatisticsCommon {
	
	/**
	 * driver使用哪种代理IP，默认不使用任何代理IP<br>
	 * 如需使用代理，参考 {@link fun.jerry.common.enumeration.proxy.enumeration.ProxyType}
	 */
	private ProxyType proxyType = ProxyType.NONE;
	
	/**
	 * driver使用哪种代理IP，默认不使用任何代理IP<br>
	 * 如需使用代理，参考 {@link fun.jerry.common.enumeration.proxy.enumeration.ProxyType}
	 */
	private Project project;
	
	/**
	 * driver使用哪种代理IP，默认不使用任何代理IP<br>
	 * 如需使用代理，参考 {@link fun.jerry.common.enumeration.proxy.enumeration.ProxyType}
	 */
	private Site site;

	public ProxyType getProxyType() {
		return proxyType;
	}

	public void setProxyType(ProxyType proxyType) {
		this.proxyType = proxyType;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	
}