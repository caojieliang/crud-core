package com.landian.crud.core.builder.impl;

import com.landian.crud.core.context.ResultMapContext;
import com.landian.crud.core.provider.ProviderHelper;
import com.landian.sql.jpa.annotation.*;
import com.landian.sql.jpa.context.BeanContext;
import com.landian.sql.jpa.context.ResultMappingVirtual;
import com.landian.sql.jpa.criterion.Criterion;
import com.landian.sql.jpa.criterion.CriterionAppender;
import com.landian.sql.jpa.criterion.Restrictions;
import com.landian.sql.jpa.sql.SQLInjectPolicy;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;


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
		return build(searchVo,null);
	}

	/**
	 *
	 * @param searchVo 查询对象
	 * @param beanContext 映射关系上下文
	 * @return
	 */
	public static CriterionAppender build(Object searchVo, BeanContext beanContext){
		return build(searchVo, beanContext, true);
	}

	/**
	 *
	 * @param searchVo 查询对象
	 * @param beanContext 映射关系上下文
	 * @param fuzzy 字符串是否模糊查询
	 * @return
	 */
	public static CriterionAppender build(Object searchVo, BeanContext beanContext, boolean fuzzy){
		CriterionAppender criterionAppender = CriterionAppender.newInstance();
		Class clazz = searchVo.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Map<String, ResultMappingVirtual> resultMappingMap = MapUtils.EMPTY_MAP;
		if(null != beanContext){
			Class beanClazz = beanContext.getBeanClass();
			resultMappingMap = ResultMapContext.getResultMappingMap(beanClazz);
		}

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
					String errorMsg = "构建" + searchVo + "属性" + fieldName + "条件异常！";
					logger.error(errorMsg);
					logger.error(e.getMessage(), e);
				}
				if(null != value){ // 1.只针对不为空的值建造查询条件
					String column = fieldName;
					Class fieldType = field.getType();
					if (fieldType == Date.class){ // 日期类型特殊处理
						DateCriterion dateCriterion = field.getAnnotation(DateCriterion.class);
						if(null != dateCriterion) {
							column = dateCriterion.column();
						}
					}else if(MapUtils.isNotEmpty(resultMappingMap)){
						column = resultMappingMap.get(fieldName).getColumn();
					}
					Criterion criterion = buildCriterion(field, column, value, fuzzy);
					if(null != criterion){
						criterionAppender.add(criterion);
					}
				}

			}
		}
		return criterionAppender;
	}

	private static Criterion buildCriterion(Field field,String columnName, Object value, boolean fuzzy){
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
				if(fuzzy){ // 指定模糊查询
					QueryEqual queryEqual = field.getAnnotation(QueryEqual.class);
					if(null != queryEqual) { // 如果标记有QueryEqual注解，指定模糊查询无效
						criterion = Restrictions.eq(columnName, stringValue.trim());
					}else{
						criterion = Restrictions.like(columnName, stringValue.trim());
					}
				}else{
					criterion = Restrictions.eq(columnName, stringValue.trim());
				}
			}
		}else if (fieldType == Date.class) {
			Date dateVal = (Date) value;
			DateCriterion dateCriterion = field.getAnnotation(DateCriterion.class);
			if(null != dateCriterion) {
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
