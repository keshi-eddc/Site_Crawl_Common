package fun.jerry.cache.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.utils.BuildSqlByBeanUtil;
import fun.jerry.cache.utils.BuildSqlUtil;
import fun.jerry.cache.utils.ClassUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.entity.DataSource;
import fun.jerry.entity.Model;
import fun.jerry.entity.PropertyOrder;
import fun.jerry.entity.SqlEntity;
import fun.jerry.entity.SqlType;
import fun.jerry.entity.system.ExecuteFailLog;

/**
 * 通用JDBC操作
 * @author conner
 *
 */
@Component
public class GeneralJdbcUtils<T extends Model> implements IGeneralJdbcUtils<T> {
	
	private Logger log = CacheLogConfig.getCacheLog();

	private static final int MAX_BATCH_SIZE = 500;
	
//	@Autowired
//	private JdbcTemplate jdbcTemplateCrawlElve;
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplateSGM;
	
	/** 是否将sql保存到文件中, 0:保存到数据库；1：将拼接的sql保存到文件，用于数据库服务器压力太大时暂时保存文件；2：将对象按一定的字段顺序保存到文件中，用于将文件保存到Hive中使用 */
	@Value(value = "${sql_append_file}")
	private int sqlAppendFile;
	
	/** 将sql追加到文件中的路径 */
	@Value(value = "${path_sql_file}")
	private String pathSqlFile;
	
	/** 将sql追加到文件的文件名称 */
	@Value(value = "${append_file_name}")
	private String appendFileName;
	
	/** 需要导入到hive的out文件路径 */
	@Value(value = "${hive_output_path}")
	private String hiveOutputPath;
	
