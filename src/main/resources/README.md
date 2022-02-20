# Conscript 数据库初始化工具

## 1.使用方式

### 1.引入Conscript

1.1.使用jar引入

```xml
<dependency>
    <groupId>com.gcc</groupId>
    <artifactId>Conscript</artifactId>
    <version>1.0</version>
</dependency>
```

1.2.引入源码

​	将com.gcc.initdb目录下的initdb包拷至代码中即可

### 2.建立SQL文件存放目录

​	1.在resource目录下新建sql目录，里面存放需要升级的sql文件

​	2.在resource目录下新建XX.json文件作为配置文件，XX.json文件样例如下：

mysql-dbconfig.json

```json
 [
   {
      "version": "1.0",
      "sqlfile": "a.sql",
      "desc": "基础数据库结构"
   },
    {
       "version": "1.1",
       "sqlfile": "ddd.sql",
       "desc": "第一版升级数据库"
    }
 ]
```

| 字段    | 含义                            |
| ------- | ------------------------------- |
| version | 数据库的版本                    |
| sqlfile | 数据库升级的sql文件，叠加式追加 |
| desc    | 维护使用的描述信息              |

### 3.构建DbConConfiguration对象

DbConConfiguration对象所需参数如下，均需必填

| 参数            | 意义                           |
| --------------- | ------------------------------ |
| host            | 数据库服务ip地址               |
| port            | 数据库服务端口                 |
| dbname          | 数据库名称（需要初始化的库名） |
| user            | 数据库连接账号                 |
| password        | 数据库连接密码                 |
| dbconfigfileurl | 数据库升级配置文件             |
| driverclassname | 数据库连接驱动名称             |
| dbtype          | 数据库类型                     |

### 4.使用初始器工厂创建数据库初始器

​	将DbConConfiguration对象作为DataBaseInitorFactory工厂方法的参数，生产数据库初始器对象，进行数据库初始化：

```java

public static void main(String args[]){
    DbConConfiguration conConfiguration = new DbConConfiguration.Builder()
                .setHost(cfgBean.getHost())
                .setDbport(cfgBean.getDbport())
                .setDbname(cfgBean.getDbname())
                .setConfigFileUrl("mysql-dbconfig.json")
             .setDriverclassname(cfgBean.getDriverclassname())
                .setDbtype(cfgBean.getDbtype().getDbtype())
                .setUser(cfgBean.getUser())
                .setPassword(cfgBean.getPassword())
                .build();
        InitDataBase initdb = DataBaseInitorFactory.createInitiator(conConfiguration);
        try {
            initdb.startCoreJob();
        }catch (SQLException e){
            log.error("数据库错误"+e.getMessage());
        }    

}
```

