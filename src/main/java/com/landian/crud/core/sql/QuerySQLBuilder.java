package com.landian.crud.core.sql;


import com.landian.crud.core.builder.SelectBuilder;
import com.landian.crud.core.builder.impl.SimpleSqlBuilder;
import com.landian.crud.core.context.ResultMapContext;
import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.order.Order;
import com.landian.sql.jpa.order.OrderAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 查询的SimpleSqlBuilder存在SQL注入问题，第一波了解过后，觉得突破口在Criterion接口的实现上，
 * 可以在子类增加实现一个接口(返回type, sql, column, valueArr)等数据，供jdbcTemplate使用
 * @see SimpleSqlBuilder
 */
public class QuerySQLBuilder {

	/**
	 * @param tableName
	 * @param clazz
	 * @param selectBuilder
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	@Deprecated
	private static QuerySQL builder(String tableName, Class clazz, SelectBuilder selectBuilder,
                                     CriterionAppender criterionAppender, OrderAppender proxyOrderAppender){
		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT(selectBuilder.SQL());
		sqlBuilder.FROM(tableName);
		buildCriterions(sqlBuilder,criterionAppender);
		buildOrder(sqlBuilder, proxyOrderAppender, clazz);
		return null;
	}

	/**
	 * build条件
	 */
	private static void buildCriterions(SQL sqlBuilder, CriterionAppender criterionAppender){
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
	private static void buildOrder(SQL sqlBuilder, OrderAppender proxyOrderAppender, Class clazz){
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
