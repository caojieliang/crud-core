package com.landian.crud.core.builder.impl;


import com.landian.crud.core.builder.SelectBuilder;
import com.landian.crud.core.context.ResultMapContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.criterion.Field;
import com.landian.sql.jpa.criterion.FieldAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 基于resultMap的字段选择器
 */
public class FieldAppenderSelectBuilder implements SelectBuilder {

	public FieldAppenderSelectBuilder(FieldAppender fieldAppender,Class clazz) {
		this.fieldAppender = fieldAppender;
		this.clazz = clazz;
	}
	
	/**
	 * 
	 */
	private StringBuffer sqlBuilder = new StringBuffer();
	
	/**
	 *
	 */
	private FieldAppender fieldAppender = null;
	/**
	 * Class
	 */
	private Class clazz = null;
	

	/**
	 * 实现接口
	 */
	@Override
	public String SQL() {
		List<Field> fields = fieldAppender.getFields();
		if(CollectionUtils.isEmpty(fields)){
			throw new RuntimeException("fieldAppender字段列表不能为空！");
		}
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(clazz);
		for(Field field : fields){
			String fieldName = field.getField();
			ResultMappingVirtual resultMappingVirtual = resultMappingMap.get(fieldName);
			String column = resultMappingVirtual.getColumn();
			sqlBuilder.append(column + ",");

		}
		String sql = sqlBuilder.toString();
		if(StringUtils.isNotBlank(sqlBuilder.toString())){
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;

	}

}
