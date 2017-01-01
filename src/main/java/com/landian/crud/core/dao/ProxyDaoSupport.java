package com.landian.crud.core.dao;

import com.landian.commons.page.PageListSupport;
import com.landian.commons.page.PageRequest;
import com.landian.crud.core.builder.SelectBuilder;
import com.landian.crud.core.builder.SqlBuilder;
import com.landian.crud.core.builder.impl.*;
import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.context.SystemContextFactory;
import com.landian.crud.core.context.impl.HashMapResultContext;
import com.landian.crud.core.converter.ResultContextConverter;
import com.landian.crud.core.converter.ResultContextConverterFactory;
import com.landian.crud.core.converter.impl.JavaBeanConverter;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.crud.core.result.SingleValue;
import com.landian.crud.core.result.StatisticMap;
import com.landian.crud.core.result.StatisticMapBuilder;
import com.landian.crud.core.sql.*;
import com.landian.sql.jpa.annotation.IdTypePolicy;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMapConfig;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.criterion.FieldAppender;
import com.landian.sql.jpa.criterion.Restrictions;
import com.landian.sql.jpa.log.JieLoggerProxy;
import com.landian.sql.jpa.order.Order;
import com.landian.sql.jpa.order.OrderAppender;
import com.landian.sql.jpa.sql.SelectUnitAppender;
import com.landian.sql.jpa.sql.SelectUnitRestrictions;
import com.landian.sql.jpa.sql.UpdateUnitAppender;
import com.landian.sql.jpa.utils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * ProxyDaoSupport
 * @author cao.jl
 * to be continute
 * * 毫无疑问，SpringData才是大神版的进化封装
 */
