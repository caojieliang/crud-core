package com.landian.crud.core.result;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class StatisticMapUtils {


    /**
     * 为String类型StatisticMap添加默认值
     * @param ids
     * @param statisticMap
     * @return
     */
    public static Map<String, Integer> defaultForString(List<String> ids, Map<String, Integer> statisticMap) {
        if(CollectionUtils.isEmpty(ids)){
            return MapUtils.EMPTY_MAP;
        }
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String id : ids) {
            Integer val = statisticMap.get(id);
            if(null == val){
                val = 0;
            }
            map.put(id,val);
        }
        return map;
    }
}
