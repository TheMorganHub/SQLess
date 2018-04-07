package com.sqless.sql.objects;

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
    
    public String getName() {
        return name;
    }

    public void rename(String newName) {
        name = newName;
    }

    @Override
    public abstract String toString();
}
