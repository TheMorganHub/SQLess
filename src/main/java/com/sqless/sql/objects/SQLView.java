package com.sqless.sql.objects;

public class SQLView extends SQLDataObject implements SQLDroppable {

    public SQLView(String name) {
        super(name);
    }

    @Override
    public String getDropStatement() {
        return "DROP VIEW `" + getName() + "`";
    }

}
