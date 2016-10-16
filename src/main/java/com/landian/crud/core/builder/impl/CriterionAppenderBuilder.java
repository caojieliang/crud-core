package com.landian.crud.core.builder.impl;

import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.jpa.annotation.*;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.criterion.Restrictions;
import com.landian.sql.jpa.sql.SQLInjectPolicy;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Date;


/**
 * 条件追加器建造器
 * 约定大于配置
 * 解释建造规则
 * 0.忽略透明注解属性
 * 1.只针对不为空的值建造查询条件
 * 2.默认Java字段与数据字段一置
 * 3.验证是否存在Column注解修正映射字段
 * 4.只解释构建Integer Long String 类型查询条件
 */
public class CriterionAppenderBuilder {
	
	private static final Logger logger = Logger.getLogger(CriterionAppenderBuilder.class);

	private static boolean needToBuildCriterionField(Field field,String fieldName){
		//0.忽略透明注解属性
		Transient transientAnnotations = field.getAnnotation(Transient.class);
		if(null != transientAnnotations){
			return false;
		}
		if(ProviderHelper.isIgnoreField(field, fieldName)){
			return false;
		}
		return true;
	}

	/**
	 * 根据查询VO，约定大于配置构建查询条件
	 * @param searchVo
	 * @return
	 */
	public static CriterionAppender build(Object searchVo){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		Class clazz = searchVo.getClass();
		Field[] fields = clazz.getDeclaredFields();
		if(null != fields && fields.length > 0){
			for(Field field : fields){
				//可以访问private
				field.setAccessible(true);
				String fieldName = field.getName();
				//0.忽略透明不需要构建的部分
				if(!needToBuildCriterionField(field, fieldName)){
					continue;
				}
				Object value = null;
				try {
					value = field.get(searchVo);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					String errorMsg = "构建" + searchVo + "属性" + fieldName + "条件异常！";
//					JieLoggerProxy.error(logger,errorMsg);
//					JieLoggerProxy.error(logger, e);
					logger.error(errorMsg);
					logger.error(e.getMessage(), e);
				}
				if(null != value){ //1.只针对不为空的值建造查询条件
					Criterion criterion = buildCriterion(field, fieldName, value);
					if(null != criterion){
						criterionAppender.add(criterion);
					}
				}

			}
		}
		return criterionAppender;
	}

	private static Criterion buildCriterion(Field field,String fieldName,Object value){
		//2.默认Java字段与数据字段一置
		String columnName = fieldName;
		/*3.验证是否存在Column注解修正映射字段*/
		Column column = field.getAnnotation(Column.class); //Column注解
		if(null != column){
			columnName = column.column();
		}
		//4.只解释构建Integer Long String 类型查询条件
		Criterion criterion = null;
		Class fieldType = field.getType();
		if(fieldType == Integer.class){
			Integer intValue = (Integer) value;
			criterion = Restrictions.eq(columnName, intValue);
		}else if(fieldType == Long.class){
			Long longValue = (Long) value;
			criterion = Restrictions.eq(columnName, longValue);
		}else if (fieldType == String.class) {
			String stringValue = (String) value;
			//替换掉这些特殊字符可以防止sql注入
			stringValue = SQLInjectPolicy.transform(stringValue);
			if(StringUtils.isNotBlank(stringValue)){
				//字符串默认模糊查询
				criterion = Restrictions.like(columnName, stringValue.trim());
			}
		}else if (fieldType == Date.class) {
			Date dateVal = (Date) value;
			DateCriterion dateCriterion = field.getAnnotation(DateCriterion.class);
			if(null != dateCriterion) {
				columnName = dateCriterion.column();
				criterion = buildCriterionDate(dateVal,columnName,dateCriterion);
			}
		}
		return criterion;
	}

	private static Criterion buildCriterionDate(Date date,String columnName,DateCriterion dateCriterion) {
		DateTypePolicy dateTypePolicy = dateCriterion.dateType();
		DateRangePolicy type = dateCriterion.type();
		if(dateTypePolicy == DateTypePolicy.DATE){
			if(type == DateRangePolicy.BEGIN){
				return Restrictions.dateGe(columnName, date);
			}else if(type == DateRangePolicy.END){
				return Restrictions.dateLe(columnName, date);
			}
		}if(dateTypePolicy == DateTypePolicy.DATE_TIME){
			if(type == DateRangePolicy.BEGIN){
				return Restrictions.dateTimeGe(columnName, date);
			}else if(type == DateRangePolicy.END){
				return Restrictions.dateTimeLe(columnName, date);
			}
		}
		return null;
	}

}
