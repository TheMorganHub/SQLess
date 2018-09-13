package com.sqless.sql.objects;

import java.util.ArrayList;
import java.util.List;

public class SQLPrimaryKey implements SQLEditable, SQLDroppable {

    private List<SQLColumn> pkColumns;
    private List<SQLColumn> pkColumnsBackup;

    public SQLPrimaryKey(List<SQLColumn> columns) {
        pkColumns = columns;
        pkColumnsBackup = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            pkColumnsBackup.add(columns.get(i));
        }
    }

    public SQLPrimaryKey() {
        this(new ArrayList<>());
    }

    @Override
    public void commit(String... args) {
        pkColumnsBackup.clear();
        for (SQLColumn pkColumn : pkColumns) {
            pkColumnsBackup.add(pkColumn);
        }
    }

    public boolean hasAutoIncrementColumn() {
        for (SQLColumn pkColumn : pkColumns) {
            if (pkColumn.isAutoincrement()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Agrega una columna a esta definición de PK. Si la columna es nullable,
     * dejará de serlo.
     *
     * @param column
     */
    public void addColumn(SQLColumn column) {
        if (column.isNullable()) {
            column.setNullable(false);
            column.evaluateUncommittedChanges();
        }
        pkColumns.add(column);
    }

    /**
     * Remueve una columna de la definición de esta PK. La columna se mantendrá
     * como no nullable lo sea o no antes de haber sido asignada como PK. Si la
     * columna fue auto increment al momento de remover la PK, la columna dejará
     * de serlo.
     *
     * @param column
     */
    public void removeColumn(SQLColumn column) {
        pkColumns.remove(column);
        if (column.isAutoincrement()) {
            column.setAutoincrement(false, true);
        }
    }

    public List<SQLColumn> getPkColumns() {
        return pkColumns;
    }

    public int getSize() {
        return pkColumns.size();
    }

    public boolean mustNotDrop() {
        //cuando no habia columnas PK viejas y solo se crearon nuevas
        if (pkColumnsBackup.isEmpty() && pkColumns.size() > 0) {
            return true;
        }
        //cuando la lista de backup y la lista de PK columns son exactamente iguales en términos de nombre de columna y tipo de dato
        //esto quiere decir que la persona borro una PK y se arrepintió y la volvió a agregar.
        //en ese caso no hay que hacer nada.
        if (pkColumnsBackup.size() == pkColumns.size()) {
            boolean equalLists = true;
            for (int i = 0; i < pkColumns.size(); i++) {
                if (!pkColumns.get(i).getUncommittedName().equals(pkColumnsBackup.get(i).getUncommittedName())
                        || !pkColumns.get(i).getDataType().equals(pkColumnsBackup.get(i).getDataType())
                        || pkColumns.get(i).isBrandNew()) {
                    equalLists = false;
                }
            }
            if (equalLists) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChanged() {
        //cuando el tamaño del backup y las columnas PK actuales es distinto es obvio que cambió
        if (pkColumns.size() != pkColumnsBackup.size()) {
            return true;
        }

        //cuando alguna PK en el backup no puede ser encontrada en las PKs actuales
        for (int i = 0; i < pkColumnsBackup.size(); i++) {
            boolean found = false;
            for (int j = 0; j < pkColumns.size(); j++) {
                if (pkColumnsBackup.get(i).getUncommittedName().equals(pkColumns.get(j).getUncommittedName())
                        && pkColumnsBackup.get(i).getDataType().equals(pkColumns.get(j).getDataType())
                        && (pkColumnsBackup.get(i).isBrandNew() == pkColumns.get(j).isBrandNew())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the index of a column within this PK. Example, if a PK is formed
     * with ID and NAME, and the person calls this method and passes a String
     * "name", this method will return 1.
     *
     * @param colName
     * @return
     */
    public int getIndexOfColumn(String colName) {
        for (int i = 0; i < pkColumns.size(); i++) {
            if (pkColumns.get(i).getName().equals(colName)) {
                return i;
            }
        }
        return -1;
    }

    public String getAddPKsStatement(boolean addKeyword) {
        if (isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder((addKeyword ? "ADD " : "") + "PRIMARY KEY (");
        int pkCount = 0;
        for (int i = 0; i < pkColumns.size(); i++) {
            builder.append(pkCount++ > 0 ? ", " : "").append("`").append(pkColumns.get(i).getUncommittedName()).append("`");
        }
        builder.append(")").append(addKeyword ? ";" : "");
        return builder.toString();
    }

    @Override
    public String getDropStatement() {
        return "DROP PRIMARY KEY" + (isEmpty() ? "" : ",");
    }

    public boolean isEmpty() {
        return pkColumns.isEmpty();
    }

    @Override
    public String toString() {
        return "" + pkColumns;
    }

}