@Repository
public class ProxyDaoSupport<T> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ProxyDaoSupport.class);

	private static final Logger infoSQLLogger = Logger.getLogger("infoSQL");

	@Autowired
	private ProxyDao proxyDao;

	private void proxyInfo(Object object){
		if(null != infoSQLLogger){
			infoSQLLogger.info(object);
		}
	}

	/**
	 * 指定ID业务Bean是否存在
	 * @param beanId
	 * @param beanContext
	 * @return
	 */
	public boolean isExist(long beanId,BeanContext beanContext) {
		String beanIdColumn = this.getBeanIdColumn(beanContext);
		CommonSelectSQLBuilder builder = CommonSelectSQLBuilder.getInstance(beanContext.getTableName(),
				SelectUnitRestrictions.column(beanIdColumn),
				Restrictions.eq(beanIdColumn, beanId),
				Order.asc(beanIdColumn));
		HashMapResultContext hashMapResultContext = this.doFind(builder.SQL());
		if(hashMapResultContext.getResultCount() > 0){
			return true;
		}
		return false;
	}


	public int doInsert(String sql){
		return proxyDao.doInsert(sql);
	}
	
	/**
	 * 插入对像bean 
	 * @param bean
	 * @param beanContext
	 */
	public int insertWithId(Object bean,BeanContext beanContext){
		InsertSQL insertSQL = InsertSQLBuilder.insertWithIdSQL(bean, beanContext);
		return proxyDao.doInsertWidthId(insertSQL);
	}

	/**
	 * 插入对像bean
	 * @param bean
	 * @param beanContext
	 */
	public void insert(Object bean,BeanContext beanContext){
		InsertSQL insertSQL = InsertSQLBuilder.insertSQL(bean, beanContext);
		Object idObject = proxyDao.doInsertAndReturnId(insertSQL);
		//回填ID
		refillId(bean, beanContext, idObject);
	}

	/**
	 * 回填业务Bean ID
	 */
	private void refillId(Object bean, BeanContext beanContext,Object idObject){
		try {
			String idFieldName = beanContext.getIdFieldName();
			String setMethodName = ProviderHelper.toSetMethodName(idFieldName);
			Method idSetMethod = null;
			SingleValue idSingleValue = SingleValue.newInstance(idObject);
			Object fixIdObject = null;
			if(IdTypePolicy.INTEGER == beanContext.getIdType()){
				fixIdObject = idSingleValue.integerValue();
                idSetMethod = bean.getClass().getDeclaredMethod(setMethodName,Integer.class);
            }else if(IdTypePolicy.LONG == beanContext.getIdType()){
				fixIdObject = idSingleValue.longValue();
                idSetMethod = bean.getClass().getDeclaredMethod(setMethodName,Long.class);
            }else if(IdTypePolicy.BIGDECIMAL == beanContext.getIdType()){
				fixIdObject = idSingleValue.bigDecimalValue();
				idSetMethod = bean.getClass().getDeclaredMethod(setMethodName,BigDecimal.class);
			}
			if(null == idSetMethod){
				String msg = MessageFormat.format("ID回填策略未实现，目标对像[{0}]，目标类型[{1}]",bean,idObject);
				logger.warn(msg);
			}else{
				idSetMethod.invoke(bean,new Object[]{fixIdObject});
			}
		}catch (Exception e) {
			String errorMsg = "回填业务Bean ID异常！";
			JieLoggerProxy.error(logger, errorMsg);
			JieLoggerProxy.error(logger, e);
			throw new RuntimeException(errorMsg);
		}
	}
	
	/**
	 * 更新对像非空属性值
	 * @param bean
	 * @param beanContext
	 */
	public int updateNotNull(Object bean, BeanContext beanContext) {
		UpdateSQL updateSQL = UpdateSQLBuilder.updateNotNull(bean, beanContext);
		return proxyDao.doUpdate(updateSQL);
	}
	
	/**
	 * 更新字段
	 * @param updateUnitAppender 更新单元追加器
	 * @param criterionAppender 条件追加器
	 * @param beanContext 
	 * @return
	 * @throws Exception
	 */
	public int update(UpdateUnitAppender updateUnitAppender, CriterionAppender criterionAppender,
			BeanContext beanContext){
		UpdateSQL sql = UpdateSqlBuilder2.build(beanContext, updateUnitAppender, criterionAppender);
		return proxyDao.doUpdate(sql);
	}

	/**
	 * 更新对像非空属性值
	 * @param sql
	 */
	public int doUpdate(String sql) {
		return proxyDao.doUpdate(sql);
	}

	/**
	 * 查询统计
	 * 由于经常需要根据ID(某属性作为key)，统计某属性总计(某属性总计作为Value)
	 * @param sql
	 * @param resultMapConfig
	 * @return
	 */
	public StatisticMap queryAsStatisticMap(String sql,ResultMapConfig resultMapConfig) {
		HashMapResultContext hashMapResultContext = doFind(sql);
		return StatisticMapBuilder.buildStatisticMap(resultMapConfig, hashMapResultContext);
	}

	/**
	 * 根据SQL查询，返回结果集
	 * @param sql
	 */
	public HashMapResultContext doFind(String sql){
		proxyInfo(sql);
		List<Map<String, Object>> resultContext = proxyDao.doFind(sql);
		HashMapResultContext hashMapResultContext = new HashMapResultContext(resultContext);
		return hashMapResultContext;
	}

	/**
	 * 根据SQL查询，返回结果集
	 * @param sql
	 */
	public List doFind(String sql,Class clazz){
		proxyInfo(clazz);
		proxyInfo(sql);
		//转换器
		ResultContextConverter converter = JavaBeanConverter.newInstance(clazz);
		//适配调用
		return this.doFind(sql, converter);
	}

	/**
	 * @param sql
	 * @param converter 结果集转换器
	 */
	public List<T> doFindPage(String sql, int start, int pageSize,  ResultContextConverter converter){
		PageSqlAdapter pageSqlAdapter = SystemContextFactory.getPageSqlAdapter();
		String pageSQL = pageSqlAdapter.wrapSQL(sql,start,pageSize);
		//结果集
		List<Map<String, Object>> resultList = proxyDao.doFind(pageSQL);
		//处理结果集
		List<T> beanList = new ArrayList<T>();
		if(CollectionUtils.isNotEmpty(resultList)){
			for(Map<String, Object> dataMap : resultList){
				@SuppressWarnings("unchecked")
				T bean = (T) converter.convert(dataMap);
				beanList.add(bean);
			}
		}
		return beanList;
	}

	/**
	 * @param sql
	 * @param converter 结果集转换器
	 */
	public List<T> doFind(String sql, ResultContextConverter converter){
		//结果集
		HashMapResultContext hashMapResultContext = this.doFind(sql);
		//处理结果集
		List<T> beanList = new ArrayList<T>();
		List<Map<String, Object>> resultList = hashMapResultContext.getResultObject();
		if(CollectionUtils.isNotEmpty(resultList)){
			for(Map<String, Object> dataMap : resultList){
				@SuppressWarnings("unchecked")
				T bean = (T) converter.convert(dataMap);
				beanList.add(bean);
			}
		}
		return beanList;
	}
	
	/**
	 * 根据ID查询对像
	 * @param beanId
	 * @param beanContext
	 */
	public T queryById(int beanId, BeanContext beanContext){
		Integer id = beanId;
		return this.queryById(id.longValue(), beanContext);
	}
	
	/**
	 * 根据ID查询对像
	 * @param beanId
	 * @param beanContext
	 */
	public T queryById(long beanId,BeanContext beanContext){
		List<Long> ids = new ArrayList<Long>();
		ids.add(beanId);
		List<T> list = this.queryByIds(ids,beanContext);
		if(!CollectionUtils.isEmpty(list)){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据ID列表，查询对像集 
	 * @param beanContext
	 * @param ids
	 */
	public List<T> queryByIds(BeanContext beanContext,List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Collections.EMPTY_LIST;
		}
		return queryByIds(ConvertUtils.Int2long(ids),beanContext);
	}
	
	/**
	 * 根据ID列表，查询对像集 
	 * @param ids
	 * @param beanContext
	 */
	public List<T> queryByIds(List<Long> ids,BeanContext beanContext){
		if(CollectionUtils.isEmpty(ids)){
			return Collections.EMPTY_LIST;
		}
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		String column = beanContext.getIdFieldName();
		criterionAppender.add(Restrictions.in(column, ids,0l));
		List<T> beanList = queryBean(beanContext,criterionAppender);
		return beanList;
	}
	
	/**
	 * 此方法非元数据表，谨慎使用
	 * 查询Bean全部对像
	 * @param beanContext
	 */
	public List<T> queryBeanAll(BeanContext beanContext) {
		return queryBean(beanContext,null,null);
	}
	
	/**
	 * 查询Bean
	 * @param beanContext
	 * @param criterionAppender
	 */
	public List<T> queryBean(BeanContext beanContext,CriterionAppender criterionAppender) {
		return queryBean(beanContext,criterionAppender,null);
	}
	
	/**
	 * 查询Bean
	 * @param beanContext
	 * @param proxyOrderAppender
	 */
	public List<T> queryBean(BeanContext beanContext,OrderAppender proxyOrderAppender) {
		return queryBean(beanContext,null,proxyOrderAppender);
	}
	
	/**
	 * 查询bean
	 * @param beanContext
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 */
	public List<T> queryBean(BeanContext beanContext,CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender) {
		String tableName = beanContext.getTableName();
		//选择器
		Class<T> beanClass = beanContext.getBeanClass();
		SelectBuilder selectBuilder = SelectBuilderFactory.builder(beanClass);
		SqlBuilder sqlBuilder = SqlBuilderFactory.builder(tableName, beanClass, selectBuilder, criterionAppender, proxyOrderAppender);
		//转换器
		ResultContextConverter resultContextConverter = ResultContextConverterFactory.build(beanContext.getBeanClass());
		//数据集
		List<T> beanList = queryBean(sqlBuilder,resultContextConverter);
		return beanList;
	}

	/**
	 * 查询bean
	 * @param beanContext
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 */
	public List<T> queryBeanField(BeanContext beanContext,FieldAppender fieldAppender, CriterionAppender criterionAppender,
			OrderAppender proxyOrderAppender) {
		String tableName = beanContext.getTableName();
		//选择器
		SelectBuilder selectBuilder = new FieldAppenderSelectBuilder(fieldAppender,beanContext.getBeanClass());
		SqlBuilder sqlBuilder = SqlBuilderFactory.builder(tableName, beanContext.getBeanClass(), selectBuilder, criterionAppender, proxyOrderAppender);
		//转换器
		ResultContextConverter resultContextConverter = ResultContextConverterFactory.build(beanContext.getBeanClass());
		//数据集
		List<T> beanList = queryBean(sqlBuilder, resultContextConverter);
		return beanList;
	}

	/**
	 * 查询对像信息
	 * @param tableName
	 * @param clazz
	 * @param selectUnitAppender
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public HashMapResultContext queryBeanInfo(String tableName, Class clazz,SelectUnitAppender selectUnitAppender,
			CriterionAppender criterionAppender, OrderAppender proxyOrderAppender) {
		//选择器
		SelectBuilder selectBuilder = SelectBuilderFactory.builder(selectUnitAppender);
		//SQL构建器
		SqlBuilder sqlBuilder = SqlBuilderFactory.builder(tableName, clazz, selectBuilder, criterionAppender, proxyOrderAppender);
		//结果集
		String sql = sqlBuilder.SQL();
		HashMapResultContext hashMapResultContext = this.doFind(sql);
		return hashMapResultContext;
	}

	/**
	 * 决定不重载此方法基于以下理由
	 * 1.分页查询大部份情况需要追加条件和排序
	 * 2.参数已经没有更好重构感觉，个人感觉不能再少了，
	 *   重载会令方法组数量变多
	 * 3.criterionAppender proxyOrderAppender 入参可为null，
	 *   里面的SqlBuilder已作了相应处理,
	 * @param beanContext
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @param pageRequest
	 * @return
	 */
	public PageListSupport<T> queryBeanPage(BeanContext beanContext,
			CriterionAppender criterionAppender,OrderAppender proxyOrderAppender,PageRequest pageRequest) {
		String tableName = beanContext.getTableName();
		//选择器
		SelectBuilder selectBuilder = SelectBuilderFactory.builder(beanContext.getBeanClass());
		//加强条件追加器
		if(null == criterionAppender){
			criterionAppender = CriterionAppender.newInstance();
		}
		//SQL建造器
		SqlBuilder sqlBuilder = SqlBuilderFactory.builder(tableName, beanContext.getBeanClass(), selectBuilder, criterionAppender, proxyOrderAppender);
		//转换器
		ResultContextConverter resultContextConverter = ResultContextConverterFactory.build(beanContext.getBeanClass());
		//分页查询
		int start = getPageStart(pageRequest);
		int size = pageRequest.getPageSize();
		List<T> beanList = doFindPage(sqlBuilder.SQL(), start, size, resultContextConverter);
		//封装PageListSupport
		PageListSupport<T> pageListSupport = new PageListSupport<T>();
		//总数查询
		Long count = 0l;
		if(!CollectionUtils.isEmpty(beanList)){
			HashMapResultContext hashMapResultContext = this.doFind(sqlBuilder.SQLCount());
			Object countObj = hashMapResultContext.singleResult();
			if(null != countObj){
				SingleValue singleValue = SingleValue.newInstance(countObj);
				count = singleValue.longValue();
			}
		}
		pageListSupport.setList(beanList);
		pageListSupport.setCount(count);
		pageListSupport.setPageIndex(pageRequest.getPageIndex());
		pageListSupport.setPageSize(pageRequest.getPageSize());
		return pageListSupport;
	}

	/**
	 * 查询Bean
	 * @param sqlBuilder
	 * @param resultContextConverter
	 */
	public List<T> queryBean(SqlBuilder sqlBuilder,ResultContextConverter resultContextConverter) {
		return doFind(sqlBuilder.SQL(), resultContextConverter);
	}
	
	/**
	 * 得到业务Bean的id字段
	 * @param beanContext
	 * @return
	 */
	public String getBeanIdColumn(BeanContext beanContext){
		String idFieldName = beanContext.getIdFieldName();
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(beanContext.getBeanClass());
		String columnName = resultMappingMap.get(idFieldName).getColumn();
		return columnName;
	}
	
	/**
	 * author jie
	 * date 15/08/21
	 * 根据业务BeanID删除业务Bean
	 * @param beanId
	 * @param beanContext
	 * @return
	 */
	public int deleteById(long beanId, BeanContext beanContext) {
		Criterion criterion = buildIdCriterion(beanId, beanContext);
		String sql = DeleteSQLBuilder.buildDeleteSQL(beanContext.getTableName(), criterion);
		return doDelete(sql);
	}

	/**
	 * 构建ID条件
	 * @param id
	 * @param beanContext
	 * @return
	 */
	public Criterion buildIdCriterion(long id, BeanContext beanContext){
		String beanIdColumn = getBeanIdColumn(beanContext);
		return Restrictions.eq(beanIdColumn,id);
	}

	/**
	 * 构建ID条件
	 * @param ids
	 * @param beanContext
	 * @return
	 */
	public Criterion buildIdCriterion(List<Long> ids, BeanContext beanContext){
		String beanIdColumn = getBeanIdColumn(beanContext);
		return Restrictions.in(beanIdColumn,ids,0l);
	}

	/**
	 * 根据ID批量删除
	 * @param ids
	 * @param beanContext
	 * @return
	 */
	public int deleteByIdLong(List<Long> ids,BeanContext beanContext) {
		if(CollectionUtils.isEmpty(ids)){
			return 0;
		}
		Criterion criterion = buildIdCriterion(ids, beanContext);
		String sql = DeleteSQLBuilder.buildDeleteSQL(beanContext.getTableName(), criterion);
		return doDelete(sql);
	}

	public int doDelete(String sql){
		return proxyDao.doDelete(sql);
	}

	/**
	 * 得到构建的查询SQL
	 * @param beanContext
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public String getQuerySQL(BeanContext beanContext, CriterionAppender criterionAppender, OrderAppender proxyOrderAppender) {
		//选择器
		SelectBuilder selectBuilder = SelectBuilderFactory.builder(beanContext.getBeanClass());
		//SQL建造器
		String tableName = beanContext.getTableName();
		SqlBuilder sqlBuilder = SqlBuilderFactory.builder(tableName, beanContext.getBeanClass(), selectBuilder, criterionAppender, proxyOrderAppender);
		return sqlBuilder.SQL();
	}

	private int getPageStart(PageRequest pageRequest) {
		return pageRequest.getPageIndex() * pageRequest.getPageSize(); //起始分页位置
	}

	/**
	 * 根据SQL查询，返回结果集第一个对像
	 * @param sql
	 */
	public Object queryAsValueFirst(String sql,Class clazz){
		List list = this.doFind(sql, clazz);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据SQL查询，返回单值列表结果
	 * @param sql
	 */
	public List queryAsValueList(String sql){
		List list = Collections.EMPTY_LIST;
		HashMapResultContext hashMapResultContext = doFind(sql);
		if(hashMapResultContext.getResultCount() > 0){
			list = hashMapResultContext.singleList();
		}
		return list;
	}

	/**
	 * 查询为SingleValue
	 * @param sql
	 * @return
	 */
	public SingleValue queryAsSingleValue(String sql) {
		HashMapResultContext hashMapResultContext = this.doFind(sql);
		Object object = hashMapResultContext.singleResult();
		SingleValue singleValue = SingleValue.newInstance(object);
		return singleValue;
	}

	/**
	 * 查询为SingleValueInt
	 * @param sql
	 * @return
	 */
	public int queryAsSingleValueInt(String sql) {
		SingleValue singleValue = queryAsSingleValue(sql);
		return singleValue.integerValue();
	}

	/**
	 * 查询为SingleValueLong
	 * @param sql
	 * @return
	 */
	public long queryAsSingleValueLong(String sql) {
		SingleValue singleValue = queryAsSingleValue(sql);
		return singleValue.longValue();
	}

	/**
	 * 查询为Long值列表
	 * @param sql
	 * @return
	 */
	public List<Long> queryAsLongValue(String sql) {
		return proxyDao.queryAsLongValue(sql);
	}

	/**
	 * 查询为Long值列表
	 * @param sql
	 * @param start 开始位置
	 * @param size 查询大小
	 * @return
	 */
	public List<Long> queryAsLongValue(String sql, int start, int size) {
		PageSqlAdapter pageSqlAdapter = SystemContextFactory.getPageSqlAdapter();
		String sqlQ = pageSqlAdapter.wrapSQL(sql, start, size);
		return proxyDao.queryAsLongValue(sqlQ);
	}

	/**
	 * 查询为Integer值列表
	 * @param sql
	 * @return
	 */
	public List<Integer> queryAsIntValue(String sql) {
		return proxyDao.queryAsIntValue(sql);
	}

	/**
	 * 查询为Integer值列表
	 * @param sql
	 * @param start 开始位置
	 * @param size 查询大小
	 * @return
	 */
	public List<Integer> queryAsIntValue(String sql, int start, int size) {
		PageSqlAdapter pageSqlAdapter = SystemContextFactory.getPageSqlAdapter();
		String sqlQ = pageSqlAdapter.wrapSQL(sql, start, size);
		return proxyDao.queryAsIntValue(sqlQ);
	}

}
