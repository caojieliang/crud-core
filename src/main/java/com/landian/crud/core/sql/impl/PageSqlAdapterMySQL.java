package com.landian.crud.core.sql.impl;

import com.landian.crud.core.sql.PageSqlAdapter;

/**
 * Created by jie on 2016/10/17.
 */
public class PageSqlAdapterMySQL implements PageSqlAdapter{

    @Override
    public String wrapSQL(String sql, int start, int size) {
        return sql + " limit " + start + " , " + size;
    }
}
