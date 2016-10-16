package com.landian.crud.core.dao;

public class SQLPageUtils {

    public static String appendLimit(String sql, int start, int size){
        return sql + " limit " + start + " , " + size;
    }
}
