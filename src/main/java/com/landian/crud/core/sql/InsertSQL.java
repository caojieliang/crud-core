package com.landian.crud.core.sql;

import com.landian.sql.builder.SQL;
import lombok.Data;

import java.util.List;

/**
 * Created by jie on 2017/1/1.
 *
 */
@Data
public class InsertSQL {

    public static InsertSQL newInstance(SQL sql, List<Object> params){
        return new InsertSQL(sql,params);
    }

    private InsertSQL(SQL sql, List<Object> params){
        this.sql = sql;
        this.params = params;
    }

    /**
     * SQL对象
     */
    private SQL sql;
    /**
     * 插入数据的值
     */
    private List<Object> params;

}
