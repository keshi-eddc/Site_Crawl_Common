package fun.jerry.entity.system;

import fun.jerry.entity.annotation.TableMapping;

/**
 * 日志记录
 * @author conner
 *
 */
@TableMapping("Execute_Fail_Log")
public class ExecuteFailLog {

	private String Id;
	
	/** 涉及的对象  */
	private String ObjName;
	
	/** 涉及的对象对应的表名  */
	private String TableName;
	
	/** 主键 */
	private String PrimaryKey;
	
	/** 主键值 */
	private String PrimaryKeyValue;
	
	private String ExecuteSql;

	public ExecuteFailLog() {
		super();
	}

	@Override
	public String toString() {
		return "ExecuteFailLog [Id=" + Id + ", ObjName=" + ObjName + ", TableName=" + TableName + ", PrimaryKey="
				+ PrimaryKey + ", PrimaryKeyValue=" + PrimaryKeyValue + ", ExecuteSql=" + ExecuteSql + "]";
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getObjName() {
		return ObjName;
	}

	public void setObjName(String objName) {
		ObjName = objName;
	}

	public String getTableName() {
		return TableName;
	}

	public void setTableName(String tableName) {
		TableName = tableName;
	}

	public String getPrimaryKey() {
		return PrimaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		PrimaryKey = primaryKey;
	}

	public String getPrimaryKeyValue() {
		return PrimaryKeyValue;
	}

	public void setPrimaryKeyValue(String primaryKeyValue) {
		PrimaryKeyValue = primaryKeyValue;
	}

	public String getExecuteSql() {
		return ExecuteSql;
	}

	public void setExecuteSql(String sql) {
		ExecuteSql = sql;
	}

}
