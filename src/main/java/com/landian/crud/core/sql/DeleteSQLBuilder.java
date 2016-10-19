package com.landian.crud.core.sql;

import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;


public class DeleteSQLBuilder{

	private static final Logger logger = Logger.getLogger(DeleteSQLBuilder.class);

	public static String buildDeleteSQL(String tableName, Criterion criterion){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		return buildDeleteSQL(tableName,criterionAppender);
	}

	public static String buildDeleteSQL(String tableName, CriterionAppender criterionAppender){
		SQL sql = new SQL();
		sql.DELETE_FROM(tableName);
		List<Criterion> criterionList = criterionAppender.getCriterions();
		if(CollectionUtils.isEmpty(criterionList)){
			throw new RuntimeException("criterion in CriterionAppender can not be empty list!");
		}
		for (Criterion criterion : criterionList) {
			sql.WHERE(criterion.SQL());
		}
		return sql.toString();
	}

}
