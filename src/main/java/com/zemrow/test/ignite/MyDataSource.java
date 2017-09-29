package com.zemrow.test.ignite;


import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Alexandr Polyakov
 */
public class MyDataSource {

    private static final HikariDataSource dataSource;

    static {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/ignite");
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(100);
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setIdleTimeout(10000);
        dataSource.setLeakDetectionThreshold(3000);
        dataSource.setConnectionTimeout(2000);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
