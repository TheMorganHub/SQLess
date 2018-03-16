package com.sqless.sql.objects;

import com.sqless.utils.SQLUtils;
import java.util.List;

/**
 * A representation of a SQL Database with all its objects such as tables,
 * functions, views, etc.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SQLDatabase extends SQLObject implements SQLDroppable {

    private List<SQLTable> tables;
    private List<SQLView> views;
    private List<SQLExecutable> functions;
    private List<SQLExecutable> procedures;

    public SQLDatabase(String name) {
        super(name);
    }

    public void loadTables() {
        tables = SQLUtils.getTableData();
    }

    public void loadViews() {
        views = SQLUtils.getViews();
    }
    
    public void loadFunctions() {
        functions = SQLUtils.getExecutables(SQLFunction.class);
    }
    
    public void loadProcedures() {
        procedures = SQLUtils.getExecutables(SQLProcedure.class);
    }

    public List<SQLTable> getTables() {
        return tables;
    }

    public List<SQLView> getViews() {
        return views;
    }

    public List<SQLExecutable> getFunctions() {
        return functions;
    }

    public List<SQLExecutable> getProcedures() {
        return procedures;
    }

    /**
     * Looks for a SQL table or view in one of the lists of the connected DB.
     *
     * @param name the full name of the object, including its schema. E.g:
     * dbo.Employees.
     * @return a {@code SQLDataObject}, or {@code null} if no object is found
     * with that name.
     */
    public SQLDataObject getTableObjectByName(String name) {
        if (tables != null) { //if tables haven't been loaded yet
            for (SQLTable table : tables) {
                if (table.getName(true).equals(name)) {
                    return table;
                }
            }
        }

        if (views != null) { //if views haven't been loaded yet
            for (SQLView view : views) {
                if (view.getName(true).equals(name)) {
                    return view;
                }
            }
        }

        return null;
    }

    @Override
    public String getDropStatement() {
        return "DROP DATABASE " + getName() + ";";
    }

    @Override
    public String toString() {
        return getName();
    }

}