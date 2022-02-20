package com.gcc.conscript.enumerate;


/**
 * 数据库类型枚举
 * @author GCC
 */
public enum DbType {

    DEFAULT("mysql","com.gcc.conscript.mysql.MySqlDataBase"),

    MYSQL("mysql","com.gcc.conscript.mysql.MySqlDataBase");

    private String dbType;

    private String classValue;

    DbType(String dbType, String classValue){
        this.dbType = dbType;
        this.classValue = classValue;
    }

    public String getDbType() {
        return dbType;
    }

    public String getClassValue() {
        return classValue;
    }


    public static DbType StringValue(String type){
        for(DbType value:DbType.values()){
            if(value.getDbType().equals(type)){
                return value;
            }
        }
        return DbType.DEFAULT;
    }

}
