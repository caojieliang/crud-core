package com.landian.crud.core.result;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA
 * User: cao.jl
 * Date: 2016-04-06
 * Time: 10:58
 * to be continue
 */
public class SingleValue {

    public static SingleValue newInstance(Object value){
        return new SingleValue(value);
    }

    private SingleValue(Object value) {
        this.value = value;
        adapt2MathValue(value);
    }

    private Object value;

    private BigDecimal mathValue = BigDecimal.ZERO;

    /**
     * 适配为数据值(目前借用BigDecimal为容器)
     * @param value
     */
    private void adapt2MathValue(Object value) {
        if(null == value){
            return;
        }
        if(value instanceof Double){
            Double target = (Double)value;
            mathValue = BigDecimal.valueOf(target);
        }
        if(value instanceof BigDecimal){
            mathValue = (BigDecimal)value;
        }
        if(value instanceof Integer){
            Integer target = (Integer)value;
            mathValue = BigDecimal.valueOf(target);
        }
        if(value instanceof Long){
            Long target = (Long)value;
            mathValue = BigDecimal.valueOf(target);
        }
        if(value instanceof Float){
            Float target = (Float)value;
            mathValue = BigDecimal.valueOf(target);
        }
    }

    /**
     * objectValue
     */
    public Object objectValue() {
        return value;
    }

    /**
     * doubleValue
     */
    public Double doubleValue() {
        return mathValue.doubleValue();
    }

    /**
     * bigDecimalValue
     */
    public BigDecimal bigDecimalValue() {
        return mathValue;
    }

    /**
     * integerValue
     */
    public Integer integerValue() {
        return mathValue.intValue();
    }

    /**
     * longValue
     */
    public Long longValue() {
        return mathValue.longValue();
    }

    /**
     * bigDecimalValue
     */
    public String stringValue() {
        String str = "";
        if(null != value && value instanceof String){
            str = (String) value;
        }
        return str;
    }

    public static void main(String[] args) {
        Object value = 3l;
        if(value instanceof Long){
            System.out.println(true);
        }
    }
}
