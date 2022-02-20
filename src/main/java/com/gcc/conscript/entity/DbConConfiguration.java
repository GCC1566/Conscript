package com.gcc.conscript.entity;

import com.gcc.conscript.enumerate.DbType;
import lombok.Data;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class DbConConfiguration {

    private String url;

    private String host;

    private String dbPort;

    private DbType dbType;

    private String dbName;

    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String user;

    private String password;

    private String dbConfigFileUrl = "mysql-dbconfig.json";

    public String getHost(){
        if(host.isEmpty()) {
            String[] temp = getHostAndPort(url).split(":");
            return temp[0];
        }else {
            return host;
        }
    }

    public String getDbport(){
        if(dbPort.isEmpty()) {
            String[] temp = getHostAndPort(url).split(":");
            return temp[1];
        }else {
            return dbPort;
        }
    }

    private void setHost(String host) {
        this.host = host;
    }

    private void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setDbtype(DbType dbtype) {
        this.dbType = dbtype;
    }


    private void setDbname(String dbname) {
        this.dbName = dbname;
    }

    private void setDriverclassname(String driverclassname) {
        this.driverClassName = driverclassname;
    }

    private void setUser(String user) {
        this.user = user;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setDbconfigfileurl(String dbconfigfileurl) {
        this.dbConfigFileUrl = dbconfigfileurl;
    }

    private String getHostAndPort(String url){
        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
        Matcher m = p.matcher(url);
        while(m.find()) {
            return m.group(1)+":"+m.group(2);
        }
        return ":";
    }

    public static class Builder {
        private DbConConfiguration dbConConfiguration = new DbConConfiguration();

        public Builder setDbtype(String dbtype) {
            this.dbConConfiguration.setDbtype(DbType.StringValue(dbtype.toLowerCase()));
            return this;
        }

        public Builder setUrl(String url){
            this.dbConConfiguration.setUrl(url);
            return this;
        }

        public Builder setHost(String host) {
            this.dbConConfiguration.setHost(host);
            return this;
        }

        public Builder setDbPort(String dbPort) {
            this.dbConConfiguration.setDbPort(dbPort);
            return this;
        }

        public Builder setDbname(String dbname) {
            this.dbConConfiguration.setDbname(dbname);
            return this;
        }

        public Builder setDriverclassname(String driverclassname) {
            this.dbConConfiguration.setDriverclassname(driverclassname);
            return this;
        }

        public Builder setUser(String username) {
            this.dbConConfiguration.setUser(username);
            return this;
        }

        public Builder setPassword(String password) {
            this.dbConConfiguration.setPassword(password);
            return this;
        }

        public Builder setConfigFileUrl(String confileurl) {
            this.dbConConfiguration.setDbconfigfileurl(confileurl);
            return this;
        }

        public DbConConfiguration build(){
            return dbConConfiguration;
        }
    }
}
