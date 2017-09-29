package com.zemrow.test.ignite.run07.entity;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.UUID;

/**
 * @author Alexandr Polyakov
 */
public class Relation extends JDBCObject<UUID> {

    @QuerySqlField(index = true)
    private Long base;

    @QuerySqlField(index = true)
    private Long reference;

    public Relation(UUID uuid, Long base, Long reference) {
        super(uuid);
        this.base = base;
        this.reference = reference;
    }

    public Long getBase() {
        return base;
    }

    public void setBase(Long base) {
        this.base = base;
    }

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Relation{");
        sb.append("id=").append(getId());
        sb.append(", base=").append(base);
        sb.append(", reference=").append(reference);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        if (!super.equals(o)) return false;

        final Relation that = (Relation) o;

        if (base != null ? !base.equals(that.base) : that.base != null)
            return false;
        return reference != null ? reference.equals(that.reference) : that.reference == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (base != null ? base.hashCode() : 0);
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        return result;
    }
}
