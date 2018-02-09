package com.sqless.sql.objects;

/**
 * This class is used to represent a procedure or function parameter. In SQL, a
 * parameter isn't considered an object per se; it cannot be dropped, modified,
 * renamed, etc, and that is why this class does not extend {@code SQLObject}.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SQLParameter {

    private String name;
    private String type;

    public SQLParameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

}
