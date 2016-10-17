package com.landian.crud.core.sql;

/**
 * Created by jie on 2016/10/17.
 * 分页SQL适配接口
 */
public interface PageSqlAdapter {
    /**
     * 将SQL包装为分页查询
     * @param sql 不带分页查询原SQL
     * @param start 开始条数
     * @param size 查询条数
     * @return
     */
    String wrapSQL(String sql, int start, int size);
}
