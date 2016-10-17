package com.landian.crud.core.context;

import java.util.List;
import java.util.Map;


/**
 * how to use org.apache.ibatis.session.ResultContext ?
 * 结果集接口
 */
public interface ResultContext{
	List<Map<String, Object>> getResultObject();
	
	int getResultCount();
}
