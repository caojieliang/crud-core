package com.landian.crud.core.sql;

import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.log.JieLoggerProxy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateSQLBuilder{
	
	private static final Logger logger = Logger.getLogger(UpdateSQLBuilder.class);

	public static UpdateSQL updateWithNull(Object bean, List<String> ignoreFields, BeanContext beanContext){
		return update(bean,ignoreFields,beanContext,true);
	}

	public static UpdateSQL updateNotNull(Object bean, BeanContext beanContext){
		return updateNotNull(bean,Collections.EMPTY_LIST,beanContext);
	}

	private static UpdateSQL updateNotNull(Object bean, List<String> ignoreFields, BeanContext beanContext){
		return update(bean,ignoreFields,beanContext,false);
	}

	private static UpdateSQL update(Object bean, List<String> ignoreFields, BeanContext beanContext, boolean updateAll){
		try {
			Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(beanContext.getBeanClass());
			String idFieldName = beanContext.getIdFieldName();
			Object idValue = getIdValue(bean,idFieldName);
			if(null == idValue){
				String msg = "更新业务BeanID不能为空！" + bean;
				JieLoggerProxy.error(logger, msg);
				throw new RuntimeException(msg);
			}
			SQL sql = new SQL();
			sql.UPDATE(beanContext.getTableName());
			List<Object> params = builtUpdate(sql, bean,idFieldName,updateAll,ignoreFields);
			List<Object> whereParams = builtWHERE(sql, idValue,resultMappingMap.get(idFieldName).getColumn());
			return UpdateSQL.newInstance(beanContext.getIdType(),sql,params, whereParams);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Object getIdValue(Object bean,String idFieldName) throws Exception{
		String getMethodName = ProviderHelper.toGetMethodName(idFieldName);
		Method idGetMethod = bean.getClass().getDeclaredMethod(getMethodName);
		Object idValue = idGetMethod.invoke(bean);
		return idValue;
	}

	/**
	 *
	 * @param sql
	 * @param bean
	 * @param idFieldName
	 * @param updateAll
	 * @param ignoreFields 忽略的属性
	 * @return
	 * @throws Exception
	 */
	private static List<Object> builtUpdate(SQL sql, Object bean,String idFieldName,boolean updateAll, List<String> ignoreFields) throws Exception{
		List<Object> params = new ArrayList();
		Object obj = bean;
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(bean.getClass());
		List<Field> fieldList = new ArrayList<Field>();
		ProviderHelper.initFieldList(obj.getClass(),fieldList);
		for (Field field : fieldList) {
			String fieldName = field.getName();
			if(CollectionUtils.isNotEmpty(ignoreFields) && ignoreFields.contains(fieldName)){
				continue;
			}
			//忽略透明属性、ID属性和其它不必要属性
			if(ProviderHelper.isIgnoreField(field,fieldName) || ProviderHelper.isBossTransientField(field)
					|| fieldName.equals(idFieldName)){
				continue;
			}
			field.setAccessible(true); // 可以访问private
			Object value = field.get(obj);
			ResultMappingVirtual resultMappingVirtual = resultMappingMap.get(fieldName);
			if(null == resultMappingVirtual){
				String errorMsg = bean.toString() + ",字段:" + fieldName + "不存在映射关系！";
				JieLoggerProxy.error(logger,errorMsg);
			}
			String column = resultMappingVirtual.getColumn();
			if(null != value){
				sql.SET(column + " = ? ");
				params.add(value);
			}else{
				if(updateAll){
					sql.SET(column + " = ? ");
					params.add(null);
				}
			}
		}
		return params;
	}



	private static List<Object> builtWHERE(SQL sql, Object beanId, String idColumnName){
		List<Object> params = new ArrayList();
		sql.WHERE(idColumnName + " = ? ");
		params.add(beanId);
		return params;
	}

}
