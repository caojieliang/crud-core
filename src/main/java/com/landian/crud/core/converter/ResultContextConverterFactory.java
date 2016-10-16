package com.landian.crud.core.converter;


import com.landian.crud.core.converter.impl.ConventionConverter;
import com.landian.crud.core.converter.impl.JavaBeanConverter;

/**
 * ResultContextConverterFactory
 */
public class ResultContextConverterFactory {

	/**
	 * 构建转换器,默认是约定大于配置策略(就是会解释注释的指定配置)
	 *
	 */
	public static ResultContextConverter build(Class beanClass){
		ConverStrategyPolicy converStrategyPolicy = ConverStrategyPolicy.CONVENTION;
		return build(beanClass, converStrategyPolicy);
	}

	/**
	 * 构建转换器
	 */
	public static ResultContextConverter build(Class beanClass, ConverStrategyPolicy converStrategyPolicy){
		if(ConverStrategyPolicy.CONVENTION == converStrategyPolicy){
			return ConventionConverter.newInstance(beanClass);
		}else if(ConverStrategyPolicy.JAVA_BEAN == converStrategyPolicy){
			return JavaBeanConverter.newInstance(beanClass);
		}
		//最后默认还是约定大于配置策略
		return ConventionConverter.newInstance(beanClass);
	}
}
