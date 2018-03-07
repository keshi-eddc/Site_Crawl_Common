package fun.jerry.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * 站点类型
 */
public enum Site {
	
	E3("e3", "E3系统"), 
	
	SYCM("sycm", "生意参谋"),
	
	JBP("jbp", "一号店聚宝盆"),

	YHDSUPPLIER("yhdsupplier", "一号店供应链"),
	
	JD("jd", "京东"),

	JD_VDC("jd_vdc", "京东_VDC"),

	JD_VC("jd_vc", "京东_VC"),
	
	TMALL_SUPER("Tmall_Super", "Tmall_Super"),
	
	WOMAI("WoMai", "我买"),

	SUNING("Suning", "苏宁"),
	
	VIP("Vip", "唯品会"),
	
	AMZ("Amazon", "亚马逊"),

	JM("jm", "聚美");
	
//	OTHER("", "");

	private String code;
	
	private String name;

	private Site(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getName(String code) {
		for (Site s : Site.values()) {
			if (s.getCode().equals(code)) {
				return s.name;
			}
		}
		return StringUtils.EMPTY;
	}
	
	public static String getCode(String name) {
		for (Site s : Site.values()) {
			if (s.getName().equals(name)) {
				return s.code;
			}
		}
		return StringUtils.EMPTY;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static void main(String[] args) {
		System.out.println(Site.E3.getCode());
	}

}
