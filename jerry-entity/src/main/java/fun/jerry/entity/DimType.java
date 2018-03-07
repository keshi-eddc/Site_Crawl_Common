package fun.jerry.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * 对应不同站点的维度类型
 */
public enum DimType {
	
	E3_STOCK("e3_stock", "E3系统库存"), 
	
	E3_ORDER("e3_order", "E3系统订单"), 
	
	E3_DETAIL("e3_detail", "E3系统订单明细"), 
	
	E3_USER("e3_user", "E3系统用户"), 
	
	SYCM_SHOP_FLOW("sycm_shop_flow", "生意参谋店铺流量KPI"),

	SYCM_SHOP_TRANSACTION("sycm_shop_transaction", "生意参谋店铺交易KPI"),
	
	SYCM_SHOP_SERVICE("sycm_shop_service", "生意参谋店铺服务KPI"),
	
	SYCM_SHOP_OTHER("sycm_shop_other", "生意参谋店铺其他KPI"),
	
	SYCM_GOODS_FLOW("sycm_goods_flow", "生意参谋商品流量KPI"),
	
	SYCM_GOODS_TRANSACTION("sycm_goods_transaction", "生意参谋商品交易KPI"),
	
	SYCM_GOODS_SERVICE("sycm_goods_service", "生意参谋商品服务KPI"),
	
	SYCM_GOODS_OTHER("sycm_goods_other", "生意参谋商品其他KPI"),
	
	JBP_PRODUCT_SALE_DETAIL("jbp_productSaleDetail", "聚宝盆商品销售明细"),

	JBP_STOCK("jbp_stock", "聚宝盆商品库存"),
	
	JD_STOCK_SALE("jd_stock_sale", "京东库存销量"),
	
	YHDSUPPLIER_SALE_DETAIL("yhdsupplier_sale_detail", "一号店供应链销售明细"),

	YHDSUPPLIER_SALE_SUMMARY("yhdsupplier_sale_summary", "一号店供应链销售汇总"),
	
	YHDSUPPLIER_PRODUCT_MANAGE("yhdsupplier_product_manage", "一号店供应链商品管理"),
	
	Other("", "");

	private String code;
	
	private String name;

	private DimType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getName(String code) {
		for (DimType s : DimType.values()) {
			if (s.getCode().equals(code)) {
				return s.name;
			}
		}
		return StringUtils.EMPTY;
	}
	
	public static String getCode(String name) {
		for (DimType s : DimType.values()) {
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

}
