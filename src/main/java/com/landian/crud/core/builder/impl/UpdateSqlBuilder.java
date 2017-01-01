package com.landian.crud.core.builder.impl;

import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.sql.UpdateUnit;
import com.landian.sql.jpa.sql.UpdateUnitAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.landian.sql.builder.SQL;


/**
 *  UpdateSqlBuilder
 *  @see com.landian.crud.core.sql.UpdateSQLBuilder
 */
@Deprecated
public class UpdateSqlBuilder{
	
	public static UpdateSqlBuilder getInstance(String tableName,
			UpdateUnitAppender updateUnitAppender,
			CriterionAppender criterionAppender){
		return new UpdateSqlBuilder(tableName,updateUnitAppender,criterionAppender);
	}
	
	private UpdateSqlBuilder(String tableName,
			UpdateUnitAppender updateUnitAppender,
			CriterionAppender criterionAppender) {
		this.tableName = tableName;
		this.updateUnitAppender = updateUnitAppender;
		this.criterionAppender = criterionAppender;
	}
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 条件追加器
	 */
	private CriterionAppender criterionAppender;
	/**
	 * 排序追加器
	 */
	private UpdateUnitAppender updateUnitAppender;
	
	/**
	 *
	 */
	public String SQL(){
		SQL sqlBuilder = new SQL();
		sqlBuilder.UPDATE(tableName);
		//更新时严格检验更新单元和条件单元一定要有
		if(null == updateUnitAppender){
			throw new RuntimeException("updateUnitAppender is null");
		}
		if(CollectionUtils.isEmpty(updateUnitAppender.getUpdateUnits())){
			throw new RuntimeException("updateUnitAppender updateUnitsList is empty");
		}
		if(null == criterionAppender){
			throw new RuntimeException("criterionAppender must is null");
		}
		if(CollectionUtils.isEmpty(criterionAppender.getCriterions())){
			throw new RuntimeException("criterionAppender criterions is empty");
		}
		for(UpdateUnit updateUnit : updateUnitAppender.getUpdateUnits()){
			sqlBuilder.SET(updateUnit.SQL());
		}
		for(Criterion criterion : criterionAppender.getCriterions()){
			if(null != criterion){
				String sql = criterion.SQL();
				if(StringUtils.isNotBlank(sql)){
					sqlBuilder.WHERE(sql);
				}
			}
		}
		return sqlBuilder.toString();
	}
	
}
