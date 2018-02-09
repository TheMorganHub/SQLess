package com.sqless.sql.objects;

import com.sqless.utils.SQLUtils;

public class SQLIndex extends SQLObject {

    private String parentTable;
    private String indexType;

    public SQLIndex(String name, String indexType, String parentTable) {
        super(name);
        this.indexType = indexType;
        this.parentTable = parentTable;
    }
    
    public String getParentTable() {
        return parentTable;
    }

    @Override
    public String toString() {
        return getName() + " ("
                + indexType
                + ")";
    }

    @Override
    public String getDropStatement() {
        return "DROP INDEX " + parentTable + "." + getName(true);
    }

}
