package com.ip241k.skoryk.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static DataSource dataSource;

    static {
        try {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setURL("jdbc:mysql://db:3306/javaSpringLR");
            mysqlDataSource.setUser("root");
            mysqlDataSource.setPassword("12345678");
            mysqlDataSource.setServerTimezone("UTC");
            
            dataSource = mysqlDataSource;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}