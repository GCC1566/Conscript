package com.gcc.initdb.entity;

import com.gcc.initdb.enumerate.DbType;
import lombok.Data;

@Data
public class DbConConfiguration {

    private String url;

    private DbType dbtype;

    private String host;

    private String dbport;

    private String dbname;

    private String driverclassname;

    private String user;

    private String password;

    private String dbconfigfileurl;

    public String getUrl() {
        url = "jdbc:mysql://"+this.host+":"+this.dbport+"/"+this.dbname+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setDbtype(DbType dbtype) {
        this.dbtype = dbtype;
    }

    private void setHost(String host) {
        this.host = host;
    }

    private void setDbport(String dbport) {
        this.dbport = dbport;
    }

    private void setDbname(String dbname) {
        this.dbname = dbname;
    }

    private void setDriverclassname(String driverclassname) {
        this.driverclassname = driverclassname;
    }

    private void setUser(String user) {
        this.user = user;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setDbconfigfileurl(String dbconfigfileurl) {
        this.dbconfigfileurl = dbconfigfileurl;
    }

    public static class Builder {
        private DbConConfiguration dbConConfiguration = new DbConConfiguration();

        public Builder setDbtype(String dbtype) {
            this.dbConConfiguration.setDbtype(DbType.StringValue(dbtype.toLowerCase()));
            return this;
        }

        public Builder setHost(String host) {
            this.dbConConfiguration.setHost(host);
            return this;
        }

        public Builder setDbport(String dbport) {
            this.dbConConfiguration.setDbport(dbport);
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
