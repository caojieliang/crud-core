package com.landian.crud.core.builder.impl;


import com.landian.crud.core.builder.SelectBuilder;
import com.landian.sql.jpa.sql.SelectUnitAppender;

/**
 * SelectBuilderFactory
 * 日后SelectBuilder都由这个获取,需要时间来搬代码了
 * to be continue
 */
public class SelectBuilderFactory{

	/**
	 * @param selectUnitAppender
	 * @return
	 */
	public static SelectBuilder builder(SelectUnitAppender selectUnitAppender){
		return SelectUnitSelectBuilder.newInstance(selectUnitAppender);
	}

	/**
	 * @param clazz
	 * @return
	 */
	public static SelectBuilder builder(Class clazz){
		return ClassReflectSelectBuilder.newInstance(clazz);
	}
}
