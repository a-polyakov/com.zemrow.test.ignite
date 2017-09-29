package com.zemrow.test.ignite.run07.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 *
 *
 *
 * @author Alexandr Polyakov
 */
public class ProfileBridge extends JDBCObjectBridge<Long, Profile> {

    private static String column[] = new String[]{"id", "name"};

    @Override
    public String getTableName() {
        return "profile";
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
    public Profile get(ResultSet rs) throws SQLException {
        return rs.next() ? new Profile(rs.getLong(1), rs.getString(2)) : null;
    }

    @Override
    public void set(PreparedStatement ps, boolean isInsert, Profile person) throws SQLException {
        int i;
        if (isInsert) {
            i = 2;
        } else {
            i = 1;
        }
        ps.setString(i++, person.getName());
        if (isInsert) {
            i = 1;
        }
        ps.setLong(i, person.getId());
    }
}
