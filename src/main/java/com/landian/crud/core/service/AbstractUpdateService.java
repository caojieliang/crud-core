package com.landian.crud.core.service;

import com.landian.crud.core.dao.ProxyDaoSupport;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.criterion.Restrictions;
import com.landian.sql.jpa.sql.Update;
import com.landian.sql.jpa.sql.UpdateUnit;
import com.landian.sql.jpa.sql.UpdateUnitAppender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jie
 * 抽象业务BeanService
 * @param <T> 业务Bean
 * date 15/07/27
 * 毫无疑问，SpringData才是大神版的进化封装
 */
public abstract class AbstractUpdateService<T>{

	private static final Logger logger = Logger.getLogger(AbstractUpdateService.class);

	@Autowired
	private ProxyDaoSupport<T> proxyDaoSupport;

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
	 * 保存业务Bean(含ID)
	 * @param bean 业务Bean
	 */
	public T saveWithId(T bean){
		getProxyDaoSupport().insertWithId(bean, getBeanContext());
		return bean;
	}

	/**
	 * 保存业务Bean
	 * @param bean 业务Bean
	 */
	public T save(T bean){
		getProxyDaoSupport().insert(bean, getBeanContext());
		return bean;
	}

	/**
	 * 保存业务Bean
	 * @param beanList 业务Bean
	 * 批量插入，涉及效率问题，具体业务视情况使用
	 * 后期有待优化为build批量插入数据的SQL
	 */
	public void save(List<T> beanList){
		if(CollectionUtils.isNotEmpty(beanList)){
			for(T bean : beanList){
				this.save(bean);
			}
		}
	}

	/**
	 * 批量保存
	 * @url http://www.iteye.com/topic/1135650 例子实现
	 * @param beanList
	 */
	private void saveBatch(List<T> beanList){
		proxyDaoSupport.insertBatch(beanList,getBeanContext());
	}

	/**
	 * 保存业务Bean
	 * @param beanList 业务Bean
	 * 批量插入，涉及效率问题，目前尚未有时间实现
	 */
	public void save4Batch(List<T> beanList){
		logger.warn("method save4Batch is to be impl");
	}

	/**
	 * 根据业务ID删除对像
	 * @param beanId
	 * date 15/08/21
	 */
	protected int deleteById(int beanId){
		Integer wrapInt = beanId;
		return this.deleteById(wrapInt.longValue());
	}

	/**
	 * 根据业务ID删除对像
	 * @param beanId
	 * date 15/08/21
	 */
	protected int deleteById(long beanId){
		return getProxyDaoSupport().deleteById(beanId, getBeanContext());
	}

	/**
	 * 根据业务ID删除对像
	 * @param beanId
	 * date 15/08/21
	 */
	protected int deleteById(String beanId){
		return getProxyDaoSupport().deleteById(beanId, getBeanContext());
	}

