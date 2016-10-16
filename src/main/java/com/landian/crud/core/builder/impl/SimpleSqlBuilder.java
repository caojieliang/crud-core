package com.landian.crud.core.builder.impl;

import com.landian.crud.core.builder.SqlBuilder;
import com.landian.crud.core.builder.SelectBuilder;
import com.landian.crud.core.context.ResultMapContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.order.Order;
import com.landian.sql.jpa.order.OrderAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.landian.sql.builder.SQL;

import java.util.List;
import java.util.Map;

/**
 *  SimpleSqlBuilder
 */
public class SimpleSqlBuilder implements SqlBuilder {

	public static SimpleSqlBuilder newInstance(String tableName, Class clazz,
                                               SelectBuilder selectBuilder, CriterionAppender criterionAppender, OrderAppender proxyOrderAppender){
		return new SimpleSqlBuilder(tableName, clazz,selectBuilder,criterionAppender,proxyOrderAppender);
	}

	private SimpleSqlBuilder(String tableName, Class clazz,
			SelectBuilder selectBuilder,CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender) {
		this.tableName = tableName;
		this.clazz = clazz;
		this.selectBuilder = selectBuilder;
		this.criterionAppender = criterionAppender;
		this.proxyOrderAppender = proxyOrderAppender;
	}
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 
	 */
	private Class clazz;
	/**
	 * 字段选择器
	 */
	private SelectBuilder selectBuilder;
	/**
	 * 条件追加器
	 */
	private CriterionAppender criterionAppender;
	/**
	 * 排序追加器
	 */
	private OrderAppender proxyOrderAppender;
	
	/**
	 * 实现接口
	 */
	@Override
	public String SQL(){
		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT(selectBuilder.SQL());
		sqlBuilder.FROM(tableName);
		buildCriterions(sqlBuilder);
		buildOrder(sqlBuilder);
		return sqlBuilder.toString();
	}
	
	/**
	 * 实现接口
	 */
	@Override
	public String SQLPage(int start, int size){
		StringBuffer sb = new StringBuffer(SQL());
        sb.append(" limit ");
        sb.append("" + start + ",");
        sb.append("" + size);
        return sb.toString();
	}
	
	/**
	 * 实现接口
	 */
	@Override
	public String SQLCount(){
		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("count(*)");
		sqlBuilder.FROM(tableName);
		buildCriterions(sqlBuilder);
//		buildOrder(sqlBuilder);
		return sqlBuilder.toString();
	}
	
	/**
	 * build条件
	 */
	private void buildCriterions(SQL sqlBuilder){
		if(null != criterionAppender){
			List<Criterion> criterions = criterionAppender.getCriterions();
			for(Criterion criterion : criterions){
				if(null != criterion){
					String sql = criterion.SQL();
					if(StringUtils.isNotBlank(sql)){
						sqlBuilder.WHERE(sql);
					}
				}
			}
		}
	}
	
	/**
	 * build排序
	 */
	private void buildOrder(SQL sqlBuilder){
		if(null != proxyOrderAppender){
			List<Order> proxyOrderList = proxyOrderAppender.getOrders();
			if(!CollectionUtils.isEmpty(proxyOrderList)){
				Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(clazz);
				for(Order proxyOrder : proxyOrderList){
					if(null != proxyOrder){
						String orderStr = resultMappingMap.get(proxyOrder.getProperty()).getColumn() + " " + proxyOrder.getSortKey();
						sqlBuilder.ORDER_BY(orderStr);
					}
				}
			}
		}
	}
	
}
