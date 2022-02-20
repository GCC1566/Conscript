package com.gcc.conscript;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 初始化数据库机制
 * @athor GCC
 */
public interface InitDataBase {


    /**
     * 数据库初始化条件是否完成
     * @return boolean
     */
    boolean isInitEd();

    /**
     * 核心任务
     * 1、建立链接
     * 2、是否可链接
     * 3、是否存在库
     * 4、版本是否需要升级
     */
    void startCoreJob() throws SQLException;

    /**
     * 建立链接
     * @return boolean
     */
    boolean createConnection();

    /**
     * 确认库是否存在
     * @return boolean
     */
    boolean databaseIsExitd() throws SQLException;

    /**
     * 获取当前数据库版本
     * @return
     */
    Float getCurrenDbVersion() throws SQLException;

    /**
     * 执行sql内容
     * @param sqlcontent
     * @return
     * @throws SQLDataException
     */
    boolean excuteSQL(Map<String, String> sqlcontent) throws SQLDataException;

    /**
     * 调整当前数据库版本号
     */
    void updateDbVersion() throws SQLException;


    /**
     * 关闭连接
     * @return
     */
    void close() throws SQLException;
}