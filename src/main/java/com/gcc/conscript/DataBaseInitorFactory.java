package com.gcc.conscript;

import com.gcc.conscript.entity.DbConConfiguration;
import com.gcc.conscript.enumerate.DbType;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Constructor;

/**
 * 数据库初始器工厂
 * @author GCC
 * @date 2021-12-01
 */
@Slf4j
public class DataBaseInitorFactory {

    /**
     * 创建初始器
     * @param config 连接配置信息
     * @return InitDataBase
     */
    public static InitDataBase createInitiator(DbConConfiguration config){
        try{
            Class<?> clazz = Class.forName(getClassUrlValue(config.getDbType()));
            Constructor c = clazz.getConstructor(DbConConfiguration.class);
            AbstractInitDataBase initiator = (AbstractInitDataBase) c.newInstance(config);
            return initiator;
        }catch (Exception e){
            log.error("create object fail！"+e);
        }
        return null;
    }

    /**
     * 可重写该类
     * @param dbType 数据库类型
     * @return String
     */
    public static String getClassUrlValue(DbType dbType){
        return dbType.getClassValue();
    }

}
