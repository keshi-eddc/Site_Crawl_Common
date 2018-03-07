package fun.jerry.entity;

/**
 * 对象属性顺序
 * @author conner
 *
 */
public class PropertyOrder {
	
	private String name;
	
	private Integer order;

	public PropertyOrder(String name, int order) {
		super();
		this.name = name;
		this.order = order;
	}

	@Override
	public String toString() {
		return "PropertyOrder [name=" + name + ", order=" + order + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
}
