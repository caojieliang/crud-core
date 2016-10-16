package com.landian.crud.core.context;

import com.landian.sql.jpa.annotation.Column;
import com.landian.sql.jpa.annotation.Transient;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResultMapContext {
	
	private static final Logger logger = Logger.getLogger(ResultMapContext.class);
	
	private static Map<String,Map<String,ResultMappingVirtual>> resultMapContext = new HashMap<String,Map<String,ResultMappingVirtual>>();
	private static Map<String,Map<String,ResultMappingVirtual>> resultMapContextInverse = new HashMap<String,Map<String,ResultMappingVirtual>>();

	/**
	 * @param beanClass
	 * @return
	 */
	public synchronized static Map<String,ResultMappingVirtual> getResultMappingMap(Class beanClass){
		String key = beanClass.getName();
		Map<String,ResultMappingVirtual> resultMappingMap = resultMapContext.get(key);
		if(MapUtils.isEmpty(resultMappingMap)){
			//构建关系Map
			resultMappingMap = buildMap(beanClass);
			Map<String,ResultMappingVirtual> resultMappingMapInverse = new HashMap<String,ResultMappingVirtual>();
			Iterator<String> iterator = resultMappingMap.keySet().iterator();
			while(iterator.hasNext()){
				String property = iterator.next();
				ResultMappingVirtual resultMappingVirtual = resultMappingMap.get(property);
				resultMappingMapInverse.put(resultMappingVirtual.getColumn(),resultMappingVirtual);
			}
			resultMapContext.put(key, resultMappingMap);
			resultMapContextInverse.put(key, resultMappingMapInverse);
		}
		return resultMappingMap;
	}
	
	public synchronized static Map<String,ResultMappingVirtual> getResultMappingMapInverse(Class beanClass){
		String key = beanClass.getName();
		Map<String,ResultMappingVirtual> resultMappingMapInverse = resultMapContextInverse.get(key);
		return resultMappingMapInverse;
	}
	
	private static Map<String,ResultMappingVirtual> buildMap(Class beanClass){
		Map<String,ResultMappingVirtual> map = new HashMap<String,ResultMappingVirtual>();
		try {
			Field[] fields = beanClass.getDeclaredFields();
			if(null != fields){
				for (Field field : fields) {
					Transient aTransient = field.getAnnotation(Transient.class); //BossTransient注解
					if(null != aTransient){
						continue;
					}
					String fieldName = field.getName();
					if(ProviderHelper.isIgnoreField(field, fieldName)){
						continue;
					}
					field.setAccessible(true); //可以访问private
					String columnName = fieldName; //默认Java与数据字段一置
					Column column = field.getAnnotation(Column.class); //Column注解
					if(null != column){ //DateRange注解不需要拼接Sql
						columnName = column.column();
					}
					ResultMappingVirtual resultMappingVirtual = ResultMappingVirtual.getInstance(fieldName, columnName);
					map.put(fieldName, resultMappingVirtual);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = "构建bean映射关系Map出错！" + beanClass;
			logger.error(message, e);
		}
		return map;
	}
}