	@Override
	public void batchExecute(List<SqlEntity> list) {
		
		if (CollectionUtils.isEmpty(list)) {
			log.info("batchExecute list is empty, return");
			return;
		}
		
		for (DataSource ds : DataSource.values()) {
			
			if (sqlAppendFile == 2){
				List<Object> ds_list = new ArrayList<Object>();
				for (SqlEntity sqlEntity : list) {
					if (sqlEntity.getDataSource() == ds) {
						ds_list.add(sqlEntity.getObj());
					}
				}
				if (CollectionUtils.isNotEmpty(ds_list)) {
					appendHiveFile(ds_list);
				}
				
			} else {
				List<String> sqls = new ArrayList<String>();
				for (int i = 0; i < list.size(); i++) {
					SqlEntity sqlEntity = list.get(i);
					if (sqlEntity.getDataSource() == ds) {
						sqls.add(getSql(sqlEntity));
					}
				}
				if (CollectionUtils.isNotEmpty(sqls)) {
					if (sqlAppendFile == 0) {
						BlockingQueue<String> sqlQueue = new LinkedBlockingQueue<>();
						sqlQueue.addAll(sqls);
						while (CollectionUtils.isNotEmpty(sqlQueue)) {
							List<String> sqlList = new ArrayList<String>();
							sqlQueue.drainTo(sqlList, MAX_BATCH_SIZE);
							String [] _sqls = sqlList.toArray(new String[sqlList.size()]);
							try {
								getJdbcTemplate(ds).batchUpdate(_sqls);
								log.info("save once time");
							} catch (Exception e) {
								// 批量处理中发现有异常的时候，调用单条保存
								for (SqlEntity se : list) {
									execute(se);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void appendHiveFile(List<Object> ds_list) {
		for (Object obj : ds_list) {
			StringBuilder lineData = new StringBuilder();
			String fileName = ClassUtils.getHiveFileName(obj.getClass());
			List<PropertyOrder> orderList = ClassUtils.getObjectPropertyOrder(obj.getClass());
			int n = 0;
			for (PropertyOrder order : orderList) {
				Object columnData = ClassUtils.getAttributeVlaue(obj, order.getName());
				if (n++ > 0) {
					lineData.append("\001");
				}
				if (columnData != null) {
					columnData = columnData.toString().replaceAll(BuildSqlUtil.CONTROL_CHAR_REGEX, "");
					lineData.append(columnData);
				}
			}
			// 追加site和ym字段
			lineData.append("\001");
			Object site = ClassUtils.getAttributeVlaue(obj, "site");
			lineData.append(site);
			lineData.append("\001");
			Object ym = ClassUtils.getAttributeVlaue(obj, "ym");
			lineData.append(ym);
//			lineData.append("\n");
			
//			FileOperateSupport.append_BufferedIOStream(lineData.toString(), hiveOutputPath, fileName);
		}
	}
	
	public List<T> queryForListObject(SqlEntity sqlEntity, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		String sql = getSql(sqlEntity);
		list = getJdbcTemplate(sqlEntity.getDataSource()).query(sql, new Object[] {}, new BeanPropertyRowMapper<T>(clazz));
		return list;
	}
	
	public List<Map<String, Object>> queryForListMap(SqlEntity sqlEntity) {
		String sql = getSql(sqlEntity);
		return getJdbcTemplate(sqlEntity.getDataSource()).queryForList(sql);
	}

	@Override
	public boolean exist(SqlEntity sqlEntity) {
		// 查看对象是否是有主键的，没有主键的无法做是否存在的判断
		Class<?> clazz = sqlEntity.getObj().getClass();
		List<String> keys = ClassUtils.getLogicalKey(clazz);
		if (CollectionUtils.isEmpty(keys)) {
			return false;
		}
		String sql = getSql(sqlEntity);
		List<Map<String, Object>> mapList = getJdbcTemplate(sqlEntity.getDataSource()).queryForList(sql);
		return CollectionUtils.isNotEmpty(mapList);
	}

	@Override
	public void execute(SqlEntity sqlEntity) {
		String sql = "";
		try {
			sql = getSql(sqlEntity);
			getJdbcTemplate(sqlEntity.getDataSource()).execute(sql);
		} catch (DuplicateKeyException e) {
			log.error(sqlEntity + " 主键重复, 该数据遭抛弃.");
		} catch (Exception e) {
			// 单条数据保存的时候，发现非主键错误的，将sql记录到 Execute_Fail_Log 表中
			log.error(sqlEntity + " 单条执行错误： ", e);
//			ExecuteFailLog executeFailLog = new ExecuteFailLog();
//			executeFailLog.setExecuteSql(sql);
//			execute(new SqlEntity(executeFailLog, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
		}
	}

	@Override
	public Map<String, Object> queryOne(SqlEntity sqlEntity) {
		String sql = getSql(sqlEntity);
		List<Map<String, Object>> list = getJdbcTemplate(sqlEntity.getDataSource()).queryForList(sql);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public T queryOne(SqlEntity sqlEntity, Class<T> clazz) {
		List<T> list = queryForListObject(sqlEntity, clazz);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public void deleteByLogicalPrimaryKey (SqlEntity sqlEntity) {
		try {
			execute(sqlEntity);
		} catch (Exception e) {
			log.error("deleteByLogicalPrimaryKey " + sqlEntity, e);
		}
		
		
	}
	
	private String getSql(SqlEntity sqlEntity) {
		String sql = "";
		try {
			if (null != sqlEntity.getSqlType()) {
				if (sqlEntity.getSqlType() == SqlType.PARSE_INSERT) {
					sql = BuildSqlByBeanUtil.insert(sqlEntity.getObj()).getSql().toString();
				} else if (sqlEntity.getSqlType() == SqlType.PARSE_INSERT_NOT_EXISTS) {
					sql = BuildSqlByBeanUtil.insertNotExists(sqlEntity.getObj()).getSql().toString();
				} else if (sqlEntity.getSqlType() == SqlType.PARSE_UPDATE) {
					sql = BuildSqlByBeanUtil.update(sqlEntity.getObj()).getSql().toString();
				} else if (sqlEntity.getSqlType() == SqlType.PARSE_NO) {
					sql = sqlEntity.getObj().toString();
				} else if (sqlEntity.getSqlType() == SqlType.PARSE_QUERY_BY_LOGICALPRIMARYKEY) {
					sql = BuildSqlUtil.queryByKey(sqlEntity.getObj()).getSql().toString();
				} else if (sqlEntity.getSqlType() == SqlType.PARSE_DELETE_BY_LOGICALPRIMARYKEY) {
					sql = BuildSqlUtil.deleteByKey(sqlEntity.getObj()).getSql().toString();
				}
				
				// 用户信息很多会重复，导致在每个批处理保存中很大机率出现主键错误的异常
				// 所以为用户信息单独处理，现将所有sql保存到 Execute_Fail_Log 表中
				Object obj = sqlEntity.getObj();
				if (obj instanceof Model) {
					Model model = (Model) obj;
					if (model.getSaveMode() == 2) {
						ExecuteFailLog executeFailLog = new ExecuteFailLog();
						executeFailLog.setObjName(obj.getClass().getSimpleName());
						executeFailLog.setTableName(ClassUtils.getTableName(obj.getClass()));
						
						List<String> keys = ClassUtils.getLogicalKey(obj.getClass());
						StringBuilder primaryKey = new StringBuilder();
						StringBuilder primaryKeyValue = new StringBuilder();
						for (String key : keys) {
							primaryKey.append(key).append("-");
							primaryKeyValue.append(ClassUtils.getAttributeVlaue(obj, key)).append("-");
						}
						
						executeFailLog.setPrimaryKey(primaryKey.substring(0, primaryKey.length() - 1));
						executeFailLog.setPrimaryKeyValue(primaryKeyValue.substring(0, primaryKeyValue.length() - 1));
						executeFailLog.setExecuteSql(sql);
						
						sqlEntity.setSqlType(SqlType.PARSE_INSERT);
						sqlEntity.setObj(executeFailLog);
						sqlEntity.setDataSource(DataSource.DATASOURCE_SGM);
						
						sql = getSql(sqlEntity);
					}
				}
			}
		} catch (Exception e) {
			log.error(sqlEntity + " 解析sql报错，", e);
		}
		return sql;
	}
	
	private JdbcTemplate getJdbcTemplate(DataSource ds) {
		if (null == ds) {
			throw new RuntimeException("SqlEntity No DataSource");
		}
		if (ds == DataSource.DATASOURCE_CrawlElve) {
			return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateCrawlElve");
		} else if (ds == DataSource.DATASOURCE_SGM) {
			return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateSGM");
		} else if (ds == DataSource.DATASOURCE_Budweiser) {
			return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateBudweiser");
		} else if (ds == DataSource.DATASOURCE_Monitor) {
			return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateMonitor");
		} else if (ds == DataSource.DATASOURCE_EPOS) {
			return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateEPOS");
		} else if (ds == DataSource.DATASOURCE_SouFang) {
		    return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateSouFang"); 
		} else if (ds == DataSource.DATASOURCE_ProxyIP) {
		    return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateProxy"); 
		} else if (ds == DataSource.DATASOURCE_DianPing) {
		    return (JdbcTemplate) ApplicationContextHolder.getBean("jdbcTemplateDianPing"); 
		} else {
			return null;
		}
	}
	
	private void exception () {
		
	}
	
	public static void main(String[] args) {
	}

	@Override
	public void procedureNoResult(SqlEntity sqlEntity) {
		try {
			getJdbcTemplate(sqlEntity.getDataSource()).execute(sqlEntity.getObj().toString());
		} catch (Exception e) {
			log.error(sqlEntity, e);
		}
	}

	@Override
	public void procedureWithResult(SqlEntity sqlEntity) {
		// TODO Auto-generated method stub
		
	}

}
