package com.landian.crud.core.sql;

import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InsertSQLBuilder{

	private static final Logger logger = Logger.getLogger(InsertSQLBuilder.class);

	public static InsertSQL insertSQL(Object bean, BeanContext beanContext){
		try {
			SQL sql = new SQL();
			sql.INSERT_INTO(beanContext.getTableName());
			List<Object> params = myVALUES(sql,bean,beanContext,true);
			InsertSQL insertSQL = InsertSQL.newInstance(sql,params);
			return insertSQL;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static InsertSQL insertWithIdSQL(Object bean, BeanContext beanContext){
		try {
			SQL sql = new SQL();
			sql.INSERT_INTO(beanContext.getTableName());
			List<Object> params = myVALUES(sql,bean,beanContext,false);
			InsertSQL insertSQL = InsertSQL.newInstance(sql,params);
			return insertSQL;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private static List<Object> myVALUES(SQL sql, Object bossBean,BeanContext beanContext,
												boolean ignoreId) throws Exception{
		List<Object> params = new ArrayList();
		Object obj = bossBean;
		Class clazz = beanContext.getBeanClass();
		Map<String, ResultMappingVirtual> resultMappingMap = ResultMapContext.getResultMappingMap(clazz);
		Field[] fields = clazz.getDeclaredFields();
		String idFieldName = beanContext.getIdFieldName();
		if(null != fields){
			for (Field field : fields) {
				String fieldName = field.getName();
				//忽略透明属性、其它不必要属性
				if(ProviderHelper.isIgnoreField(field,fieldName) || ProviderHelper.isBossTransientField(field)){
					continue;
				}
				//忽略ID属性
				if(ignoreId){
					if(fieldName.equals(idFieldName)){
						continue;
					}
				}
				field.setAccessible(true); // 可以访问private
				Object value = field.get(obj);
				if(null != value){
					String column = resultMappingMap.get(fieldName).getColumn();
					sql.VALUES(column, "?");
					params.add(value);
				}
			}
		}
		return params;
	}

}
