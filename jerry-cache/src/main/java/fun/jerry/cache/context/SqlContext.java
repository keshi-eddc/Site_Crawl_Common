package fun.jerry.cache.context;

import java.util.List;

/**
 * 
 * @author conner
 *
 */
public class SqlContext {

	/** 执行的sql */
	private StringBuilder sql;

	/** 参数，对应sql中的?号 */
	private List<Object> params;

	public SqlContext(StringBuilder sql, List<Object> params) {
		this.sql = sql;
		this.params = params;
	}
	
	@Override
	public String toString() {
		return "SqlContext [sql=" + sql + ", params=" + params + "]";
	}

	public StringBuilder getSql() {
		return sql;
	}

	public void setSql(StringBuilder sql) {
		this.sql = sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

}
