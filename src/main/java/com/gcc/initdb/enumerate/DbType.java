package com.gcc.initdb.enumerate;


/**
 * 数据库类型枚举
 * @author GCC
 */
public enum DbType {

    DEFAULT("mysql","com.gcc.initdb.mysql.MySqlDataBase"),

    MYSQL("mysql","com.gcc.initdb.mysql.MySqlDataBase");

    private String dbtype;

    private String classvalue;

    DbType(String dbtype, String classvalue){
        this.dbtype = dbtype;
        this.classvalue = classvalue;
    }

    public String getDbtype() {
        return dbtype;
    }

    public String getClassvalue() {
        return classvalue;
    }


    public static DbType StringValue(String type){
        for(DbType value:DbType.values()){
            if(value.getDbtype().equals(type)){
                return value;
            }
        }
        return DbType.DEFAULT;
    }

}
