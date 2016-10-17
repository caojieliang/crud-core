package com.landian.crud.core.context.impl;


import com.landian.crud.core.context.ResultContext;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * how to use org.apache.ibatis.session.ResultContext ?
 * HashMap结果集
 */
public class HashMapResultContext implements ResultContext {
	public HashMapResultContext(List<Map<String, Object>> resultList) {
		if(CollectionUtils.isNotEmpty(resultList)){
			if(resultList.size() > 1){
				this.resultCount = resultList.size();
			}else{
				//处理空条情况
				Map<String, Object> hashMap = resultList.get(0);
				if(null != hashMap){
					this.resultCount = 1;
				}
			}
		}
		this.resultList = resultList;
	}
	
	/**
	 * 结果集
	 */
	private List<Map<String, Object>> resultList;
	/**
	 * 条数
	 */
	private int resultCount = 0;
	
	@Override
	public List<Map<String, Object>> getResultObject() {
		return resultList;
	}
	@Override
	public int getResultCount() {
		return resultCount;
	}
	
	/**
	 * 返回单列数据集
	 */
	@SuppressWarnings("rawtypes")
	public List singleList() {
		List<Object> dataList = new ArrayList<Object>();
		if(!CollectionUtils.isEmpty(resultList)){
			for(Map<String, Object> dataMap : resultList){
				if(null != dataMap){
					Iterator<Object> iterator = dataMap.values().iterator();
					if(iterator.hasNext()){
						Object value = iterator.next();
						dataList.add(value);
					}
				}
			}
		}
		return dataList;
	}
	
	/**
	 * 返回唯一结果值
	 */
	@SuppressWarnings("rawtypes")
	public Object singleResult() {
		List dataList = singleList();
		if(!CollectionUtils.isEmpty(dataList)){
			return dataList.get(0);
		}
		return null;
	}

	/**
	 * 返回唯一结果值
	 */
	public Double singleResultDouble() {
		Object result = singleResult();
		if(null != result && result instanceof Double){
			Double value = (Double)result;
			return value;
		}
		return Double.NaN;
	}

	/**
	 * 返回唯一结果值
	 */
	public BigDecimal singleResultBigDecimal() {
		Object result = singleResult();
		if(null != result && result instanceof BigDecimal){
			BigDecimal value = (BigDecimal)result;
			return value;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 转换为Map
	 * @param key 作为key的属性字符串
	 * @param value 作为value的属性字符串
	 * @param hashMapResultContext
	 * @param <K> key范型
	 * @param <V> value范型
	 * @return
	 */
	public static <K, V>
	Map<K,V> asMap(String key, String value, HashMapResultContext hashMapResultContext) {
		List<Map<String, Object>> resultList = hashMapResultContext.getResultObject();
		Map<K,V> asMap = new HashMap<K,V>();
		if(!CollectionUtils.isEmpty(resultList)){
			for(Map<String, Object> dataMap : resultList){
				if(null != dataMap){
					K keyObject = (K) dataMap.get(key);
					V valueObject = (V) dataMap.get(value);
					asMap.put(keyObject, valueObject);
				}
			}
		}
		return asMap;
	}
}
