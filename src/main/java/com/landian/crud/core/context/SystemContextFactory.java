package com.landian.crud.core.context;

import com.landian.crud.core.sql.PageSqlAdapter;
import com.landian.crud.core.sql.impl.PageSqlAdapterMySQL;

/**
 * Created by jie on 2016/10/17.
 */
public class SystemContextFactory {
    /**
     *  返回具体数据库类型数值
     *  @see DataBase
     */
    public static PageSqlAdapter getPageSqlAdapter(){
        int dataBaseType = SystemContext.getDataBaseType();
        if(dataBaseType == DataBase.MySQL){
            return new PageSqlAdapterMySQL();
        }else{
            throw new RuntimeException("PageSqlAdapter just support PageSqlAdapterMySQL now!");
        }
    }
}
