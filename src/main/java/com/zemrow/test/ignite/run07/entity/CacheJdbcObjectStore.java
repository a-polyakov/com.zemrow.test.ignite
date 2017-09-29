package com.zemrow.test.ignite.run07.entity;

import com.zemrow.test.ignite.MyDataSource;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.Serializable;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexandr Polyakov
 */
public class CacheJdbcObjectStore<K, T extends JDBCObject<K>> implements CacheStore<K, T>, Serializable {

    static final long serialVersionUID = 1L;

    /**
     * Auto-injected store session.
     */
    @CacheStoreSessionResource
    private transient CacheStoreSession ses;

    private JDBCObjectBridge<K, T> jdbcObjectBridge;

    public CacheJdbcObjectStore(JDBCObjectBridge<K, T> jdbcObjectBridge) {
        this.jdbcObjectBridge = jdbcObjectBridge;
    }

    // Complete transaction or simply close connection if there is no transaction.
    @Override
    public void sessionEnd(boolean commit) {
        try (Connection conn = ses.attachment()) {
            if (conn != null && ses.isWithinTransaction()) {
                if (commit)
                    conn.commit();
                else
                    conn.rollback();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to end store session.", e);
        }
    }

    // This mehtod is called whenever "get(...)" methods are called on IgniteCache.
    @Override
    //TODO
    public T load(K key) {
        try (final Connection conn = connection()) {
            try (final PreparedStatement st = conn.prepareStatement(jdbcObjectBridge.getSqlSelect())) {
                st.setObject(1, key);
                final ResultSet rs = st.executeQuery();
                return jdbcObjectBridge.get(rs);
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to load: " + key, e);
        }
    }

    // This mehtod is called whenever "getAll(...)" methods are called on IgniteCache.
    @Override
    //TODO
    public Map<K, T> loadAll(Iterable<? extends K> keys) {
        try (final Connection conn = connection()) {
            try (final PreparedStatement st = conn.prepareStatement(jdbcObjectBridge.getSqlSelect())) {
                final Map<K, T> loaded = new HashMap<>();
                for (final K key : keys) {
                    st.setObject(1, key);
                    try (ResultSet rs = st.executeQuery()) {
                        final T entity = jdbcObjectBridge.get(rs);
                        if (entity != null) {
                            loaded.put(key, entity);
                        }
                    }
                }
                return loaded;
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to loadAll: " + keys, e);
        }
    }

    // This mehtod is called whenever "put(...)" methods are called on IgniteCache.
    @Override
    public void write(Cache.Entry<? extends K, ? extends T> entry) {
        try (final Connection conn = connection()) {
            // Syntax of MERGE statement is database specific and should be adopted for your database.
            // If your database does not support MERGE statement then use sequentially update, insert statements.
            try (final PreparedStatement stUpdate = conn.prepareStatement(jdbcObjectBridge.getSqlUpdate())) {
                try (final PreparedStatement stInsert = conn.prepareStatement(jdbcObjectBridge.getSqlInsert())) {
                    jdbcObjectBridge.set(stUpdate, false, entry.getValue());
                    final int resultUpdate = stUpdate.executeUpdate();
                    if (resultUpdate == 0) {
                        jdbcObjectBridge.set(stInsert, true, entry.getValue());
                        stInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to write [key=" + entry.getKey() + ", val=" + entry.getValue() + ']', e);
        }
    }


    // This mehtod is called whenever "putAll(...)" methods are called on IgniteCache.
    @Override
    public void writeAll(Collection<Cache.Entry<? extends K, ? extends T>> entries) {
        try (final Connection conn = connection()) {
            // Syntax of MERGE statement is database specific and should be adopted for your database.
            // If your database does not support MERGE statement then use sequentially update, insert statements.
            try (final PreparedStatement stUpdate = conn.prepareStatement(jdbcObjectBridge.getSqlUpdate())) {
                try (final PreparedStatement stInsert = conn.prepareStatement(jdbcObjectBridge.getSqlInsert())) {
                    for (Cache.Entry<? extends K, ? extends T> entry : entries) {
                        jdbcObjectBridge.set(stUpdate, false, entry.getValue());
                        stUpdate.addBatch();
                    }
                    final int[] resultUpdate = stUpdate.executeBatch();
                    int i = 0;
                    for (Cache.Entry<? extends K, ? extends T> entry : entries) {
                        if (resultUpdate[i] == 0) {
                            jdbcObjectBridge.set(stInsert, true, entry.getValue());
                            stInsert.addBatch();
                        }
                        i++;
                    }
                    stInsert.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to writeAll: " + entries, e);
        }
    }

    // This mehtod is called whenever "remove(...)" methods are called on IgniteCache.
    @Override
    public void delete(Object key) {
        try (final Connection conn = connection()) {
            try (final PreparedStatement st = conn.prepareStatement(jdbcObjectBridge.getSqlDelete())) {
                st.setObject(1, key);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to delete: " + key, e);
        }
    }

    // This mehtod is called whenever "removeAll(...)" methods are called on IgniteCache.
    @Override
    public void deleteAll(Collection keys) {
        try (final Connection conn = connection()) {
            try (final PreparedStatement st = conn.prepareStatement(jdbcObjectBridge.getSqlDelete())) {
                for (Object key : keys) {
                    st.setObject(1, key);
                    st.addBatch();
                }
                st.executeBatch();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to deleteAll: " + keys, e);
        }
    }

    // This mehtod is called whenever "loadCache()" and "localLoadCache()"
    // methods are called on IgniteCache. It is used for bulk-loading the cache.
    // If you don't need to bulk-load the cache, skip this method.
    @Override
    public void loadCache(IgniteBiInClosure<K, T> clo, Object... args) {
        System.out.println("CacheJdbcObjectStore.loadCache");
        try (final Connection conn = connection()) {
            try (final PreparedStatement st = conn.prepareStatement(jdbcObjectBridge.getSqlSelectAll())) {
                try (final ResultSet rs = st.executeQuery()) {
                    T jdbcObject = jdbcObjectBridge.get(rs);
                    while (jdbcObject != null) {
                        clo.apply(jdbcObject.getId(), jdbcObject);
                        jdbcObject = jdbcObjectBridge.get(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }
    }


    // Opens JDBC connection and attaches it to the ongoing
    // session if within a transaction.
    protected Connection connection() throws SQLException {
        if (ses.isWithinTransaction()) {
            Connection conn = ses.attachment();
            if (conn == null) {
                conn = openConnection(false);
                // Store connection in the session, so it can be accessed
                // for other operations within the same transaction.
                ses.attach(conn);
            }
            return conn;
        } else {
            // Transaction can be null in case of simple load or put operation.
            return openConnection(true);
        }
    }

    // Opens JDBC connection.
    protected Connection openConnection(boolean autocommit) throws SQLException {
        final Connection conn = MyDataSource.getConnection();
        conn.setAutoCommit(autocommit);
        return conn;
    }
}