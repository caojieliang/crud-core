package com.landian.crud.core.context;

/**
 * Created by jie on 2016/10/17.
 */
public class SystemContext {
    /**
     *  返回具体数据库类型数值
     *  @see DataBase
     */
    public static int getDataBaseType(){
        //目前暂时写死MySQL，日后进化
        return DataBase.MySQL;
    }
}
