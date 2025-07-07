package com.freeuni.quiz.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/db21?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tasobaso123321.";
    //private static final String PASSWORD = "balibarcelona13";
    //private static final String PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";



    public static DataSource createDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        ds.setDriverClassName(DRIVER);
        return ds;
    }
}
