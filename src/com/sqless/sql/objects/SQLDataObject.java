package com.sqless.sql.objects;

import com.sqless.utils.SQLUtils;
import java.util.List;

/**
 * A class that represents {@code SQLObjects} that are comprised of columns such
 * as tables and views and contain data inside.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public abstract class SQLDataObject extends SQLObject implements SQLSelectable {

    private List<SQLColumn> columns;
    private List<SQLIndex> indexes;
    private List<SQLTrigger> triggers;
    private SQLPrimaryKey primaryKey;

    public SQLDataObject(String name) {
        super(name);
    }

    public SQLDataObject(String name, List<SQLColumn> columns) {
        super(name);
        setColumns(columns);
    }

    public void setColumns(List<SQLColumn> columns) {
        for (SQLColumn column : columns) {
            column.setParentTable(this);
        }
        this.columns = columns;
        primaryKey = new SQLPrimaryKey();
    }

    public void replaceColumns(List<SQLColumn> columns) {
        this.columns = columns;
    }

    public SQLColumn getColumn(String name) {
        for (SQLColumn column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    public SQLColumn getColumn(int index) {
        return columns.get(index);
    }

    public int getColumnIndex(SQLColumn column) {
        return column.getOrdinalPosition() - 1;
    }

    public SQLColumn findClosestNonBrandNewColumnOf(SQLColumn column) {
        int colIndex = getColumnIndex(column);
        if (colIndex == -1) {
            return null;
        }
        if (colIndex == 0) {
            return column;
        }
        for (int i = colIndex - 1; i >= 0; i--) {
            SQLColumn prevCol = getColumn(i);
            if (!prevCol.isBrandNew()) {
                return prevCol;
            }
        }
        return column;
    }

    public void loadColumns() {
        setColumns(SQLUtils.getColumnData(this));
    }

    public List<SQLColumn> getColumns() {
        return columns;
    }

    public void addPrimaryKey(SQLPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean columnIsPK(SQLColumn column) {
        for (SQLColumn pkColumn : primaryKey.getPkColumns()) {
            if (column.getName().equals(pkColumn.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Agrega una columna al final de la tabla.
     *
     * @param column
     */
    public void addColumn(SQLColumn column) {
        columns.add(column);
        column.setParentTable(this);
        column.setOrdinalPosition(getColumnCount());
        column.setFirstTimeChangeStatement();
    }

    public void insertColumn(int pos, SQLColumn column) {
        for (int i = pos; i < columns.size(); i++) {
            SQLColumn col = columns.get(i);
            col.setOrdinalPosition(col.getOrdinalPosition() + 1);
        }
        column.setParentTable(this);
        column.setOrdinalPosition(pos + 1);
        columns.add(pos, column);
        column.setFirstTimeChangeStatement();
    }

    public void removeColumn(int pos) {
        for (int i = pos + 1; i < columns.size(); i++) {
            SQLColumn col = columns.get(i);
            col.setOrdinalPosition(col.getOrdinalPosition() - 1);
        }
        columns.remove(pos);
    }

    public int getColumnCount() {
        return columns.size();
    }

    public void loadIndexes() {
        indexes = SQLUtils.getIndexes(this);
    }

    public List<SQLIndex> getIndexes() {
        return indexes;
    }

    public void loadTriggers() {
        triggers = SQLUtils.getTriggers(this);
    }

    public List<SQLTrigger> getTriggers() {
        return triggers;
    }

    public SQLPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public String getRetrieveTriggersStatement() {
        return "SHOW TRIGGERS WHERE `Table` = '" + getName() + "'";
    }

    public String getRetrieveIndexesStatement() {
        return "SHOW INDEX FROM " + getName(true);
    }

    public String getRetrieveColumnDataStatement() {
        return "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '" + SQLUtils.getConnectedDBName()
                + "' AND TABLE_NAME = '" + getName() + "'";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getSelectStatement(int limit) {
        return "SELECT * FROM `" + getName() + "`" + (limit == SQLSelectable.ALL ? "" : " LIMIT " + limit);
    }

}
