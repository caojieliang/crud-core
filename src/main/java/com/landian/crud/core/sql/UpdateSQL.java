package com.landian.crud.core.sql;

import com.landian.sql.builder.SQL;
import com.landian.sql.jpa.annotation.IdTypePolicy;
import lombok.Data;

import java.util.List;

/**
 * Created by jie on 2017/1/1.
 *
 */
@Data
public class UpdateSQL {

    public static UpdateSQL newInstance(IdTypePolicy idTypePolicy, SQL sql,
                                        List<Object> updateParams, List<Object> whereParams){
        return new UpdateSQL(idTypePolicy,sql,updateParams, whereParams);
    }

    private UpdateSQL(IdTypePolicy idTypePolicy, SQL sql, List<Object> updateParams, List<Object> whereParams){
        this.idTypePolicy = idTypePolicy;
        this.sql = sql;
        this.updateParams = updateParams;
        this.whereParams = whereParams;
    }

    /**
     * IdTypePolicy，ID类型
     */
    private IdTypePolicy idTypePolicy;
    /**
     * SQL对象
     */
    private SQL sql;
    /**
     * 更新数据的值
     */
    private List<Object> updateParams;
    /**
     * 条数数据的值
     */
    private List<Object> whereParams;

}
