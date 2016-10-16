package com.landian.crud.core.builder.impl;

import com.landian.crud.core.builder.SelectBuilder;
import com.landian.sql.jpa.sql.SelectUnit;
import com.landian.sql.jpa.sql.SelectUnitAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * SelectUnitAppender的字段选择器
 */
public class SelectUnitSelectBuilder implements SelectBuilder {

	public static SelectUnitSelectBuilder newInstance(SelectUnitAppender selectUnitAppender){
		return new SelectUnitSelectBuilder(selectUnitAppender);
	}

	private SelectUnitSelectBuilder(SelectUnitAppender selectUnitAppender) {
		this.selectUnitAppender = selectUnitAppender;
	}
	
	/**
	 *
	 */
	private SelectUnitAppender selectUnitAppender;

	/**
	 * 实现接口
	 */
	@Override
	public String SQL() {
		StringBuffer sqlBuilder = new StringBuffer();
		if(null == selectUnitAppender) {
			throw new RuntimeException("selectUnitAppender不能为空");
		}
		List<SelectUnit> selectUnits = selectUnitAppender.getSelectUnits();
		if(CollectionUtils.isEmpty(selectUnits)){
			throw new RuntimeException("selectUnit列表不能为空");
		}
		for(SelectUnit selectUnit : selectUnits) {
			sqlBuilder.append(selectUnit.SQL() + ",");
		}
		String sql = sqlBuilder.toString();
		if(StringUtils.isNotBlank(sqlBuilder.toString())){
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

}
