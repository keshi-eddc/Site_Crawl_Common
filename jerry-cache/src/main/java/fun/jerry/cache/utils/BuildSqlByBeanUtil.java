package fun.jerry.cache.utils;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.cache.context.SqlContext;

public class BuildSqlByBeanUtil {
	
	private static Logger log = CacheLogConfig.getCacheLog();
	
	 public static final String CONTROL_CHAR_REGEX
		= "[\\x00\\x01\\x02\\x03\\x04\\x05\\x06\\x07"
		+ "\\x08\\x09\\x0a\\x0b\\x0c\\x0d\\x0e\\x0f"
		+ "\\x10\\x11\\x12\\x13\\x14\\x15\\x16\\x17"
		+ "\\x18\\x19\\x1a\\x1b\\x1c\\x1d\\x1e\\x1f"
		+ "\\x7f]";
	
	/**
	 * 生成Hive的一行数据，按照一定的字段顺序
	 * @param list
	 * @return
	 */
	public static StringBuilder hive(Object obj) {
		StringBuilder lineAppender = new StringBuilder();
		return lineAppender;
	}
	
	public static SqlContext insert(Object entity) {
		Class<?> clazz = entity.getClass();
		
		String tableName = ClassUtils.getTableName(clazz);
		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(tableName);
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(clazz);
		Map<String, String> fieldColumnMapping = ClassUtils.getObjectFieldColumnMapping(clazz);
		List<Field> execludeFields = ClassUtils.getBeanInsertExecludeFields(clazz);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		sql.append("(");
		StringBuilder args = new StringBuilder();
//		args.append("(");
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			
			boolean needExeclude = false;
			for (Field field : execludeFields) {
				if (field.getName().equalsIgnoreCase(pd.getName())) {
					needExeclude = true;
				}
			}
			if (needExeclude) {
				continue;
			}
			
			for (String key : fieldColumnMapping.keySet()) {
				if (key.equalsIgnoreCase(pd.getName())) {
					sql.append(fieldColumnMapping.get(key));
				}
			}
//			sql.append(fieldColumnMapping.get(pd.getName()));
			if (pd.getPropertyType() == String.class
					|| pd.getPropertyType() == Date.class
					|| pd.getPropertyType() == List.class) {
				//将单引号变成两个单引号，防止insert错误, 将控制符替换掉，否则bcp导出会换行
				args.append("'" + value.toString().replaceAll(CONTROL_CHAR_REGEX, "").replace("'", "''") + "'");
				args.append(",");
			} else {
				args.append(value);
				args.append(",");
			}
			params.add(value);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		args.deleteCharAt(args.length() - 1);
//		args.append(")");
		sql.append(")");
		sql.append(" select ");
		sql.append(args);
		return new SqlContext(sql, params);
	}
	
	public static SqlContext insertNotExists(Object entity) {
		StringBuilder sql = insert(entity).getSql().append(" where not exists (" + queryByKey(entity).getSql().toString() + ")");
		SqlContext sqlContext = new SqlContext(sql, null);
		return sqlContext;
	}

	/**
	 * 构建更新sql
	 * 
	 * @param entity
	 * @param nameHandler
	 * @return
	 */
	public static SqlContext update(Object entity) {
		Class<?> clazz = entity.getClass();
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(entity.getClass());
		Map<String, String> fieldColumnMapping = ClassUtils.getObjectFieldColumnMapping(clazz);
		List<String> keys = ClassUtils.getLogicalKey(clazz);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		String tableName = ClassUtils.getTableName(clazz);
		StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");
		StringBuilder sqlWhere = new StringBuilder(" where ");
		List<Object> params = new ArrayList<Object>();
		
		List<Field> execludeFields = ClassUtils.getBeanUpdateExecludeFields(clazz);
		
		//组装where条件部分
		for (int i = 0; i < keys.size(); i++) {
			for (PropertyDescriptor pd : pds) {
				if (keys.get(i).equalsIgnoreCase(fieldColumnMapping.get(pd.getName()))) {
					Object value = getReadMethodValue(pd.getReadMethod(), entity);
					sqlWhere.append((i > 0 ? " and " : "") + keys.get(i)).append(" = ");
					if (pd.getPropertyType() == String.class
							|| pd.getPropertyType() == Date.class) {
						sqlWhere.append("'").append(value).append("'");
					} else {
						sqlWhere.append(value);
					}
				}
			}
		}
		
		//组装update部分
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			
			boolean needExeclude = false;
			for (Field field : execludeFields) {
				if (field.getName().equalsIgnoreCase(pd.getName())) {
					needExeclude = true;
				}
			}
			if (needExeclude) {
				continue;
			}
			
			boolean flag = false;
			for (String key : keys) {
				if (key.equalsIgnoreCase(pd.getName())) {
					flag = true;
				}
			}
			if (flag) {
				continue;
			}
			
			sql.append(fieldColumnMapping.get(pd.getName())).append(" = ");
			
			if (pd.getPropertyType() == String.class
					|| pd.getPropertyType() == Date.class) {
				
				sql.append("'").append(value.toString().replace("'", "''")).append("'");
				
			} else {
				if (pd.getName().equalsIgnoreCase("version")) {
					sql.append("version + 1");
				} else {
					sql.append(value);
				}
			}
			sql.append(", ");
		}
		//有的表是其他人create的，不一定有update_time字段
//		if (clazz.getSuperclass() == Model.class) {
//			sql.append(" update_time = getDate() ");
//		} else {
			sql.deleteCharAt(sql.lastIndexOf(","));
//		}
		sql.append(sqlWhere);
		
