package com.gcc.conscript;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gcc.conscript.entity.DbConConfiguration;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gcc.conscript.enumerate.FixedSQLConstant.DB_VERSION_CONTENT;

/**
 * 抽象核心升级父类
 * 提供基本的执行方法及调度升级逻辑顺序
 * @author GCC
 */
@Slf4j
public abstract class AbstractInitDataBase implements InitDataBase {
    
    String DB_CONFIG_URL;

    public JSONArray dbconfig = new JSONArray();

    public DbConConfiguration dbConConfiguration;

    public static Boolean flag = false;

    public AbstractInitDataBase(DbConConfiguration conConfiguration){
        DB_CONFIG_URL = conConfiguration.getDbConfigFileUrl();
        dbConConfiguration = conConfiguration;
    }


    @Override
    public boolean isInitEd() {
        return flag;
    }

    @Override
    public void startCoreJob() throws SQLException {
        reloadConfigFile();
        log.info("【Conscript】Database initialization ：Start basic database initialization.");
        if(createConnection()){
            log.info("【Conscript】Database initialization ：Successfully established a connection to the database.");
            Map<String,String> sqlcontent;
            if(databaseIsExitd()) {
                //比对代码配置中所需数据库版本是否大于当前数据库中实际版本
                if(getLatestVersion() > getCurrenDbVersion()) {
                    log.info("【Conscript】Database initialization ：The current database version is lower, and the database is upgraded.");
                    sqlcontent = getSqlFromFile(getCurrenDbVersion());
                }else {
                    log.info("【Conscript】Database initialization ：The current database is the latest version.");
                    flag = true;
                    return;
                }
            }else{
                log.info("【Conscript】Database initialization ：It is verified that the database required by the system does not exist, and the automatic database creation process is started.");
                sqlcontent = getSqlFromFile(0f);
            }
            flag = excuteSQL(sqlcontent);
        }else{
            log.error("【Conscript】Database initialization ：The connection to the database server failed! Please confirm whether the database service is normal or the configuration is correct!");
        }
        close();
    }


    /**
     * 加载数据库升级相关配置
     */
    private void reloadConfigFile() {
        String content = readFileContent(DB_CONFIG_URL);
        if(null != content) {
            dbconfig = JSONUtil.parseArray(content);
        }
    }

    /**
     * 获取程序中最新版本号
     * @return Float
     */
    public Float getLatestVersion() {
        int item = dbconfig.size()-1;
        JSONObject json = dbconfig.getJSONObject(item);
        return Float.parseFloat(json.getStr("version"));
    }

    /**
     * 根据版本号获取需要执行的sql文件
     * @param ver 数据库内版本号
     * @return Map<String,String>
     */
    private Map<String,String> getSqlFromFile(Float ver) {
        Map<String,String> result =  new LinkedHashMap<>();
        List<String> filelist = getFileList(ver);
        for(String filename:filelist){
            if(0 == ver) {
                result.put("ConscriptBase", DB_VERSION_CONTENT);
            }
            result.put(filename,readFileContent("sql/"+filename));
        }
        return result;
    }

    /**
     * 根据版本号获取数据库升级文件
     * @param ver 库中版本号
     * @return List<String>
     */
    private List<String> getFileList(Float ver){
        List<String> result = new ArrayList<>();
        if(0 == ver){
            for(int i = 0;i < dbconfig.size();i++){
                JSONObject temp = dbconfig.getJSONObject(i);
                result.add(temp.getStr("sqlfile"));
            }
        }else{
            for(int index = 0;index < dbconfig.size();index++){
                JSONObject temp = dbconfig.getJSONObject(index);
                if(Float.parseFloat(temp.getStr("version")) > ver){
                    result.add(temp.getStr("sqlfile"));
                }
            }
        }
        return result;
    }

    /**
     * 根据路径获取配置文件内容
     * @param url 路径
     * @return String
     */
    private String readFileContent(String url){
        ClassPathResource classPathResource = new ClassPathResource(url);
        try{
            return IoUtil.read (classPathResource.getStream(), Charset.defaultCharset());
        }catch (Exception e){
            log.error("file【"+url+"】Read failed!");
        }
        return null;
    }


}
