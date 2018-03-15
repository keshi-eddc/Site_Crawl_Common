package fun.jerry.entity.system;

/**
 * 
 * @author conner
 *
 */
public enum SqlType {
	
	PARSE_NO ("不用解析", -1),
	
	PARSE_INSERT ("解析新增", 1),

	PARSE_INSERT_NOT_EXISTS ("解析新增", 10),

	PARSE_UPDATE ("解析更新", 2),

	PARSE_QUERY_BY_LOGICALPRIMARYKEY ("根据逻辑主键查询", 3),

	PARSE_DELETE_BY_LOGICALPRIMARYKEY ("根据逻辑主键删除", 4);
	
	private String name;

	private int type;

	private SqlType(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public static void main(String[] args) {
		System.out.println(SqlType.PARSE_INSERT.getType());
	}
	
}
