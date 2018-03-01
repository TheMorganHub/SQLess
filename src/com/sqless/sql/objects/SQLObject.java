package com.sqless.sql.objects;

import com.sqless.utils.SQLUtils;

/**
 * All the objects that belong to a SQL engine inherit from this class. This
 * class encloses common behaviour shown by said objects.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public abstract class SQLObject {

    private String name;

    public SQLObject(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this {@code SQLObject}.
     *
     * @param sqlify Whether to wrap the name around '` `' if it's comprised
     * of more than 1 word separated by white space.
     * @return if {@code sqlify == true} and for example the name is
     * {@code David Database}, this method will return '`David Database`',
     * compatible with SQL query format. Otherwise, this method will return the
     * name without any sort of formatting.
     */
    public String getName(boolean sqlify) {
        return sqlify ? SQLUtils.sqlify(name) : name;
    }
    
    public String getName() {
        return getName(false);
    }

    public void rename(String newName) {
        name = newName;
    }

    @Override
    public abstract String toString();
}
