package com.landian.crud.core.sql;

import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.builder.SQL;
import com.landian.sql.builder.SqlBuilder;
import com.landian.sql.jpa.annotation.IdTypePolicy;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.log.JieLoggerProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateSQLBuilder{
	
	private static final Logger logger = Logger.getLogger(UpdateSQLBuilder.class);

	public static UpdateSQL updateNotNull(Object bean, BeanContext beanContext){
		try {
			Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(beanContext.getBeanClass());
			String idFieldName = beanContext.getIdFieldName();
			Long beanId = getIdValue(bean,idFieldName,beanContext);
			if(null == beanId){
                String msg = "更新业务BeanID不能为空！" + bean;
                JieLoggerProxy.error(logger, msg);
                throw new RuntimeException(msg);
            }
            SQL sql = new SQL();
			sql.UPDATE(beanContext.getTableName());
			List<Object> params = builtUpdate(sql, bean,idFieldName,false);
			List<Object> whereParams = builtWHERE(sql, beanId,resultMappingMap.get(idFieldName).getColumn());
			return UpdateSQL.newInstance(beanContext.getIdType(),sql,params, whereParams);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Long getIdValue(Object bean,String idFieldName,BeanContext beanContext) throws Exception{
		String getMethodName = ProviderHelper.toGetMethodName(idFieldName);
		Method idGetMethod = bean.getClass().getDeclaredMethod(getMethodName);
		Long beanId = 0l;
		if(beanContext.getIdType() == IdTypePolicy.LONG){
			beanId = (Long) idGetMethod.invoke(bean);
		}else if(beanContext.getIdType() == IdTypePolicy.BIGDECIMAL){
			BigDecimal bigDecimal = (BigDecimal) idGetMethod.invoke(bean);
			beanId = bigDecimal.longValue();
		}else{
			Integer proxyId = (Integer) idGetMethod.invoke(bean);
			beanId = proxyId.longValue();
		}
		return beanId;
	}

	private static List<Object> builtUpdate(SQL sql, Object bean,String idFieldName,boolean updateAll) throws Exception{
		List<Object> params = new ArrayList();
		Object obj = bean;
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(bean.getClass());
		List<Field> fieldList = new ArrayList<Field>();
		ProviderHelper.initFieldList(obj.getClass(),fieldList);
		for (Field field : fieldList) {
			String fieldName = field.getName();
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

	private static List<Object> builtWHERE(SQL sql, Long beanId, String idColumnName){
		List<Object> params = new ArrayList();
		sql.WHERE(idColumnName + " = ? ");
		params.add(beanId);
		return params;
	}

}
