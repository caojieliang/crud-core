package com.landian.crud.core.builder.impl;

import com.landian.crud.core.builder.SqlBuilder;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.order.Order;
import com.landian.sql.jpa.order.OrderAppender;
import com.landian.sql.jpa.sql.SelectUnit;
import com.landian.sql.jpa.sql.SelectUnitAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.landian.sql.builder.SQL;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *  CommonSelectSQLBuilder
 *  date 15/08/26
 */
public class CommonSelectSQLBuilder implements SqlBuilder {
	
	private static final Logger logger = Logger.getLogger(CommonSelectSQLBuilder.class);

	public static CommonSelectSQLBuilder getInstance(String tableName,
		 SelectUnitAppender selectUnitAppender,CriterionAppender criterionAppender){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}

	public static CommonSelectSQLBuilder getInstance(String tableName,
		SelectUnitAppender selectUnitAppender,CriterionAppender criterionAppender,Order proxyOrder){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrder);
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}
	
	public static CommonSelectSQLBuilder getInstance(String tableName,
			SelectUnitAppender selectUnitAppender,
			CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender){
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}
	
	public static CommonSelectSQLBuilder getInstance(String tableName,
			SelectUnit selectUnit,
			CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance();
		selectUnitAppender.select(selectUnit);
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}
	
	public static CommonSelectSQLBuilder getInstance(String tableName,
			SelectUnit selectUnit,
			Criterion criterion,
			Order proxyOrder){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance();
		selectUnitAppender.select(selectUnit);
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		criterionAppender.add(criterion);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		proxyOrderAppender.add(proxyOrder);
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}

	public static CommonSelectSQLBuilder getInstance(String tableName,
			SelectUnit selectUnit,Criterion criterion){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		criterionAppender.add(criterion);
		return CommonSelectSQLBuilder.getInstance(tableName,selectUnit,criterionAppender);
	}

	public static CommonSelectSQLBuilder getInstance(String tableName,
			 SelectUnit selectUnit,
			 CriterionAppender criterionAppender){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance();
		selectUnitAppender.select(selectUnit);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}
	public static CommonSelectSQLBuilder getInstance(String tableName,
			SelectUnit selectUnit,
			Criterion criterion,
			OrderAppender proxyOrderAppender){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance();
		selectUnitAppender.select(selectUnit);
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		criterionAppender.add(criterion);
		return new CommonSelectSQLBuilder(tableName,selectUnitAppender,criterionAppender,proxyOrderAppender);
	}
	
	private CommonSelectSQLBuilder(String tableName,
			SelectUnitAppender selectUnitAppender,
			CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender) {
		this.tableName = tableName;
		this.selectUnitAppender = selectUnitAppender;
		this.criterionAppender = criterionAppender;
		this.proxyOrderAppender = proxyOrderAppender;
	}
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 条件追加器
	 */
	private SelectUnitAppender selectUnitAppender;
	/**
	 * 条件追加器
	 */
	private CriterionAppender criterionAppender;
	/**
	 * 排序追加器
	 */
	private OrderAppender proxyOrderAppender;
	/**
	 * group by 时间比较紧，后期封装成对像
	 */
	private List<String> groupByList = new ArrayList<String>();
	
	/**
	 * 实现接口
	 */
	@Override
	public String SQL(){
		SQL sqlBuilder = new SQL();
		bulitSelect(selectUnitAppender,sqlBuilder);
		sqlBuilder.FROM(tableName);
		buildCriterions(sqlBuilder);
		buildOrder(sqlBuilder);
		buildGroupBy(sqlBuilder);
		return sqlBuilder.toString();
	}
	
	/**
	 * 
	 * @param sqlBuilder
	 */
	private void buildGroupBy(SQL sqlBuilder) {
		if(!CollectionUtils.isEmpty(groupByList)){
			for(String column : groupByList){
				if(StringUtils.isNotBlank(column)){
					sqlBuilder.GROUP_BY(column.trim());
				}
			}
		}
	}

	/**
	 * 抛出异常影响已有的依赖，先不抛异常体验一下
	 * @param selectUnitAppender
	 * @param sqlBuilder
	 */
	private void bulitSelect(SelectUnitAppender selectUnitAppender,SQL sqlBuilder){
		if(null == selectUnitAppender){
			logger.error("selectUnitAppender is null");
			logger.error("SelectUnitAppender 不能为空");
		}
		List<SelectUnit> selectUnits = selectUnitAppender.getSelectUnits();
		if(CollectionUtils.isEmpty(selectUnits)){
			logger.error("SelectUnitAppender selectUnits is empty");
			logger.error("SelectUnitAppender里selectUnits不能为空");
		}
		for(SelectUnit selectUnit : selectUnits){
			if(null != selectUnit){
				sqlBuilder.SELECT(selectUnit.SQL());
			}
		}
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
		buildOrder(sqlBuilder);
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
				for(Order proxyOrder : proxyOrderList){
					if(null != proxyOrder){
						String orderStr = proxyOrder.getProperty() + " " + proxyOrder.getSortKey();
						sqlBuilder.ORDER_BY(orderStr);
					}
				}
			}
		}
	}
	
	/**
	 * 排序
	 * @param column
	 */
	public void addGroupBy(String column){
		groupByList.add(column);
	}
	
}
