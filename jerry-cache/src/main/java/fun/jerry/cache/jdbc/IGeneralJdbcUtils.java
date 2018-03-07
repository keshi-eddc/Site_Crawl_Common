package fun.jerry.cache.jdbc;

import java.util.List;
import java.util.Map;

import fun.jerry.entity.Model;
import fun.jerry.entity.SqlEntity;

/**
 * 通用JDBC操作
 * @author conner
 *
 */
public interface IGeneralJdbcUtils <T extends Model> {
	
	void batchExecute(List<SqlEntity> list);
	
	List<T> queryForListObject(SqlEntity sqlEntity, Class<T> clazz);

	List<Map<String, Object>> queryForListMap(SqlEntity sqlEntity);

	Map<String, Object> queryOne(SqlEntity sqlEntity);
	
	T queryOne(SqlEntity sqlEntity, Class<T> clazz);

	void procedureNoResult(SqlEntity sqlEntity);
	
	void procedureWithResult(SqlEntity sqlEntity);

	void execute(SqlEntity sqlEntity);
	
	void deleteByLogicalPrimaryKey(SqlEntity sqlEntity);
	
	boolean exist(SqlEntity sqlEntity);
	
}
