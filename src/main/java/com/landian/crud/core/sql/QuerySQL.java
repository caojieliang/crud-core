package com.landian.crud.core.sql;

import com.landian.crud.core.builder.impl.SimpleSqlBuilder;
import com.landian.sql.builder.SQL;
import lombok.Data;

import java.util.List;

/**
 * Created by jie on 2017/8/6.
 * @see SimpleSqlBuilder
 */
@Data
public class QuerySQL {

    public static QuerySQL newInstance(SQL sql,List<Object> whereParams){
        return new QuerySQL(sql, whereParams);
    }

    private QuerySQL(SQL sql, List<Object> whereParams){
        this.sql = sql;
        this.whereParams = whereParams;
    }

    /**
     * SQL对象
     */
    private SQL sql;
    /**
     * 条数数据的值
     */
    private List<Object> whereParams;

}
