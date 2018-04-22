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
    private boolean isBrandNew;

    public SQLDatabase(String name) {
        super(name);
    }

    public SQLDatabase(String name, boolean isBrandNew) {
        super(name);
        this.isBrandNew = isBrandNew;
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

    public boolean isBrandNew() {
        return isBrandNew;
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
                if (table.getName().equals(name)) {
                    return table;
                }
            }
        }

        if (views != null) { //if views haven't been loaded yet
            for (SQLView view : views) {
                if (view.getName().equals(name)) {
                    return view;
                }
            }
        }

        return null;
    }

    public SQLTable getTableByName(String name) {
        if (tables != null) {
            for (SQLTable table : tables) {
                if (table.getName().equals(name)) {
                    return table;
                }
            }
        }
        return null;
    }

    @Override
    public String getDropStatement() {
        return "DROP DATABASE `" + getName() + "`";
    }

    public String getCharsetAndCollationStatement() {
        return "SELECT DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA\n"
                + "WHERE schema_name = '" + getName() + "'";
    }

    @Override
    public String toString() {
        return getName();
    }

}
