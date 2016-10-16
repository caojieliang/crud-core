package com.landian.crud.core.result;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * User: cao.jl
 * Date: 2016-06-07
 * Time: 10:58
 */
public class StatisticMap {

    public static StatisticMap EMPTY_MAP = new StatisticMap();

    Map<BigDecimal,BigDecimal> adaptorMap = new HashMap<BigDecimal,BigDecimal>();

    public static StatisticMap newInstance(){
        return new StatisticMap();
    }

    private StatisticMap() {
    }

    public StatisticMap put(BigDecimal key, BigDecimal value){
        adaptorMap.put(key,value);
        return this;
    }

    public Map<Integer,Integer> asIntIntMap(){
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        Iterator<BigDecimal> iterator = adaptorMap.keySet().iterator();
        while (iterator.hasNext()){
            BigDecimal key = iterator.next();
            BigDecimal value = adaptorMap.get(key);
            map.put(key.intValue(),value.intValue());
        }
        return map;
    }

    public Map<Integer,Long> asIntLongMap(){
        Map<Integer,Long> map = new HashMap<Integer,Long>();
        Iterator<BigDecimal> iterator = adaptorMap.keySet().iterator();
        while (iterator.hasNext()){
            BigDecimal key = iterator.next();
            BigDecimal value = adaptorMap.get(key);
            map.put(key.intValue(),value.longValue());
        }
        return map;
    }

    public Map<Long,Integer> asLongIntMap(){
        Map<Long,Integer> map = new HashMap<Long,Integer>();
        Iterator<BigDecimal> iterator = adaptorMap.keySet().iterator();
        while (iterator.hasNext()){
            BigDecimal key = iterator.next();
            BigDecimal value = adaptorMap.get(key);
            map.put(key.longValue(),value.intValue());
        }
        return map;
    }

    public Map<Long,Long> asLongLongMap(){
        Map<Long,Long> map = new HashMap<Long,Long>();
        Iterator<BigDecimal> iterator = adaptorMap.keySet().iterator();
        while (iterator.hasNext()){
            BigDecimal key = iterator.next();
            BigDecimal value = adaptorMap.get(key);
            map.put(key.longValue(),value.longValue());
        }
        return map;
    }
}
