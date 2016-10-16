package com.landian.crud.core.context;

import java.util.HashMap;
import java.util.List;


/**
 * how to use org.apache.ibatis.session.ResultContext ?
 * 结果集接口
 */
public interface ResultContext{
	List<HashMap<String, Object>> getResultObject();
	
	int getResultCount();
}
