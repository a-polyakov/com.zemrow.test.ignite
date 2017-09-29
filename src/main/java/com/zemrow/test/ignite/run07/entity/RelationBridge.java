package com.zemrow.test.ignite.run07.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Alexandr Polyakov
 */
public class RelationBridge extends JDBCObjectBridge<UUID, Relation> {

    private static String column[] = new String[]{"id", "base", "reference"};

    @Override
    public String getTableName() {
        return "relation";
    }

    @Override
    public int getColumnCount() {
        return column.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return column[columnIndex];
    }

    @Override
    public Relation get(ResultSet rs) throws SQLException {
        return rs.next() ? new Relation(
                // "id", "base", "reference",
                (UUID) rs.getObject(1), rs.getLong(2), rs.getLong(3)
        ) : null;
    }

    @Override
    public void set(PreparedStatement ps, boolean isInsert, Relation person) throws SQLException {
        int i;
        if (isInsert) {
            i = 2;
        } else {
            i = 1;
        }
        ps.setLong(i++, person.getBase());
        ps.setLong(i++, person.getReference());
        if (isInsert) {
            i = 1;
        }
        ps.setObject(i, person.getId());
    }
}
