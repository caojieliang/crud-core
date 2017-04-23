package com.landian.crud.core.service;

import com.landian.commons.page.PageListSupport;
import com.landian.commons.page.PageRequest;
import com.landian.crud.core.context.impl.HashMapResultContext;
import com.landian.crud.core.dao.ProxyDaoSupport;
import com.landian.crud.core.result.SingleValue;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.criterion.*;
import com.landian.sql.jpa.order.Order;
import com.landian.sql.jpa.order.OrderAppender;
import com.landian.sql.jpa.order.OrderVo;
import com.landian.sql.jpa.sql.SelectUnit;
import com.landian.sql.jpa.sql.SelectUnitAppender;
import com.landian.sql.jpa.sql.SelectUnitRestrictions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author jie
 * 抽象业务BeanService
 * @param <T> 业务Bean
 * date 15/07/27
 * 毫无疑问，SpringData才是大神版的进化封装
 */
public abstract class AbstractQueryService<T>{

	private static final Logger logger = Logger.getLogger(AbstractQueryService.class);

	@Autowired
	private ProxyDaoSupport<T> proxyDaoSupport;

	private Class beanClass = null;

	/**
	 * 需要实现获取代理ProxyDaoSupport抽象接口 
	 */
	protected ProxyDaoSupport<T> getProxyDaoSupport(){
		return proxyDaoSupport;
	}

	/**
	 * 需要实现获取BeanContext
	 */
	public abstract BeanContext getBeanContext();

	/**
	 * 尚未对接使用
	 * 得到业务Beaan Class
	 * @return
	 */
	private Class getBeanClass(){
		if(null != beanClass){
			return beanClass;
		}
		Type sType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
		Class<T> mTClass = (Class<T>) (generics[0]);
		beanClass = mTClass;
		return mTClass;
	}

	/**
	 * 业务Bean是否存在
	 * @return
	 */
	public boolean isExist(int beanId){
		Integer id = beanId;
		return isExist(id.longValue());
	}

	/**
	 * 业务Bean是否存在
	 * @return
	 */
	public boolean isExist(long beanId) {
		BeanContext beanContext = this.getBeanContext();
		return getProxyDaoSupport().isExist(beanId, beanContext);
	}

	/**
	 * 业务Bean是否存在
	 * @return
	 */
	public boolean isExist(String beanId) {
		BeanContext beanContext = this.getBeanContext();
		return getProxyDaoSupport().isExist(beanId, beanContext);
	}

	/**
	 * 根据业务Bean Id查询业务Bean
	 */
	public T queryById(int beanId){
		T bean = (T) getProxyDaoSupport().queryById(beanId, getBeanContext());
		return bean;
	}

	/**
	 * 根据业务Bean Id查询业务Bean 
	 */
	public T queryById(long beanId){
		T bean = (T) getProxyDaoSupport().queryById(beanId, getBeanContext());
		return bean;
	}

