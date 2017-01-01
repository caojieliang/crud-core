package com.landian.crud.core.sql;

import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.annotation.IdTypePolicy;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.sql.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *  UpdateSqlBuilder
 *  @see UpdateSQLBuilder
 */
public class UpdateSqlBuilder2 {

	public static UpdateSQL build(BeanContext beanContext,UpdateUnitAppender updateUnitAppender,
								  CriterionAppender criterionAppender){
		IdTypePolicy idType = beanContext.getIdType();
		SQL sql = buildSQL();
		sql.UPDATE(beanContext.getTableName());
		List<Object> updateParams = buildUpdateParams(sql, updateUnitAppender);
		buildWhereParams(sql, criterionAppender);
		List<Object> whereParams = Collections.emptyList();
		return UpdateSQL.newInstance(idType,sql,updateParams,whereParams);
	}

	private static void buildWhereParams(SQL sqlBuilder , CriterionAppender criterionAppender) {
		if(null == criterionAppender){
			throw new RuntimeException("criterionAppender must is null");
		}
		if(CollectionUtils.isEmpty(criterionAppender.getCriterions())){
			throw new RuntimeException("criterionAppender criterions is empty");
		}
		for(Criterion criterion : criterionAppender.getCriterions()){
			if(null != criterion){
				String sql = criterion.SQL();
				if(StringUtils.isNotBlank(sql)){
					sqlBuilder.WHERE(sql);
				}
			}
		}
	}

	private static List<Object> buildUpdateParams(SQL sqlBuilder , UpdateUnitAppender updateUnitAppender) {
		List<Object> list = new ArrayList();
		//更新时严格检验更新单元和条件单元一定要有
		if(null == updateUnitAppender){
			throw new RuntimeException("updateUnitAppender is null");
		}
		if(CollectionUtils.isEmpty(updateUnitAppender.getUpdateUnits())){
			throw new RuntimeException("updateUnitAppender updateUnitsList is empty");
		}
		for(UpdateUnit updateUnit : updateUnitAppender.getUpdateUnits()){
			if(updateUnit instanceof UpdateUnitEqual){
				UpdateUnitEqual update = (UpdateUnitEqual) updateUnit;
				String column = update.getColumn();
				Object value = update.getValue();
				sqlBuilder.SET(column + " = ? ");
				list.add(value);
			}else if(updateUnit instanceof UpdateUnitNull){
				UpdateUnitNull update = (UpdateUnitNull) updateUnit;
				String column = update.getColumn();
				sqlBuilder.SET(column + " = ? ");
				list.add(null);
			}else if(updateUnit instanceof UpdateUnitSQL){
				sqlBuilder.SET(updateUnit.SQL());
			}else{
				throw new RuntimeException("尚未解释UpdateUnit实现类型：" + updateUnit.getClass().getName());
			}
		}
		return list;
	}

	private static SQL buildSQL() {
		return new SQL();
	}


}
