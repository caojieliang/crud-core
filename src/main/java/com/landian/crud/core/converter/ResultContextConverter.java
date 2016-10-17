package com.landian.crud.core.converter;

import java.util.Map;


/**
 * 转换器接口
 * 感觉目前的转换器实现有点暴力，尚且能用，有待进化
 */
public interface ResultContextConverter{
	Object convert(Map<String, Object> dataMap);
}
