package com.landian.crud.core.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/7.
 */
public interface ProxyDao {

    /**
     * 执行插入数据SQL
     * @param sql
     * @return
     */
    int doInsert(String sql);

    /**
     * 执行插入数据SQL(含ID)
     * @param sql
     * @return
     */
    int doInsertWidthId(String sql);

    /**
     * 执行插入并返回插入的自增ID值
     * (ID由具体数据库产生)
     * @param sql
     * @return
     */
    Object doInsertAndReturnId(String sql);

    /**
     * 执行更新
     * @param sql
     * @return
     */
    int doUpdate(String sql);

    /**
     * 执行查询
     * @param sql
     * @return
     */
    List<Map<String,Object>> doFind(String sql);

    /**
     * 查询数据集
     * @param sql SQL
     * @param start 第几条记录开始
     * @param pageSize 查询几条
     * @return
     */
//    List<Map<String,Object>> doFindPage(String sql, int start, int pageSize);

    /**
     *
     * @param sql
     * @return
     */
    int doDelete(String sql);

    /**
     *
     * @param sql
     * @return
     */
    List<Long> queryAsLongValue(String sql);

    /**
     *
     * @param sql
     * @return
     */
    List<Integer> queryAsIntValue(String sql);
}
