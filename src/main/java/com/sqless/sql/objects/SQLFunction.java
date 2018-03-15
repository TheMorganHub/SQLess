package com.sqless.sql.objects;

public class SQLFunction extends SQLExecutable {

    public SQLFunction(String name) {
        super(name);
    }

    @Override
    public String getCallStatement() {
        return "SELECT " + getName() + "(" + prepareParameters() + ")";
    }

}
