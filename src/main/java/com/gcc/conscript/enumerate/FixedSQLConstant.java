package com.gcc.conscript.enumerate;

/**
 * 数据库基础结构
 * @author GCC
 */
public class FixedSQLConstant {


    public static final String DB_VERSION_NAME = "dbversion";


    public static final String DB_VERSION_CONTENT = "DROP TABLE IF EXISTS `dbversion`;\n" +
            "CREATE TABLE `dbversion`  (\n" +
            "`id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "`version` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,\n" +
            "`create_time` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,\n" +
            "PRIMARY KEY (`id`) USING BTREE\n" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;\n"+
            "INSERT INTO `dbversion` VALUES ('1',1.0,'2021-12-01 08:00:00') ";

}
