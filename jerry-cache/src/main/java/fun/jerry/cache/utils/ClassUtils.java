package fun.jerry.cache.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fun.jerry.cache.common.log.CacheLogConfig;
import fun.jerry.entity.PropertyOrder;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.HiveColumnOrder;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

public class ClassUtils {
	
	private static Logger log = CacheLogConfig.getCacheLog();

	private static final Map<Class<?>, BeanInfo> classCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, BeanInfo>());

	private static final Map<Class<?>, String> beanTableCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, String>());
	
	private static final Map<Class<?>, Map<String, String>> BEAN_FIELD_COLUMN_CACHE = Collections.synchronizedMap(new WeakHashMap<Class<?>, Map<String, String>>());

	private static final Map<Class<?>, String> beanFileCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, String>());

	private static final Map<Class<?>, List<PropertyOrder>> beanPropertyOrderCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, List<PropertyOrder>>());

	/** bean的属性中在构建insert sql的时候排除在外的 */
	private static final Map<Class<?>, List<Field>> beanInsertExeclueCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, List<Field>>());

	/** bean的属性中在构建update sql的时候排除在外的 */
	private static final Map<Class<?>, List<Field>> beanUpdateExeclueCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, List<Field>>());

	private static final Map<Class<?>, List<String>> logicalKeysCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, List<String>>());

	/**
	 * 通过className获取类信息
	 * @param className
	 * @return
	 */
	public static BeanInfo getSelfBeanInfo(Class<?> clazz) {
		
		BeanInfo beanInfo = null;
		
		try {
			if (classCache.get(clazz) == null) {
				for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
					for (Field field : temp.getDeclaredFields()) {
						//排除序列化field
						if (!field.getName().equals("serialVersionUID")) {
							//判断类型是否为基础数据类型，必须为封装类型，保证new的对象初始值为null
							if (field.getType().isPrimitive()) {
								throw new RuntimeException(temp.getName() + " " + field.getName() + " 类型必须为包装类");
							}
						}
					}
				}
				
//				beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
				//获取包含继承自父类的属性
				beanInfo = Introspector.getBeanInfo(clazz, Object.class);
				
				classCache.put(clazz, beanInfo);
				// Immediately remove class from Introspector cache, 
				// to allow for proper garbage collection on class loader shutdown - we cache it here anyway, in a GC-friendly manner. 
				// In contrast to CachedIntrospectionResults,
				// Introspector does not use WeakReferences as values of its WeakHashMap!
				Class<?> classToFlush = clazz;
				do {
					Introspector.flushFromCaches(classToFlush);
					classToFlush = classToFlush.getSuperclass();
				} while (classToFlush != null);
			} else {
				beanInfo = classCache.get(clazz);
			}
		} catch (IntrospectionException e) {
			log.error(clazz.getName() + " " + e);
		}
		
		return beanInfo;
	}
	
	/**
	 * 通过className获取类信息
	 * @param className
	 * @return
	 */
	public static List<Field> getBeanInsertExecludeFields(Class<?> clazz) {
		
		List<Field> fields = new ArrayList<Field>();
		
		if (beanInsertExeclueCache.get(clazz) == null) {
			for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
				for (Field field : temp.getDeclaredFields()) {
					//排除在外的field
					if (field.getAnnotation(FieldInsertExclude.class) != null) {
						fields.add(field);
					}
				}
			}
			beanInsertExeclueCache.put(clazz, fields);
			Class<?> classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			fields = beanInsertExeclueCache.get(clazz);
		}
		
		return fields;
	}
	
	/**
	 * 通过className获取类信息
	 * @param className
	 * @return
	 */
	public static List<Field> getBeanUpdateExecludeFields(Class<?> clazz) {
		
		List<Field> fields = new ArrayList<Field>();
		
		if (beanUpdateExeclueCache.get(clazz) == null) {
			for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
				for (Field field : temp.getDeclaredFields()) {
					//排除在外的field
					if (field.getAnnotation(FieldUpdateExclude.class) != null) {
						fields.add(field);
					}
				}
			}
			beanUpdateExeclueCache.put(clazz, fields);
			Class<?> classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			fields = beanUpdateExeclueCache.get(clazz);
		}
		
		return fields;
	}
	
	/**
	 * 通过className获取映射的表
	 * @param className
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		
		String tableName = "";
		
		if (beanTableCache.get(clazz) == null 
				|| beanTableCache.get(clazz).equals("")) {
			
			tableName = clazz.getAnnotation(TableMapping.class).value();
			
			beanTableCache.put(clazz, tableName);
			
			Class<?> classToFlush = clazz;
			
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			tableName = beanTableCache.get(clazz);
		}
		
		if (StringUtils.isEmpty(tableName)) {
			throw new RuntimeException(clazz + " TableMapping is not exist.");
		}
		
		return tableName;
	}
	
	/**
	 * 通过className获取映射的文件名
	 * @param className
	 * @return
	 */
	public static String getHiveFileName(Class<?> clazz) {
		
		String fileName = "";
		
		if (beanFileCache.get(clazz) == null 
				|| beanFileCache.get(clazz).equals("")) {
			ColumnMapping fm = clazz.getAnnotation(ColumnMapping.class);
			if (null == fm) {
//				throw new RuntimeException(clazz + " FileMapping is not exist.");
				log.error(clazz + " FileMapping is not exist.");
			} else {
				fileName = fm.value();
				beanFileCache.put(clazz, fileName);
			}
			
			Class<?> classToFlush = clazz;
			
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			fileName = beanFileCache.get(clazz);
		}
		
		if (StringUtils.isEmpty(fileName)) {
			throw new RuntimeException(clazz + " FileMapping is not exist.");
		}
		
		return fileName;
	}
	
	/**
	 * 获取对象属性对应的顺序
	 * @param className
	 * @return
	 */
	public static List<PropertyOrder> getObjectPropertyOrder(Class<?> clazz) {
		
		List<PropertyOrder> list = new ArrayList<PropertyOrder>();
		
		if (beanPropertyOrderCache.get(clazz) == null 
				|| CollectionUtils.isEmpty(beanPropertyOrderCache.get(clazz))
				|| beanPropertyOrderCache.get(clazz).size() == 0) {
			
			for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
				for (Field field : temp.getDeclaredFields()) {
					HiveColumnOrder fm = field.getAnnotation(HiveColumnOrder.class);
					//排除在外的field
					if (null != fm) {
						list.add(new PropertyOrder(field.getName(), fm.value()));
					}
				}
			}
			
			Collections.sort(list, new Comparator<PropertyOrder>() {
				@Override
				public int compare(PropertyOrder o1, PropertyOrder o2) {
					return o1.getOrder().compareTo(o2.getOrder());
				}
			});
			
			beanPropertyOrderCache.put(clazz, list);
			
			Class<?> classToFlush = clazz;
			
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			list = beanPropertyOrderCache.get(clazz);
		}
		
		if (CollectionUtils.isEmpty(list)) {
			throw new RuntimeException(clazz + " HiveColumnOrder is not exist.");
		}
		
		return list;
	}
	
	/**
	 * 获取Bean的Field和表中Column的Mapping关系
	 * @param clazz
	 * @return
	 */
	public static Map<String, String> getObjectFieldColumnMapping(Class<?> clazz) {
		
		Map<String, String> fieldColumnMapping = new HashMap<String, String>();
		
		if (BEAN_FIELD_COLUMN_CACHE.get(clazz) == null || MapUtils.isEmpty(BEAN_FIELD_COLUMN_CACHE.get(clazz))) {
			
			for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
				for (Field field : temp.getDeclaredFields()) {
					ColumnMapping fm = field.getAnnotation(ColumnMapping.class);
					//排除在外的field
					if (null != fm) {
						fieldColumnMapping.put(field.getName(), fm.value());
					} else {
						fieldColumnMapping.put(field.getName(), field.getName());
					}
				}
			}
			
			BEAN_FIELD_COLUMN_CACHE.put(clazz, fieldColumnMapping);
			
			Class<?> classToFlush = clazz;
			
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			fieldColumnMapping = BEAN_FIELD_COLUMN_CACHE.get(clazz);
		}
		
		if (MapUtils.isEmpty(fieldColumnMapping)) {
			throw new RuntimeException(clazz + " fieldColumnMapping is not exist.");
		}
		
		return fieldColumnMapping;
	}
	
	/**
	 * 通过className获取映射的表
	 * @param className
	 * @return
	 */
	public static List<String> getLogicalKey(Class<?> clazz) {
		
		List<String> keys = new ArrayList<String>();
		
		if (logicalKeysCache.get(clazz) == null) {
			for (Class<?> temp = clazz; temp != Object.class; temp = temp.getSuperclass()) {
				for (Field field : temp.getDeclaredFields()) {
					if (null != field.getAnnotation(LogicalPrimaryKey.class)) {
						keys.add(field.getName());
					}
//					System.out.println(field.getAnnotation(LogicalPrimaryKey.class).value());;
				}
			}
			
			logicalKeysCache.put(clazz, keys);
			// Immediately remove class from Introspector cache, 
			// to allow for proper garbage collection on class loader shutdown - we cache it here anyway, in a GC-friendly manner. 
			// In contrast to CachedIntrospectionResults,
			// Introspector does not use WeakReferences as values of its WeakHashMap!
			Class<?> classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
		} else {
			keys = logicalKeysCache.get(clazz);
		}
		
		return keys;
	}

	public static Object newInstance(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException : " + e.getStackTrace());
		}
		Object obj = null;
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static List<Class<?>> getAllAssignedClass(Class<?> cls) throws IOException, ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Class<?> c : getClasses(cls)) {
			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add(c);
			}
		}
		return classes;
	}

	private static List<Class<?>> getClasses(Class<?> cls) throws IOException, ClassNotFoundException {
		String pk = cls.getPackage().getName();
		String path = pk.replace('.', '/');
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL url = classloader.getResource(path);
		return getClasses(new File(url.getFile()), pk);
	}

	private static List<Class<?>> getClasses(File dir, String pk) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!dir.exists()) {
			return classes;
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClasses(f, pk + "." + f.getName()));
				continue;
			}
			String name = f.getName();
			if (name.endsWith("CrawlHelper.class")) {
//			if (name.endsWith(".class")) {
//				log.info(name);
				classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
			}
		}
		return classes;
	}
	
	public static Object getAttributeVlaue(Object obj, String attributeName) {
        PropertyDescriptor[] propertyDescriptors = getSelfBeanInfo(obj.getClass()).getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equalsIgnoreCase(attributeName)) {
                try {
                    if (propertyDescriptor.getReadMethod() != null) {
                        return propertyDescriptor.getReadMethod().invoke(obj);
                    }
                } catch (InvocationTargetException e) {
                    log.error("获取指定属性值异常 " + obj.getClass().getSimpleName() + " 属性 " + attributeName, e);
                } catch (IllegalAccessException e) {
                	 log.error("获取指定属性值异常 " + obj.getClass().getSimpleName() + " 属性 " + attributeName, e);
                }
 
            }
        }
        return null;
    }
	
	public static void main(String[] args) {
	}
}
