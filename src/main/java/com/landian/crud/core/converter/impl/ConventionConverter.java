package com.landian.crud.core.converter.impl;


import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.converter.ResultContextConverter;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.utils.BeanToMapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 基于Class的约定大于配置转换器
 */
public class ConventionConverter implements ResultContextConverter {

	public static ConventionConverter newInstance(Class beanClass){
		return new ConventionConverter(beanClass);
	}

	private ConventionConverter(Class beanClass) {
		this.beanClass = beanClass;
	}
	/**
	 * class
	 */
	private Class beanClass;


	@Override
	public Object convert(HashMap<String, Object> dataMap) {
		Map propertyMap = columnMap2PropertyMap(dataMap, beanClass);
		Object object = BeanToMapUtils.toBean(beanClass, propertyMap);
		return object;
	}
	
	/**
	 * 从默认+注解解释
	 * @param dataMap
	 * @param beanClass
	 * @return
	 */
	private Map<String,Object> columnMap2PropertyMap(HashMap<String, Object> dataMap,Class beanClass) {
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(beanClass);
		Collection<ResultMappingVirtual> resultMappingCollection = resultMappingMap.values();
		Map<String,Object> propertyMap = new HashMap<String,Object>();
		if(null != dataMap && dataMap.size() > 0){
			Iterator<String> iter = dataMap.keySet().iterator();
			while(iter.hasNext()){
				Object key = iter.next();
				String columnName = (String) key;
				Object value = dataMap.get(key);
				String propertyName = "";
				//在移植的过程中，先不管效率
				Iterator<ResultMappingVirtual> resultMappingIter = resultMappingCollection.iterator();
				while(resultMappingIter.hasNext()){
					ResultMappingVirtual resultMapping = resultMappingIter.next();
					String mappingColumn = resultMapping.getColumn();
					String mappingProperty = resultMapping.getProperty();
					if(columnName.equals(mappingColumn)){
						propertyName = mappingProperty;
						break;
					}
				}
				propertyMap.put(propertyName, value);
			}
		}
		return propertyMap;
	}
}
