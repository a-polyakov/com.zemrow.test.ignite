package com.zemrow.test.ignite.run07.entity;

/**
 *
 *
 * @author Alexandr Polyakov
 */
public class JDBCObject<T> {
    private T id;

    public JDBCObject(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDBCObject)) return false;
        final JDBCObject that = (JDBCObject) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
