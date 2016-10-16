package com.landian.crud.core.converter.impl;

import com.landian.crud.core.converter.ResultContextConverter;
import com.landian.sql.jpa.utils.BeanToMapUtils;

import java.util.HashMap;



/**
 * JavaBean对像转换器
 */
public class JavaBeanConverter implements ResultContextConverter {
	
	public static JavaBeanConverter newInstance(Class beanClass){
		return new JavaBeanConverter(beanClass);
	}
	
	private JavaBeanConverter(Class beanClass) {
		this.beanClass = beanClass;
	}
	/**
	 * class
	 */
	private Class beanClass;


	@Override
	public Object convert(HashMap<String, Object> dataMap) {
		Object object = BeanToMapUtils.toBean(beanClass, dataMap);
		return object;
	}
}
