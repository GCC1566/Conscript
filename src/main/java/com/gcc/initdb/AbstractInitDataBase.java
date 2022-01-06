package com.gcc.initdb;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gcc.initdb.entity.DbConConfiguration;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        DB_CONFIG_URL = conConfiguration.getDbconfigfileurl();
        dbConConfiguration = conConfiguration;
    }


    @Override
    public boolean isInitEd() {
        return flag;
    }

    @Override
    public void startCoreJob() throws SQLException {
        reloadConfigFile();
        log.info("【数据库初始化】开始基本数据库初始化");
        if(createConnection()){
            log.info("【数据库初始化】成功建立与数据库的联系");
            Map<String,String> sqlcontent;
            if(databaseIsExitd()) {
                //比对代码配置中所需数据库版本是否大于当前数据库中实际版本
                if(getLatestVersion() > getCurrenDbVersion()) {
                    log.info("【数据库初始化】当前数据库版本较低，进行数据库升级");
                    sqlcontent = getSqlFromFile(getCurrenDbVersion());
                }else {
                    log.info("【数据库初始化】当前数据库已是最新版本");
                    flag = true;
                    return;
                }
            }else{
                log.info("【数据库初始化】检验到本系统所需数据库不存在，开启自动建库流程");
                sqlcontent = getSqlFromFile(0f);
            }
            flag = excuteSQL(sqlcontent);
        }else{
            log.error("【数据库初始化】与数据库服务建立链接失败 ! 请确认数据库服务是否正常或配置是否正确！");
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
        if(ver == 0){
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
            String content = IoUtil.read (classPathResource.getStream(), Charset.defaultCharset());
            return content;
        }catch (Exception e){
            log.error("文件【"+url+"】读取失败！");
        }
        return null;
    }

}
