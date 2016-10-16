package com.landian.crud.core.result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/30.
 */
public class ResultMapUtils {

    /**
     *
     * @param resultMap
     * @param typeIdIds
     * @return
     */
    public static Map<Long, Integer> defaultZero(Map<Long, Integer> resultMap, List<Long> typeIdIds) {
        for (long typeIdId : typeIdIds) {
            Integer value = resultMap.get(typeIdId);
            if(null == value){
                value = 0;
            }
            resultMap.put(typeIdId,value);
        }
        return resultMap;
    }
}
