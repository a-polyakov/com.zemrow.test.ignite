package com.zemrow.test.ignite.run07.entity;

/**
 *
 *
 * @author Alexandr Polyakov
 */
public class Profile extends JDBCObject<Long> {

    private String name;

    public Profile(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Profile{");
        sb.append("id=").append(getId());
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;
        if (!super.equals(o)) return false;
        final Profile profile = (Profile) o;
        return name != null ? name.equals(profile.name) : profile.name == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
