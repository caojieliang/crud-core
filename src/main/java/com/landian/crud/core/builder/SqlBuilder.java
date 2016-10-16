package com.landian.crud.core.builder;


/**
 *  SqlBuilder
 */
public interface SqlBuilder {
	
	/**
	 * 查询接口 
	 */
	String SQL();
	
	/**
	 * 分页查询接口 
	 * @param start
	 * @param size
	 */
	String SQLPage(int start,int size);
	/**
	 * 总数查询接口 
	 */
	String SQLCount();
	
}
