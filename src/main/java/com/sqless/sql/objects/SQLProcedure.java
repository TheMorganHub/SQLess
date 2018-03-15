package com.sqless.sql.objects;

public class SQLProcedure extends SQLExecutable {

    public SQLProcedure(String name) {
        super(name);
    }

    @Override
    public String getCallStatement() {
        return "CALL " + getName() + "(" + prepareParameters() + ")";
    }
    
}
