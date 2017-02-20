package com.landian.crud.core.result;

import com.landian.crud.core.context.impl.HashMapResultContext;
import com.landian.sql.jpa.context.JavaType;
import com.landian.sql.jpa.context.ResultMapConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class StatisticMapBuilder {

    private static final Logger logger = Logger.getLogger(StatisticMapBuilder.class);

    public static Map<String,Integer> buildStatisticMapForStringKey(
            ResultMapConfig resultMapConfig, HashMapResultContext hashMapResultContext) {
        if(hashMapResultContext.getResultCount() < 1){
            return MapUtils.EMPTY_MAP;
        }
        List<Map<String, Object>> resultList = hashMapResultContext.getResultObject();
        if(CollectionUtils.isEmpty(resultList)){
            return MapUtils.EMPTY_MAP;
        }
        String key = resultMapConfig.getKeyField();
        String value = resultMapConfig.getValueField();
        Map<String,Integer> statisticMap = new HashMap<String, Integer>();
        for(Map<String, Object> dataMap : resultList){
            if(null != dataMap){
                String keyObject = (String) dataMap.get(key);
                Object valueObject = dataMap.get(value);
                SingleValue _value = SingleValue.newInstance(valueObject);
                statisticMap.put(keyObject,_value.integerValue());
            }
        }
        return statisticMap;
    }
    public static StatisticMap buildStatisticMap(ResultMapConfig resultMapConfig, HashMapResultContext hashMapResultContext) {
        if(hashMapResultContext.getResultCount() < 1){
            return StatisticMap.EMPTY_MAP;
        }
        String key = resultMapConfig.getKeyField();
        String value = resultMapConfig.getValueField();
        List<Map<String, Object>> resultList = hashMapResultContext.getResultObject();
        JavaType javaType = checkKeyType(key, hashMapResultContext);
        if(null == javaType) {
            String errorMsg = "not support type for value : " + key;
            throw new RuntimeException(errorMsg);
        }
        if(javaType != JavaType.INT && javaType != JavaType.LONG){
            String errorMsg = "buildStatisticMap just support for key[{0}]  INT or LONG!";
            throw new RuntimeException(MessageFormat.format(errorMsg, key));
        }
        if(CollectionUtils.isEmpty(resultList)){
            return StatisticMap.EMPTY_MAP;
        }
        StatisticMap statisticMap = StatisticMap.newInstance();
        for(Map<String, Object> dataMap : resultList){
            if(null != dataMap){
                Long keyValue;
                if(javaType == JavaType.INT){
                    Integer keyObject = (Integer) dataMap.get(key);
                    keyValue = keyObject.longValue();
                }else{
                    keyValue = (Long) dataMap.get(key);
                }
                Object valueObject = dataMap.get(value);
                SingleValue _key = SingleValue.newInstance(keyValue);
                SingleValue _value = SingleValue.newInstance(valueObject);
                statisticMap.put(_key.bigDecimalValue(),_value.bigDecimalValue());
            }
        }
        return statisticMap;
    }

    private static JavaType checkKeyType(String key, HashMapResultContext hashMapResultContext) {
        List<Map<String, Object>> resultList = hashMapResultContext.getResultObject();
        Map<String, Object> dataMap = resultList.get(0);
        Object keyObject = dataMap.get(key);
        if(keyObject instanceof Integer){
            return JavaType.INT;
        }else if(keyObject instanceof Long){
            return JavaType.LONG;
        }else{
            logger.error("not support type for value : " + keyObject);
            return null;
        }
    }

}