		return new SqlContext(sql, params);
	}
	
	/**
	 * 构建查询条件
	 * 
	 * @param entity
	 * @param nameHandler
	 */
	/*
	public static SqlContext query(Object entity) {
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(entity.getClass());
		// PropertyDescriptor[] pds =
		// BeanUtils.getPropertyDescriptors(entityClass);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		StringBuilder condition = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			if (count > 0) {
				condition.append(" and ");
			}
			condition.append(nameHandler.getColumnName(pd.getName()));
			condition.append(" = ?");
			params.add(value);
			count++;
		}
		return new SqlContext(condition, params);
	}*/

	/**
	 * 获取属性值
	 *
	 * @param readMethod
	 * @param entity
	 * @return
	 */
	private static Object getReadMethodValue(Method readMethod, Object entity) {
		Object obj = null;
		if (readMethod == null) {
			return null;
		}
		try {
			if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
				readMethod.setAccessible(true);
			}
			obj = readMethod.invoke(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static SqlContext query(Object entity) {
		Class<?> clazz = entity.getClass();
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(entity.getClass());
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		String tableName = ClassUtils.getTableName(clazz);
		StringBuilder sql = new StringBuilder("select * from ");
		sql.append(tableName);
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			if (count > 0) {
				sql.append(" and ");
			} else {
				sql.append(" where ");
			}
			sql.append(pd.getName());
			sql.append(" = ");
			if (pd.getPropertyType() == String.class
					|| pd.getPropertyType() == Date.class) {
				sql.append("'").append(value).append("'");
			} else {
				sql.append(value);
			}
			count++;
		}
		return new SqlContext(sql, params);
	}
	
	public static SqlContext queryByKey(Object entity) {
		Class<?> clazz = entity.getClass();
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(entity.getClass());
		List<String> keys = ClassUtils.getLogicalKey(clazz);
		Map<String, String> fieldColumnMapping = ClassUtils.getObjectFieldColumnMapping(clazz);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		String tableName = ClassUtils.getTableName(clazz);
		StringBuilder sql = new StringBuilder("select * from ");
		sql.append(tableName);
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			
			for (String key : keys) {
				if (key.equalsIgnoreCase(fieldColumnMapping.get(pd.getName()))) {
					if (count > 0) {
						sql.append(" and ");
					} else {
						sql.append(" where ");
					}
					sql.append(key);
					sql.append(" = ");
					if (pd.getPropertyType() == String.class
							|| pd.getPropertyType() == Date.class) {
						sql.append("'").append(value).append("'");
					} else {
						sql.append(value);
					}
					count++;
				}
			}
		}
		return new SqlContext(sql, params);
	}
	
	public static SqlContext deleteByKey(Object entity) {
		Class<?> clazz = entity.getClass();
		// 获取属性信息
		BeanInfo beanInfo = ClassUtils.getSelfBeanInfo(entity.getClass());
		List<String> keys = ClassUtils.getLogicalKey(clazz);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		String tableName = ClassUtils.getTableName(clazz);
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(tableName);
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		for (PropertyDescriptor pd : pds) {
			Object value = getReadMethodValue(pd.getReadMethod(), entity);
			if (value == null) {
				continue;
			}
			
			for (String key : keys) {
				if (key.equalsIgnoreCase(pd.getName())) {
					if (count > 0) {
						sql.append(" and ");
					} else {
						sql.append(" where ");
					}
					sql.append(key);
					sql.append(" = ");
					if (pd.getPropertyType() == String.class
							|| pd.getPropertyType() == Date.class) {
						sql.append("'").append(value).append("'");
					} else {
						sql.append(value);
					}
					count++;
				}
			}
		}
		return new SqlContext(sql, params);
	}
	
	public static void main(String[] args) {
		
	}
}
