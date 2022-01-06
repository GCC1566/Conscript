package com.gcc.initdb.mysql;

import cn.hutool.core.date.DateUtil;
import com.gcc.initdb.AbstractInitDataBase;
import com.gcc.initdb.entity.DbConConfiguration;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.Map;

/**
 * Mysql数据库初始化类
 * @author GCC
 */
@Slf4j
public class MySqlDataBase extends AbstractInitDataBase {

    private Connection conn = null;

    private Driver driver = null;

    private Statement stmt = null;

    public MySqlDataBase(DbConConfiguration conConfiguration){
        super(conConfiguration);
    }

    @Override
    public boolean createConnection() {
        try{
            Class.forName(dbConConfiguration.getDriverclassname());
            String jdbc_url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/mysql?characterEncoding=utf8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(jdbc_url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            String url ="jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/mysql?user="+dbConConfiguration.getUser()+"&password="+dbConConfiguration.getPassword()+"&serverTimezone=GMT%2B8";
            driver = DriverManager.getDriver(url);
            if(conn != null){
                return true;
            }
        }catch (Exception e){
           log.error("data base is not connection.....");
        }
        return false;
    }

    @Override
    public boolean databaseIsExitd() throws SQLException {
        try {
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name= \""+dbConConfiguration.getDbname()+"\"");
            if(res.next() && res.getInt(1) == 0){
                stmt.execute("CREATE DATABASE IF NOT EXISTS "+dbConConfiguration.getDbname()+" DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci");
                res.close();
                stmt.close();
                stmt = null;
                return false;
            }
            return true;
        }catch (Exception e){
            conn.close();
            stmt.close();
            conn = null;
            stmt = null;
            log.error("database base query is error");
        }
        return false;
    }


    @Override
    public Float getCurrenDbVersion() throws SQLException {
        float version = 0f;
        try {
            String url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/"+dbConConfiguration.getDbname()+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT version FROM research_dbversion ");
            if(res.next()){
                version = Float.parseFloat(res.getString("version"));
            }else{
                log.error("数据库混乱，缺少 tb_dbversion 数据源");
            }
        }catch (Exception e){
            log.error("缺少基础版本号所需信息："+e.getMessage());
        }finally {
            conn.close();
            stmt.close();
        }
        return version;
    }

    @Override
    public boolean excuteSQL(Map<String,String> sqlcontent) throws SQLDataException{
        try {
            String url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/"+dbConConfiguration.getDbname()+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            stmt = conn.createStatement();
            for (Map.Entry<String,String> entry : sqlcontent.entrySet()) {
                log.info("【数据库初始化】正在升级sql文件 ——>" + entry.getKey());
                String[] sqls = entry.getValue().split(";");
                for(String sql:sqls){
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
                stmt.clearBatch();
            }
            stmt.execute("UPDATE research_dbversion SET version = "+getLatestVersion()+" , create_time = '"+ DateUtil.now()+"'");
            log.info("【数据库初始化】版本号:"+getLatestVersion());
            return true;
        }catch (Exception e){
            log.error("数据库batch失败！"+e.getMessage());
        }
        return false;
    }

    @Override
    public void close() throws SQLException{
        if(null != conn){
            conn.close();
            conn = null;
        }
        if(null != stmt){
            stmt.close();
            stmt = null;
        }
    }
}
