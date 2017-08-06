package com.landian.crud.core.builder.impl;


import com.landian.crud.core.builder
		.SelectBuilder;
import com.landian.crud.core.builder.SqlBuilder;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.order.OrderAppender;

/**
 * SqlBuilderFactory
 * 日后SqlBuilder都由这个获取,需要时间来搬代码了
 * to be continue
 */
public class SqlBuilderFactory {

	/**
	 * 后期有待对接使用QuerySQLBuilder
	 * @param tableName
	 * @param clazz
	 * @param selectBuilder
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public static SqlBuilder builder(String tableName, Class clazz, SelectBuilder selectBuilder,
									CriterionAppender criterionAppender, OrderAppender proxyOrderAppender){
		return SimpleSqlBuilder.newInstance(tableName, clazz,selectBuilder,criterionAppender,proxyOrderAppender);
	}
}
