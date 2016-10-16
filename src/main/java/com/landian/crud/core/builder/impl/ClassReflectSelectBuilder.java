package com.landian.crud.core.builder.impl;

import com.landian.crud.core.builder.SelectBuilder;
import com.landian.crud.core.context.ResultMapContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * 基于class的字段选择器
 */
public class ClassReflectSelectBuilder implements SelectBuilder {

	public static ClassReflectSelectBuilder newInstance(Class clazz){
		return new ClassReflectSelectBuilder(clazz);
	}

	private ClassReflectSelectBuilder(Class clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * 
	 */
	private StringBuffer sqlBuilder = new StringBuffer();
	
	/**
	 * Class
	 */
	private Class clazz = null;
	
	/**
	 * 实现接口
	 */
	@Override
	public String SQL() {
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(clazz);
		Iterator<ResultMappingVirtual> iterator = resultMappingMap.values().iterator();
		while(iterator.hasNext()){
			sqlBuilder.append(iterator.next().getColumn() + ",");
		}
		String sql = sqlBuilder.toString();
		if(StringUtils.isNotBlank(sqlBuilder.toString())){
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

}