	/**
	 * 根据业务ID删除对像
	 * @param ids
	 */
	protected int deleteById(List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return 0;
		}
		List<Long> idsLong = new ArrayList<Long>();
		for (Integer id : ids) {
			if(null != id){
				idsLong.add(id.longValue());
			}
		}
		return deleteByIdLong(idsLong);
	}

	protected int deleteByIdLong(List<Long> ids) {
		return proxyDaoSupport.deleteByIdLong(ids,getBeanContext());
	}

	public int doDelete(String sql){
		return proxyDaoSupport.doDelete(sql);
	}

	/**
	 * 更新业务Bean非空属性
	 * @param bean 业务Bean
	 */
	public int update(T bean) {
		return getProxyDaoSupport().updateNotNull(bean, getBeanContext());
	}

	/**
	 * 根据条件更新业务bean字段
	 * @param updateUnits
	 * @param criterion
	 */
	protected int update(Criterion criterion, UpdateUnit... updateUnits) {
		UpdateUnitAppender updateUnitAppender = UpdateUnitAppender.newInstance().add(updateUnits);
		return this.update(criterion,updateUnitAppender);
	}

	/**
	 * 根据条件更新业务bean字段
	 * @param updateUnitAppender
	 * @param criterion
	 */
	protected int update(Criterion criterion, UpdateUnitAppender updateUnitAppender) {
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		return getProxyDaoSupport().update(updateUnitAppender, criterionAppender, getBeanContext());
	}

	/**
	 * 根据条件更新业务bean字段
	 * @param updateUnit
	 * @param criterionList
	 */
	protected int update(UpdateUnit updateUnit, Criterion... criterionList) {
		UpdateUnitAppender updateUnitAppender = UpdateUnitAppender.newInstance().add(updateUnit);
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterionList);
		return getProxyDaoSupport().update(updateUnitAppender, criterionAppender, getBeanContext());
	}

	/**
	 * 根据条件更新业务bean字段
	 * @param updateUnitAppender
	 * @param criterion
	 */
	protected int update(UpdateUnitAppender updateUnitAppender,Criterion criterion) {
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		return getProxyDaoSupport().update(updateUnitAppender, criterionAppender, getBeanContext());
	}

	/**
	 * 根据条件更新业务bean字段
	 * @param updateUnitAppender 更新单元追加器
	 * @param criterionAppender 条件追加器
	 * @return
	 */
	protected int update(UpdateUnitAppender updateUnitAppender,CriterionAppender criterionAppender){
		return getProxyDaoSupport().update(updateUnitAppender, criterionAppender, getBeanContext());
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, int fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, String fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, Date fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, float fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, BigDecimal fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(int beanId, String fieldName, long fieldValue){
		Integer id = beanId;
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(id.longValue(),updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(long beanId, String fieldName, int fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId,updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(String beanId, String fieldName, int fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId,updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 */
	protected int update(long beanId, String fieldName, String fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId,updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 */
	protected int update(String beanId, String fieldName, String fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId,updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(long beanId, String fieldName, Date fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId, updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(long beanId, String fieldName, float fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId, updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(long beanId, String fieldName, BigDecimal fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId, updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param fieldName 业务Bean的属性
	 * @param fieldValue 更新值
	 * @return
	 */
	protected int update(long beanId, String fieldName, long fieldValue){
		UpdateUnit updateUnit = Update.set(fieldName, fieldValue);
		return this.update(beanId, updateUnit);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param updateUnits 更新单元
	 * @return
	 */
	protected int update(int beanId, UpdateUnit... updateUnits){
		Integer beanIdVal = beanId;
		return update(beanIdVal.longValue(),updateUnits);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param updateUnits 更新单元
	 * @return
	 */
	protected int update(String beanId, UpdateUnit... updateUnits){
		Criterion eq = getProxyDaoSupport().buildIdCriterion(beanId,getBeanContext());
		return this.update(eq,updateUnits);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param updateUnitAppender 更新单元容器
	 * @return
	 */
	protected int update(String beanId, UpdateUnitAppender updateUnitAppender){
		Criterion eq = getProxyDaoSupport().buildIdCriterion(beanId,getBeanContext());
		return this.update(eq,updateUnitAppender);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param updateUnits 更新单元
	 * @return
	 */
	protected int update(long beanId, UpdateUnit... updateUnits){
		Criterion eq = getProxyDaoSupport().buildIdCriterion(beanId,getBeanContext());
		return this.update(eq,updateUnits);
	}

	/**
	 * 根据Id更新业务Bean字段值
	 * @param beanId 业务BeanId
	 * @param updateUnitAppender 更新单元
	 * @return
	 */
	protected int update(long beanId, UpdateUnitAppender updateUnitAppender){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		String idColumn = getProxyDaoSupport().getBeanIdColumn(getBeanContext());
		criterionAppender.add(Restrictions.eq(idColumn, beanId));
		return this.update(updateUnitAppender, criterionAppender);
	}

	/**
	 * 更新业务Bean字段信息
	 * @param updateUnit 更新单元
	 * @param criterion 条件
	 * @return
	 */
	protected int update(UpdateUnit updateUnit,Criterion criterion) {
		UpdateUnitAppender updateUnitAppender = UpdateUnitAppender.newInstance().add(updateUnit);
		CriterionAppender criterionAppender = CriterionAppender.newInstance().add(criterion);
		return this.update(updateUnitAppender, criterionAppender);
	}

	/**
	 * 更新业务Bean字段信息
	 * @param updateUnit 更新单元
	 * @param criterionAppender 条件
	 * @return
	 */
	protected int update(UpdateUnit updateUnit,CriterionAppender criterionAppender){
		UpdateUnitAppender updateUnitAppender = UpdateUnitAppender.newInstance().add(updateUnit);
		return this.update(updateUnitAppender, criterionAppender);
	}

	/**
	 * 多字段属性更新业务bean
	 * @param updateUnitAppender 更新单元追加器
	 * @param beanId 业务BeanId
	 */
	protected int update(UpdateUnitAppender updateUnitAppender,int beanId){
		Integer id = beanId;
		return this.update(updateUnitAppender, id.longValue());
	}

	/**
	 * 多字段属性更新业务bean
	 * @param updateUnitAppender 更新单元追加器
	 * @param beanId 业务BeanId
	 */
	protected int update(UpdateUnitAppender updateUnitAppender,long beanId){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		String idColumn = getProxyDaoSupport().getBeanIdColumn(getBeanContext());
		criterionAppender.add(Restrictions.eq(idColumn, beanId));
		return this.update(updateUnitAppender, criterionAppender);
	}
}