	/**
	 * 根据业务Bean Id查询业务Bean
	 */
	public T queryById(String beanId){
		if(StringUtils.isBlank(beanId)){
			return null;
		}
		String column = getBeanContext().getIdFieldName();
		List<T> list = this.queryBy(column, beanId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * PS:为了兼容ID类型为BigDecimal的情况，注意此处当作整形来用
	 */
	public T queryById(BigDecimal beanId){
		if(null == beanId){
			return null;
		}
		T bean = (T) getProxyDaoSupport().queryById(beanId.longValue(), getBeanContext());
		return bean;
	}

	/**
	 * 根据业务Bean Ids查询业务Bean列表(默认Integer)
	 */
	public List<T> queryByIds(List<Integer> beanIds){
		if(CollectionUtils.isEmpty(beanIds)){
			return Collections.EMPTY_LIST;
		}
		List<T> list = getProxyDaoSupport().queryByIds(getBeanContext(), beanIds);
		return list;
	}

	/**
	 * 根据业务Bean Ids查询业务Bean列表(备选Long)
	 */
	public List<T> queryByIdsLong(List<Long> beanIds){
		if(CollectionUtils.isEmpty(beanIds)){
			return Collections.EMPTY_LIST;
		}
		List<T> list = null;
		try {
			list = getProxyDaoSupport().queryByIds(beanIds,getBeanContext());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 根据业务Bean Ids查询业务Bean列表
	 */
	public List<T> queryByIdsString(List<String> beanIds){
		if(CollectionUtils.isEmpty(beanIds)){
			return Collections.EMPTY_LIST;
		}
		List<T> list = null;
		try {
			list = getProxyDaoSupport().queryByIdsString(beanIds,getBeanContext());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * PS:为了兼容ID类型为BigDecimal的情况，注意此处当作整形来用
	 */
	public List<T> queryByIdsBigDecimal(List<BigDecimal> beanIds){
		if(CollectionUtils.isEmpty(beanIds)){
			return Collections.EMPTY_LIST;
		}
		List<Long> targetIds = new ArrayList<Long>();
		for (BigDecimal beanId : beanIds) {
			targetIds.add(beanId.longValue());
		}
		List<T> list = queryByIdsLong(targetIds);
		return list;
	}

	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, int value){
		return this.queryBean(Restrictions.eq(field, value));
	}

	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, long value){
		return this.queryBean(Restrictions.eq(field, value));
	}

	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, String value){
		return this.queryBean(Restrictions.eq(field, value));
	}


	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, int value, Order... proxyOrders){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return this.queryBean(Restrictions.eq(field, value), proxyOrderAppender);
	}

	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, long value, Order... proxyOrders){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return this.queryBean(Restrictions.eq(field, value), proxyOrderAppender);
	}

	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, String value, Order... proxyOrders){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return this.queryBean(Restrictions.eq(field, value), proxyOrderAppender);
	}


	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, Collection values){
		if(CollectionUtils.isEmpty(values)){
			return Collections.EMPTY_LIST;
		}
		return this.queryBean(Restrictions.in(field, values));
	}


	/**
	 * 根据业务Bean 属性值查询业务Bean
	 */
	public List<T> queryBy(String field, Collection values, Order... proxyOrders){
		if(CollectionUtils.isEmpty(values)){
			return Collections.EMPTY_LIST;
		}
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return this.queryBean(Restrictions.in(field, values), proxyOrderAppender);
	}

	/**
	 * 查询业务Bean全部对像
	 * 此方法非元数据表，谨慎使用
	 */
	public List<T> queryBeanAll(){
		List<T> list = getProxyDaoSupport().queryBeanAll(getBeanContext());
		return list;
	}

	/**
	 * 查询业务Bean全部对像
	 * 此方法非元数据表，谨慎使用
	 */
	public List<T> queryBeanAll(Order... orders){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(orders);
		return getProxyDaoSupport().queryBean(getBeanContext(),null,proxyOrderAppender);
	}

	/**
	 * 查询业务bean
	 * @param criterion 查询条件
	 */
	public List<T> queryBean(Criterion criterion){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		criterionAppender.add(criterion);
		List<T> list = queryBean(criterionAppender);
		return list;
	}

	/**
	 * 查询业务bean
	 * @param criterion 查询条件
	 */
	public List<T> queryBean(Criterion criterion, Order... orders){
		OrderAppender orderAppender = OrderAppender.newInstance().add(orders);
		return this.queryBean(criterion,orderAppender);
	}

	/**
	 * 查询业务bean
	 * @param criterions 查询条件
	 */
	public List<T> queryBean(Criterion... criterions){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterions);
		return queryBean(criterionAppender);
	}

	/**
	 * 查询业务bean
	 * @param order
	 * @param criterions 查询条件
	 */
	public List<T> queryBean(Order order, Criterion... criterions){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterions);
		return queryBean(criterionAppender, OrderAppender.newInstance().add(order));
	}

	/**
	 * 查询业务bean
	 * @param criterion 查询条件
	 */
	public List<T> queryBean(Criterion criterion,Order proxyOrder){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		proxyOrderAppender.add(proxyOrder);
		return queryBean(criterion, proxyOrderAppender);
	}

	/**
	 * 查询业务bean
	 * @param criterion 查询条件
	 */
	public List<T> queryBean(Criterion criterion, OrderAppender proxyOrderAppender){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		criterionAppender.add(criterion);
		return queryBean(criterionAppender,proxyOrderAppender);
	}

	/**
	 * 查询业务bean
	 * @param criterionAppender 条件追加器
	 */
	public List<T> queryBean(CriterionAppender criterionAppender){
		List<T> list = queryBean(criterionAppender, OrderAppender.newInstance());
		return list;
	}

	/**
	 * 查询业务bean
	 * @param criterionAppender 条件追加器
	 */
	public List<T> queryBean(CriterionAppender criterionAppender,Order proxyOrder){
		List<T> list = queryBean(criterionAppender, OrderAppender.newInstance().add(proxyOrder));
		return list;
	}

	/**
	 * 查询业务bean
	 * @param criterionAppender 条件追加器
	 * @param proxyOrderAppender 排序追加器
	 */
	public List<T> queryBean(CriterionAppender criterionAppender,OrderAppender proxyOrderAppender){
		List<T> list = getProxyDaoSupport().queryBean(getBeanContext(), criterionAppender, proxyOrderAppender);
		return list;
	}

	/**
	 * 根据业务Bean 分页对像
	 * @param criterionAppender 条件追加器
	 * @param pageRequest 分页信息
	 * @return
	 */
	public PageListSupport<T> queryBean(CriterionAppender criterionAppender, PageRequest pageRequest){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		return getProxyDaoSupport().queryBeanPage(getBeanContext(), criterionAppender, proxyOrderAppender, pageRequest);
	}

	/**
	 * 根据业务Bean 分页对像
	 * @param criterionAppender 条件追加器
	 * @param proxyOrder 排序追加器
	 * @param pageRequest 分页信息
	 * @return
	 */
	public PageListSupport<T> queryBean(CriterionAppender criterionAppender,Order proxyOrder,PageRequest pageRequest){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		if(null != proxyOrder){
			proxyOrderAppender.add(proxyOrder);
		}
		return getProxyDaoSupport().queryBeanPage(getBeanContext(), criterionAppender, proxyOrderAppender, pageRequest);
	}

	/**
	 * 根据业务Bean 分页对像
	 * @param criterionAppender 条件追加器
	 * @param proxyOrders 排序
	 * @param pageRequest 分页信息
	 * @return
	 */
	public PageListSupport<T> queryBean(CriterionAppender criterionAppender,PageRequest pageRequest,Order... proxyOrders){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return queryBean(criterionAppender, proxyOrderAppender, pageRequest);
	}

	/**
	 * 根据业务Bean 分页对像
	 * @param criterionAppender 条件追加器
	 * @param pageRequest 分页信息
	 * @param orderVos 排序对象
	 * @return
	 */
	public PageListSupport<T> queryBean(CriterionAppender criterionAppender,PageRequest pageRequest,OrderVo... orderVos){
		Order[] orders = Order.asArray(orderVos);
		OrderAppender orderAppender = OrderAppender.newInstance().add(orders);
		return this.queryBean(criterionAppender, orderAppender, pageRequest);
	}

	/**
	 * 根据业务Bean 分页对像
	 * @param criterionAppender 条件追加器
	 * @param proxyOrderAppender 排序追加器
	 * @param pageRequest 分页信息
	 * @return
	 */
	public PageListSupport<T> queryBean(CriterionAppender criterionAppender,OrderAppender proxyOrderAppender,PageRequest pageRequest){
		return getProxyDaoSupport().queryBeanPage(getBeanContext(), criterionAppender, proxyOrderAppender, pageRequest);
	}

	/**
	 * 查询字段属性值
	 * @param fieldName
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public List<T> queryBean(String fieldName, CriterionAppender criterionAppender,OrderAppender proxyOrderAppender){
		FieldAppender fieldAppender = FieldAppender.newInstance();
		fieldAppender.addField(Field.newInstance(fieldName));
		return queryBean(fieldAppender, criterionAppender, proxyOrderAppender);
	}

	/**
	 * 查询字段属性值
	 * @param fieldName
	 * @param criterion
	 * @return
	 */
	public List<T> queryBean(String fieldName, Criterion criterion){
		FieldAppender fieldAppender = FieldAppender.newInstance().addField(Field.newInstance(fieldName));
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		return queryBean(fieldAppender, criterionAppender, proxyOrderAppender);
	}

	/**
	 * 查询字段属性值
	 * @param fieldName
	 * @param criterion
	 * @param proxyOrder
	 * @return
	 */
	public List<T> queryBean(String fieldName, Criterion criterion, Order proxyOrder){
		FieldAppender fieldAppender = FieldAppender.newInstance().addField(Field.newInstance(fieldName));
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrder);
		return queryBean(fieldAppender, criterionAppender, proxyOrderAppender);
	}

	/**
	 *
	 * @param fieldName
	 * @param criterionAppender
	 * @param proxyOrder
	 * @return
	 */
	public List<T> queryBean(String fieldName, CriterionAppender criterionAppender,Order proxyOrder){
		FieldAppender fieldAppender = FieldAppender.newInstance().addField(Field.newInstance(fieldName));
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrder);
		List<T> list = getProxyDaoSupport().queryBeanField(getBeanContext(), fieldAppender, criterionAppender, proxyOrderAppender);
		return list;
	}

	/**
	 * @param fieldAppender
	 * @param criterion
	 * @return
	 */
	public List<T> queryBean(FieldAppender fieldAppender, Criterion criterion){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		List<T> list = getProxyDaoSupport().queryBeanField(getBeanContext(), fieldAppender, criterionAppender, proxyOrderAppender);
		return list;
	}

	/**
	 *
	 * @param fieldAppender
	 * @param criterion
	 * @param proxyOrder
	 * @return
	 */
	public List<T> queryBean(FieldAppender fieldAppender, Criterion criterion, Order proxyOrder){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrder);
		List<T> list = getProxyDaoSupport().queryBeanField(getBeanContext(), fieldAppender, criterionAppender, proxyOrderAppender);
		return list;
	}

	/**
	 *
	 * @param fieldAppender
	 * @param criterionAppender
	 * @param proxyOrders
	 * @return
	 */
	public List<T> queryBean(FieldAppender fieldAppender, CriterionAppender criterionAppender,Order... proxyOrders) {
		OrderAppender proxyOrderAppender = OrderAppender.newInstance().add(proxyOrders);
		return queryBean(fieldAppender, criterionAppender,proxyOrderAppender);
	}

	/**
	 *
	 * @param fieldAppender
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public List<T> queryBean(FieldAppender fieldAppender, CriterionAppender criterionAppender,OrderAppender proxyOrderAppender) {
		List<T> list = getProxyDaoSupport().queryBeanField(getBeanContext(), fieldAppender, criterionAppender, proxyOrderAppender);
		return list;
	}

	/**
	 * 查询对像信息
	 * @param selectUnit
	 * @param criterionAppender
	 * @return
	 */
	public HashMapResultContext queryBeanInfo(SelectUnit selectUnit, CriterionAppender criterionAppender){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance().select(selectUnit);
		return queryBeanInfo(selectUnitAppender, criterionAppender);
	}

	/**
	 * 查询对像信息
	 * @param selectUnitAppender
	 * @param criterionAppender
	 * @return
	 */
	public HashMapResultContext queryBeanInfo(SelectUnitAppender selectUnitAppender, CriterionAppender criterionAppender){
		OrderAppender proxyOrderAppender = OrderAppender.newInstance();
		return queryBeanInfo(selectUnitAppender, criterionAppender, proxyOrderAppender);
	}

	/**
	 * 查询对像信息
	 * @param selectUnitAppender
	 * @param criterionAppender
	 * @param proxyOrderAppender
	 * @return
	 */
	public HashMapResultContext queryBeanInfo(SelectUnitAppender selectUnitAppender,
		CriterionAppender criterionAppender,OrderAppender proxyOrderAppender){
		BeanContext beanContext = getBeanContext();
		return getProxyDaoSupport().queryBeanInfo(beanContext.getTableName(), beanContext.getBeanClass(),
				selectUnitAppender, criterionAppender, proxyOrderAppender);
	}

	/**
	 * 查询对像单个值
	 * @param fieldName 业务Bean属性值
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(String fieldName, long id){
		return querySingleValue(SelectUnitRestrictions.column(fieldName), id);
	}

	/**
	 * 查询对像单个值
	 * @param fieldName 业务Bean属性值
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(String fieldName, int id){
		Integer value = id;
		return querySingleValue(SelectUnitRestrictions.column(fieldName), value.longValue());
	}

	/**
	 * 查询对像单个值
	 * @param fieldName 业务Bean属性值
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(String fieldName, String id){
		return querySingleValue(SelectUnitRestrictions.column(fieldName), id);
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, int id){
		Integer value = id;
		return querySingleValue(selectUnit, value.longValue());
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, long id){
		String idFieldName = getBeanContext().getIdFieldName();
		Criterion eq = Restrictions.eq(idFieldName, id);
		return querySingleValue(selectUnit, eq);
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param id 业务BeanID
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, String id){
		Criterion eq = buildIdCriterion(id);
		return querySingleValue(selectUnit, eq);
	}

	/**
	 * 构建ID条件
	 */
	private Criterion buildIdCriterion(String id) {
		return getProxyDaoSupport().buildIdCriterion(id,getBeanContext());
	}

	/**
	 * 构建ID条件
	 */
	private Criterion buildIdCriterion(long id) {
		return getProxyDaoSupport().buildIdCriterion(id,getBeanContext());
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param criterion
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, Criterion criterion){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		return querySingleValue(selectUnit, criterionAppender);
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param criterionArr
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, Criterion... criterionArr){
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterionArr);
		return querySingleValue(selectUnit, criterionAppender);
	}

	/**
	 * 查询对像单个值
	 * @param selectUnit
	 * @param criterionAppender
	 * @return
	 */
	public SingleValue querySingleValue(SelectUnit selectUnit, CriterionAppender criterionAppender){
		SelectUnitAppender selectUnitAppender = SelectUnitAppender.newInstance().select(selectUnit);
		HashMapResultContext hashMapResultContext = queryBeanInfo(selectUnitAppender, criterionAppender);
		Object value = hashMapResultContext.singleResult();
		return SingleValue.newInstance(value);
	}

	/**
	 * 查询对像单个值
	 * @param sql
	 * @return
	 */
	public SingleValue querySingleValue(String sql){
		HashMapResultContext hashMapResultContext = this.getProxyDaoSupport().doFind(sql);
		Object value = hashMapResultContext.singleResult();
		return SingleValue.newInstance(value);
	}

	/**
	 * 查询为Long值列表
	 * @param sql
	 * @return
	 */
	protected List<Long> queryAsLongValue(String sql) {
		return proxyDaoSupport.queryAsLongValue(sql);
	}

	/**
	 * 查询为Long值列表
	 * @param sql
	 * @param start 开始位置
	 * @param size 查询大小
	 * @return
	 */
	protected List<Long> queryAsLongValue(String sql, int start, int size) {
		return proxyDaoSupport.queryAsLongValue(sql, start, size);
	}

	/**
	 * 查询为Integer值列表
	 * @param sql
	 * @return
	 */
	protected List<Integer> queryAsIntValue(String sql) {
		return proxyDaoSupport.queryAsIntValue(sql);
	}

	/**
	 * 查询为Integer值列表
	 * @param sql
	 * @param start 开始位置
	 * @param size 查询大小
	 * @return
	 */
	protected List<Integer> queryAsIntValue(String sql, int start, int size) {
		return proxyDaoSupport.queryAsIntValue(sql, start, size);
	}

	public String getQuerySQL(CriterionAppender criterionAppender,Order... orders){
		OrderAppender orderAppender = OrderAppender.newInstance().add(orders);
		return getQuerySQL(criterionAppender, orderAppender);
	}

	public String getQuerySQL(CriterionAppender criterionAppender,OrderAppender proxyOrderAppender){
		return getProxyDaoSupport().getQuerySQL(getBeanContext(), criterionAppender, proxyOrderAppender);
	}


}
