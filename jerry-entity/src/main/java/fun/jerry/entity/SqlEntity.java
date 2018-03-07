package fun.jerry.entity;

/**
 * 
 * @author conner
 *
 */
public class SqlEntity {
	
	private Object obj;
	
	/** 指定对象属于哪个数据源，如果属于多个数据源，向缓存队列中添加两次 */
	private DataSource dataSource;
	
	private SqlType sqlType;

//	public SqlEntity(Object obj, SqlType sqlType) {
//		super();
//		this.obj = obj;
//		this.sqlType = sqlType;
//	}
	
	public SqlEntity(Object obj, DataSource dataSource, SqlType sqlType) {
		super();
		this.obj = obj;
		this.dataSource = dataSource;
		this.sqlType = sqlType;
	}

	public SqlEntity() {
		super();
	}

	@Override
	public String toString() {
		return "SqlEntity [obj=" + obj + ", dataSource=" + dataSource + ", sqlType=" + sqlType + "]";
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SqlType getSqlType() {
		return sqlType;
	}

	public void setSqlType(SqlType sqlType) {
		this.sqlType = sqlType;
	}
	
}
