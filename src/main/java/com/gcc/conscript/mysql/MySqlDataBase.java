package com.gcc.conscript.mysql;

import cn.hutool.core.date.DateUtil;
import com.gcc.conscript.AbstractInitDataBase;
import com.gcc.conscript.entity.DbConConfiguration;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.Map;

import static com.gcc.conscript.enumerate.FixedSQLConstant.DB_VERSION_NAME;

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
            Class.forName(dbConConfiguration.getDriverClassName());
            String jdbc_url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/mysql?characterEncoding=utf8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(jdbc_url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            String url ="jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/mysql?user="+dbConConfiguration.getUser()+"&password="+dbConConfiguration.getPassword()+"&serverTimezone=GMT%2B8";
            driver = DriverManager.getDriver(url);
            if(conn != null){
                return true;
            }
        }catch (Exception e){
           log.error("【Conscript】Database initialization ：data base is not connection.....");
        }
        return false;
    }

    @Override
    public boolean databaseIsExitd() throws SQLException {
        try {
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name= \""+dbConConfiguration.getDbName()+"\"");
            if(res.next() && res.getInt(1) == 0){
                stmt.execute("CREATE DATABASE IF NOT EXISTS "+dbConConfiguration.getDbName()+" DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci");
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
            log.error("【Conscript】Database initialization ：database base query is error");
        }
        return false;
    }


    @Override
    public Float getCurrenDbVersion() throws SQLException {
        float version = 0f;
        try {
            String url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/"+dbConConfiguration.getDbName()+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT version FROM "+ DB_VERSION_NAME);
            if(res.next()){
                version = Float.parseFloat(res.getString("version"));
            }else{
                log.error("【Conscript】Database initialization ：Database messed up, dbversion data source missing");
            }
        }catch (Exception e){
            log.error("【Conscript】Database initialization ：Missing required information for base version number："+e.getMessage());
        }finally {
            conn.close();
            stmt.close();
        }
        return version;
    }

    @Override
    public boolean excuteSQL(Map<String,String> sqlcontent) throws SQLDataException{
        try {
            String url = "jdbc:mysql://"+dbConConfiguration.getHost()+":"+dbConConfiguration.getDbport()+"/"+dbConConfiguration.getDbName()+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url,dbConConfiguration.getUser(),dbConConfiguration.getPassword());
            stmt = conn.createStatement();
            for (Map.Entry<String,String> entry : sqlcontent.entrySet()) {
                log.info("【Conscript】Database initialization ：Upgrading sql file ——>" + entry.getKey());
                String[] sqls = entry.getValue().split(";");
                for(String sql:sqls){
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
                stmt.clearBatch();
            }
            updateDbVersion();
           return true;
        }catch (Exception e){
            log.error("【Conscript】Database initialization ：DataBase batch fail！"+e.getMessage());
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


    @Override
    public void updateDbVersion() throws SQLException {
        stmt.execute("UPDATE "+DB_VERSION_NAME+" SET version = "+getLatestVersion()+" , create_time = '"+ DateUtil.now()+"'");
        log.info("【Conscript】Database initialization ：version:"+getLatestVersion());

    }

}
