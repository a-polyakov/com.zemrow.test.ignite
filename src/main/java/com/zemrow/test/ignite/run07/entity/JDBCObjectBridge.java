package com.zemrow.test.ignite.run07.entity;

import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.configuration.CacheConfiguration;

import javax.cache.configuration.FactoryBuilder;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 *
 * @author Alexandr Polyakov
 */
public abstract class JDBCObjectBridge<K, T> implements Serializable {

    static final long serialVersionUID = 1L;

    public abstract String getTableName();

    public abstract int getColumnCount();

    public abstract String getColumnName(int columnIndex);

    public String getSqlSelectAll() {
        return "select * from " + getTableName();
    }

    public String getSqlSelect() {
        return getSqlSelectAll() + " where id=?";
    }

    public abstract T get(ResultSet rs) throws SQLException;

    public abstract void set(PreparedStatement ps, boolean isInsert, T entity) throws SQLException;

    public String getSqlUpdate() {
        final StringBuilder result = new StringBuilder();
        result.append("update ");
        result.append(getTableName());
        result.append(" set ");
        for (int i = 1; i < getColumnCount(); i++) {
            result.append(getColumnName(i));
            result.append(" = ?");
            if (i < getColumnCount() - 1) {
                result.append(", ");
            }
        }
        result.append(" where id = ? ");
        return result.toString();
    }

    public String getSqlInsert() {
        final StringBuilder result = new StringBuilder();
        result.append("insert into ");
        result.append(getTableName());
        result.append(" (");
        for (int i = 0; i < getColumnCount(); i++) {
            result.append(getColumnName(i));
            if (i < getColumnCount() - 1) {
                result.append(", ");
            }
        }
        result.append(") VALUES (");
        for (int i = 0; i < getColumnCount(); i++) {
            result.append("?");
            if (i < getColumnCount() - 1) {
                result.append(", ");
            }
        }
        result.append(")");
        return result.toString();
    }

    public String getSqlDelete() {
        return "delete from " + getTableName() + " where id=?";
    }

    // IGNITE
    public CacheConfiguration<K, T> getCacheConfiguration() {
        final CacheStore<K, T> store = new CacheJdbcObjectStore(this);
        final CacheConfiguration<K, T> cacheCfg = new CacheConfiguration<>(getTableName());
        cacheCfg.setCacheStoreFactory(new FactoryBuilder.SingletonFactory<>(store));
        cacheCfg.setReadThrough(true);
        // запись данных будет дублироваться в БД
        cacheCfg.setWriteThrough(true);
        // запись будет производится по накоплению изменений в кеше
        cacheCfg.setWriteBehindEnabled(true);
        // данные будут сброшены в БД в течении 1 минуты
        cacheCfg.setWriteBehindFlushFrequency(60000);
        // даные не потеряются если из кластера одновременно отключить 1 ноду
        cacheCfg.setBackups(1);
        cacheCfg.setIndexedTypes(UUID.class, Relation.class);
        return cacheCfg;
    }
}
