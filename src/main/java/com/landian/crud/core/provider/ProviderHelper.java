package com.landian.crud.core.provider;

import com.landian.sql.jpa.annotation.Transient;
import javassist.Modifier;
import com.landian.sql.jpa.criterion.CriterionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

public class ProviderHelper {

	private static final Logger logger = Logger.getLogger(ProviderHelper.class);

	public static void initFieldList(Class clazz,List<Field> fieldList){
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			String fieldName = field.getName();
			if(!isIgnoreField(field,fieldName)){
				fieldList.add(field);
			}
		}
	}

	/*	由于基本类型存在默认值(不好识别是否为空)，笔者决定在设计实现上不解释基本类型
	 */
	public static boolean isIgnoreField(Field field,String fieldName) {
		if("serialVersionUID".equals(fieldName)) { // 过滤serialVersionUID此属性
			return true;
		}
		if(Modifier.isStatic(field.getModifiers())) { //过滤静态属性
			return true;
		}
		if(field.getType() == int.class || field.getType() == long.class ||
				field.getType() == double.class || field.getType() == short.class || 
				field.getType() == byte.class || field.getType() == float.class || 
				field.getType() == boolean.class || field.getType() == char.class){
			return true;
		}
		return false;
	}

	public static boolean isBossTransientField(Field field){
		Transient transientAnnotations = field.getAnnotation(Transient.class); //透明注解
		if(null != transientAnnotations){ //透明注解不需要拼接Sql
			return true;
		}
		return false;
	}
	
	//转为set方法名
	public static String toGetMethodName(String fieldName){
		String getMethodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length());
		return getMethodName;
	}
	
	//转为get方法名
	public static String toSetMethodName(String fieldName){
		String setMethodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length());
		return setMethodName;
	}

	public static String buildUpdateCron(Field field, Object value){
		String updateCron = "";
		Class fieldType = field.getType();
		if(fieldType == Integer.class){
			Integer intValue = (Integer) value;
			updateCron = intValue.toString();
		}else if(fieldType == Long.class){
			Long longValue = (Long) value;
			updateCron = longValue.toString();
		}else if (fieldType == String.class) {
			String valueStr = (String) value;
			updateCron = "'" + valueStr + "'";
		}else if (fieldType == Date.class) {
			Date dateVal = (Date) value;
			updateCron = "'" + CriterionUtils.TIME_FORMAT.format(dateVal) + "'";
		}else if (fieldType == BigDecimal.class) {
			BigDecimal bigDecimal = (BigDecimal) value;
			updateCron = bigDecimal.toString();
			if(updateCron.length() > 10){
				updateCron = updateCron.substring(0,10);
			}
		}else if (fieldType == Short.class) {
			Short aShort = (Short) value;
			updateCron = aShort.toString();
		}else{
			String message = MessageFormat.format("buildUpdateCron未解释类型{0}", field);
			logger.warn(message);
		}
		return updateCron;
	}
}
