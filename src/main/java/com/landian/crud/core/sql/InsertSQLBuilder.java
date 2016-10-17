package com.landian.crud.core.sql;

import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.builder.SqlBuilder;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;


public class InsertSQLBuilder extends SqlBuilder{

	private static final Logger logger = Logger.getLogger(InsertSQLBuilder.class);

	public static String insertSQL(Object bean, BeanContext beanContext){
		try {
			BEGIN();
			INSERT_INTO(beanContext.getTableName());
			myVALUES(bean,beanContext,true);
			String sql = SQL();
			return sql;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String insertWithIdSQL(Object bean, BeanContext beanContext){
		try {
			BEGIN();
			INSERT_INTO(beanContext.getTableName());
			myVALUES(bean,beanContext,false);
			String sql = SQL();
			return sql;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*由于基本类型存在默认值(不好识别是否为空)，笔者决定在设计实现上不解释基本类型
 *
 * PS:目前验证通过类型String Integer Long Date
 */
	private static void myVALUES(Object bossBean,BeanContext beanContext, boolean ignoreId) throws Exception{
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
				field.setAccessible(true); //可以访问private
				Object value = field.get(obj);
				//String typeConfigStr = "javaType=string,jdbcType=VARCHAR";
				if(null != value){
					String column = resultMappingMap.get(fieldName).getColumn();
					String updateCron = ProviderHelper.buildUpdateCron(field,value);
					if(StringUtils.isBlank(updateCron)){
						String message = MessageFormat.format("myVALUES未解释类型{0}:{1}", column, field);
						logger.warn(message);
					}else{
						VALUES(column, updateCron); //好像不需要类型与能成功
					}
				}
			}
		}
	}

}
