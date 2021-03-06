package com.sqless.utils;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.sql.objects.*;
import com.sqless.ui.UIClient;
import com.sqless.ui.UIEditTable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;

/**
 * Utility class used to perform various tasks related to SQL database
 * manipulation and SQL Object data retrieval.
 * <p>
 * <b>Note:</b> many methods in this class mirror methods from
 * {@link SQLConnectionManager} for the sake of convenience.</p>
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SQLUtils {

    public static String[] getEnumLikeValuesAsArray(String enumLikeValues) {
        if (enumLikeValues == null) {
            return null;
        }
        String original = enumLikeValues;
        Pattern enumPattern = Pattern.compile("[^,']+");
        Matcher matcher = enumPattern.matcher(original);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches.toArray(new String[matches.size()]);
    }

    public static String translateErrorMessage(String errorMessage) {
        String translated = "";
        String lowercasedErrorMessage = errorMessage.toLowerCase();
        if (lowercasedErrorMessage.contains("syntax")) {
            translated = "Hubo un error de sintaxis en tu sentencia SQL. Por favor, verifica que estés escribiendo bien la sentencia.";
        } else if (lowercasedErrorMessage.contains("unknown") || lowercasedErrorMessage.contains("doesn't exist")) {
            if (lowercasedErrorMessage.contains("table")) {
                translated = "La sentencia que estás ejecutando está intentando utilizar una tabla que no existe.";
            } else if (lowercasedErrorMessage.contains("column")) {
                translated = "La sentencia que estás ejecutando está intentando utilizar una columna que no existe.";
            } else if (lowercasedErrorMessage.contains("database")) {
                translated = "La sentencia que estás ejecutando está intentando utilizar una base de datos que no existe.";
            }
        } else if (lowercasedErrorMessage.contains("communications link")) {
            translated = "Hubo un error al llevar a cabo la conexión con el servidor de base de datos. Por favor, revisa el host y el puerto; es probable que alguno de los dos sea incorrecto.";                    
        }
        
        if (lowercasedErrorMessage.contains("where clause")) {
            translated += "\nRevisa el condicional de tu sentencia.";
        }
        
        translated += "\nMensaje original desde el motor de la base de datos: " + errorMessage;
        return translated;
    }

    /**
     * Retrieves the names of all DBs in the DB engine. Each time this method is
     * called, the DB engine is queried for names. This guarantees that the list
     * returned by this method is always up-to-date.
     *
     * @return a {@code List} of type {@code String} with the names of all the
     * databases within the engine.
     */
    public static List<String> retrieveDBNamesFromServer() {
        List<String> dbNames = new ArrayList<>();

        SQLQuery dbNamesQuery = new SQLSelectQuery("SHOW DATABASES WHERE `Database` != 'mysql'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    dbNames.add(rs.getString(1));
                }
            }
        };
        dbNamesQuery.exec();
        return dbNames;
    }

    public static List<String> getTableNamesFromDB() {
        List<String> tableNames = new ArrayList<>();
        SQLQuery showTablesQuery = new SQLSelectQuery("show full tables where Table_Type = 'BASE TABLE' OR Table_Type = 'SYSTEM VIEW'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    tableNames.add(rs.getString(1));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer los nombres de las tablas desde el servidor.\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        showTablesQuery.exec();
        return tableNames;
    }

    public static List<String> getColumnsFromTableAsString(String tableName) {
        List<String> colNames = new ArrayList<>();
        SQLQuery getColumnsQuery = new SQLSelectQuery("SHOW COLUMNS FROM `" + getConnectedDBName() + "`.`" + tableName + "`") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    colNames.add(rs.getString("Field"));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer los nombres de las columnas desde el servidor.\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        getColumnsQuery.exec();
        return colNames;
    }

    public static List<SQLTable> getTableData() {
        List<SQLTable> tables = new ArrayList<>();
        SQLQuery getTableDataQuery = new SQLSelectQuery("show full tables where Table_Type = 'BASE TABLE' OR Table_Type = 'SYSTEM VIEW'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    tables.add(new SQLTable(rs.getString(1)));
                }
            }
        };
        getTableDataQuery.exec();
        return tables;
    }

    public static List<SQLView> getViews() {
        List<SQLView> views = new ArrayList<>();
        SQLQuery getViewsQuery = new SQLSelectQuery("SHOW FULL TABLES IN " + getConnectedDBName() + " WHERE TABLE_TYPE LIKE 'VIEW'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String name = rs.getString(1);
                    views.add(new SQLView(name));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer las vistas desde el servidor.\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        getViewsQuery.exec();
        return views;
    }

    public static List<SQLExecutable> getExecutables(Class<? extends SQLExecutable> className) {
        List<SQLExecutable> executables = new ArrayList<>();
        SQLQuery getFunctionsQuery = new SQLSelectQuery("SHOW" + (className == SQLFunction.class ? " FUNCTION " : " PROCEDURE ") + "STATUS WHERE Db = '" + getConnectedDBName() + "'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    executables.add(className == SQLFunction.class ? new SQLFunction(name) : new SQLProcedure(name));
                }
            }
        };
        getFunctionsQuery.exec();

        if (!executables.isEmpty()) {
            SQLQuery getParametersQuery = new SQLSelectQuery("SELECT SPECIFIC_NAME, PARAMETER_NAME, DATA_TYPE FROM information_schema.parameters\n"
                    + "WHERE SPECIFIC_SCHEMA = '" + getConnectedDBName() + "' AND ROUTINE_TYPE = '" + (className == SQLFunction.class ? "FUNCTION" : "PROCEDURE") + "' AND PARAMETER_NAME IS NOT NULL") {
                @Override
                public void onSuccess(ResultSet rs) throws SQLException {
                    String previousName = null;
                    SQLExecutable executable = null;
                    while (rs.next()) {
                        String specificName = rs.getString("SPECIFIC_NAME");
                        if (!specificName.equals(previousName)) {
                            for (SQLExecutable e : executables) {
                                if (e.getName().equals(specificName)) {
                                    executable = e;
                                    break;
                                }
                            }
                        }
                        String paramName = rs.getString("PARAMETER_NAME");
                        String dataType = rs.getString("DATA_TYPE");
                        if (executable != null) {
                            executable.addParameter(new SQLParameter(paramName, dataType));
                        }
                        previousName = specificName;
                    }
                }
            };
            getParametersQuery.exec();
        }
        return executables;
    }

    public static int getTableAutoIncrement(SQLTable table) {
        FinalValue<Integer> autoIncrement = new FinalValue<>(-1);

        SQLQuery getAutoIncrementQuery = new SQLSelectQuery("SELECT `AUTO_INCREMENT`\n"
                + "FROM  INFORMATION_SCHEMA.TABLES\n"
                + "WHERE TABLE_SCHEMA = '" + getConnectedDBName() + "'\n"
                + "AND   TABLE_NAME   = '" + table.getName() + "'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                autoIncrement.set(rs.next() ? rs.getInt("AUTO_INCREMENT") : 1);
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "Hubo un error al traer información del auto increment de la fila.\nEl servidor respondió con mensaje: " + errMessage, UIClient.getInstance());
            }
        };
        getAutoIncrementQuery.exec();
        return autoIncrement.get();
    }

    public static List<SQLColumn> getColumnData(SQLDataObject tableObject) {
        List<SQLColumn> columns = new ArrayList<>();
        SQLQuery getColumnDataQuery = new SQLSelectQuery(tableObject.getRetrieveColumnDataStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("DATA_TYPE");
                    String charMaxLength = rs.getString("CHARACTER_MAXIMUM_LENGTH");
                    String numericPrecision = rs.getString("NUMERIC_PRECISION");
                    String numericScale = rs.getString("NUMERIC_SCALE");

                    boolean nullable = rs.getString("IS_NULLABLE").equals("YES");
                    int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                    SQLDataObject parentObject = tableObject;
                    SQLColumn newColumn = new SQLColumn(columnName, dataType, charMaxLength,
                            numericPrecision, numericScale, nullable, ordinalPosition, parentObject);

                    //extras used by UI that alters table definition
                    newColumn.setDefaultVal(rs.getString("COLUMN_DEFAULT"), false);
                    newColumn.setCharacterSet(rs.getString("CHARACTER_SET_NAME"));
                    newColumn.setCollation(rs.getString("COLLATION_NAME"));
                    newColumn.setUnsigned(rs.getString("COLUMN_TYPE").endsWith("unsigned"), false);
                    newColumn.setDateTimePrecision(DataTypeUtils.dataTypeIsTimeBased(dataType));
                    newColumn.setOnUpdateCurrentTimeStamp(rs.getString("EXTRA").equalsIgnoreCase("ON UPDATE CURRENT_TIMESTAMP"));
                    newColumn.setAutoincrement(rs.getString("EXTRA").equals("auto_increment"), false);

                    if (newColumn.getDataType().equals("enum")) {
                        String enumTypes = rs.getString("COLUMN_TYPE");
                        newColumn.setEnumLikeValues(enumTypes.substring(5, enumTypes.length() - 1));
                    } else if (newColumn.getDataType().equals("set")) {
                        String setTypes = rs.getString("COLUMN_TYPE");
                        newColumn.setEnumLikeValues(setTypes.substring(4, setTypes.length() - 1));
                    }
                    columns.add(newColumn);
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer datos de columna para " + tableObject.getName() + ".\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        getColumnDataQuery.exec();
        return columns;
    }

    public static Map<String, String> getDbCollationAndCharSetName() {
        Map<String, String> collationAndCharset = new HashMap<>();
        SQLQuery tableCollationQuery = new SQLSelectQuery(getConnectedDB().getCharsetAndCollationStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    collationAndCharset.put("collation", rs.getString("DEFAULT_COLLATION_NAME"));
                    collationAndCharset.put("charset", rs.getString("DEFAULT_CHARACTER_SET_NAME"));
                }
            }
        };
        tableCollationQuery.exec();

        return collationAndCharset;
    }

    public static List<SQLIndex> getIndexes(SQLDataObject tableObject) {
        List<SQLIndex> indexes = new ArrayList<>();
        SQLQuery getIndexesQuery = new SQLSelectQuery(tableObject.getRetrieveIndexesStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String indexName = rs.getString("Key_name");
                    if (indexName.equals("PRIMARY")) {
                        continue;
                    }
                    String indexType = rs.getString("Index_type");
                    indexes.add(new SQLIndex(indexName, indexType, tableObject.getName()));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer los índices para " + tableObject.getName() + " desde el servidor.\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        getIndexesQuery.exec();
        return indexes;
    }

    public static List<SQLTrigger> getTriggers(SQLDataObject tableObject) {
        List<SQLTrigger> triggers = new ArrayList<>();
        SQLQuery getTriggersQuery = new SQLSelectQuery(tableObject.getRetrieveTriggersStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String name = rs.getString("Trigger");
                    String event = rs.getString("Event");
                    String timing = rs.getString("Timing");
                    String stmt = rs.getString("Statement");
                    triggers.add(new SQLTrigger(name, event, timing, stmt));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer triggers para " + tableObject.getName() + ".\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        getTriggersQuery.exec();
        return triggers;
    }

    public static void loadKeys(SQLTable table) {
        if (table.getColumns() == null || table.getColumns().isEmpty()) {
            return;
        }
        List<SQLColumn> primaryKeyColumns = new ArrayList<>();
        SQLQuery loadKeysQuery = new SQLSelectQuery(table.getRetrievePKStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    SQLColumn column = table.getColumn(rs.getString("Column_name"));
                    primaryKeyColumns.add(column);
                }
                table.addPrimaryKey(new SQLPrimaryKey(primaryKeyColumns));
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer primary keys para " + table.getName() + ".\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        loadKeysQuery.exec();

        SQLQuery loadFKsQuery = new SQLSelectQuery(table.getRetrieveDetailedFKsStatement()) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String name = rs.getString("CONSTRAINT_NAME");
                    String tableName = rs.getString("TABLE_NAME");
                    String field = rs.getString("COLUMN_NAME");
                    String referencedTableName = rs.getString("REFERENCED_TABLE_NAME");
                    String referencedColumnName = rs.getString("REFERENCED_COLUMN_NAME");
                    String updateRule = rs.getString("UPDATE_RULE");
                    String deleteRule = rs.getString("DELETE_RULE");

                    SQLForeignKey fkDetail = new SQLForeignKey(name, tableName, field, referencedTableName, referencedColumnName, updateRule, deleteRule);
                    table.addFK(fkDetail);
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer claves foráneas para " + table.getName() + ".\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        loadFKsQuery.exec();
    }

    public static String getConnectedDBName() {
        return getConnectedDB().getName();
    }

    public static SQLDatabase getConnectedDB() {
        return SQLConnectionManager.getInstance().getConnectedDB();
    }

    public static String getSampleValueForColumn(SQLColumn column) {
        String value;
        if (column.isTimeBased()) {
            value = DataTypeUtils.convertDateToValidSQLDate(new Date(), column);
        } else if (column.isStringBased()) {
            value = "";
        } else {
            value = "0";
        }
        return value;
    }

    public static Date dateFromString(String dateString, SQLColumn column) {
        if (!column.isTimeBased()) {
            throw new IllegalArgumentException("The column must be of a data type that displays time or date.");
        }
        Date parsedDate = null;
        if (dateString != null) {
            try {
                parsedDate = column.getDataType().equals("date") ? DataTypeUtils.MYSQL_DATE_FORMAT.parse(dateString) : DataTypeUtils.MYSQL_DATETIME_FORMAT.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return parsedDate;
    }

    /**
     * Lleva a cabo un UPDATE en las filas y columnas seleccionadas. Una celda
     * recibirá el valor nuevo siempre y cuando se cumpla la condición dispuesta
     * por el {@link Predicate} dado. Por ejemplo, si el {@code Predicate} dice
     * que sólo las columnas que sean nullables van a ser updateadas, las
     * columnas que no lo sean serán ignoradas. <br><br>
     * Nota: independientemente del {@code Predicate} dado, si el valor nuevo es
     * igual al valor viejo, esa celda no se incluirá en el UPDATE.
     *
     * @param uiTable una referencia a una {@code JTable} que contiene los
     * valores que se van a actualizar.
     * @param table una referencia a una {@link SQLTable} que contiene las
     * columnas.
     * @param rows la {@code List} que contiene a todas las {@code SQLRow}
     * (filas con datos) de la tabla.
     * @param value el valor nuevo.
     * @param predicate sólamente las columnas que cumplan esa condición serán
     * actualizadas.
     */
    public static void updateGroup(JTable uiTable, SQLTable table, List<UIEditTable.SQLRow> rows, Object value, Predicate<SQLColumn> predicate) {
        int[] selectedCols = uiTable.getSelectedColumns();
        int[] selectedRows = uiTable.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            UIEditTable.SQLRow row = rows.get(selectedRows[i]);
            for (int j = 0; j < selectedCols.length; j++) {
                SQLColumn column = table.getColumn(selectedCols[j]);
                Object oldValue = row.getValue(column.getName());
                if ((oldValue != null && !oldValue.equals(value) || value != null && !value.equals(oldValue)) && predicate.test(column)) {
                    if (row.isBrandNew()) {
                        row.setValue(column.getName(), value);
                        row.refreshWithUi(selectedRows[i]);
                    } else {
                        row.setValueForUpdate(column, value);
                    }
                }
            }
            if (row.hasUncommittedChanges()) {
                //llevamos a cabo el update en la DB en las filas seleccionadas
                if (row.update(selectedRows[i])) { //si la update salió bien, refrescamos los valores en la tabla con el valor nuevo
                    row.refreshWithUi(selectedRows[i]);
                } else { //si algo salió mal, descartamos todos los valores nuevos
                    for (int j = 0; j < selectedRows.length; j++) {
                        rows.get(selectedRows[j]).discardUpdate();
                    }
                    break;
                }
            }
        }
    }

    public static Object fetchFirstValueForColumn(SQLColumn column) {
        FinalValue val = new FinalValue();
        SQLQuery query = new SQLSelectQuery("SELECT `" + column.getName() + "` FROM `" + column.getParentTable().getName() + "` LIMIT 1", true) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    val.set(rs.getString(1));
                }
            }
        };
        query.exec();
        return val.get();
    }

    public static List<Object> getAllowedValuesForFk(SQLForeignKey fk) {
        List<Object> refTableVals = new ArrayList<>();
        SQLQuery query = new SQLSelectQuery("SELECT " + fk.getReferencedColumnName() + " FROM " + fk.getReferencedTableName() + " LIMIT 50", true) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    refTableVals.add(rs.getString(1));
                }
            }
        };
        query.exec();
        return refTableVals;
    }

    /**
     * Devuelve cualquier fila(s) que contengan el valor dado en la tabla. El
     * valor puede estar incluido dentro de otra string y también será devuelto.
     * Por ejemplo, si el valor a buscar es "er" y se encuentra una fila con el
     * valor "user", ésta será devuelta.
     *
     * @param tableName La tabla en la cual se buscará el valor dado.
     * @param value El valor a buscar.
     * @return Las filas que contengan el valor dado con los datos de todas sus
     * columnas. <br> <b>Nota: el primer índice del vector de vectores devueltos
     * contiene el nombre de las columnas.</b>. Si no se encuentra el valor, el
     * vector de vectores volverá vacío.
     */
    public static Vector<Vector<String>> findAnyWithStringInTable(String tableName, String value) {
        Vector<Vector<String>> rows = new Vector<>();
        if (value == null) {
            return rows;
        }

        StringBuilder querySb = new StringBuilder("SELECT * FROM `" + tableName + "` WHERE ");
        Vector<String> columns = new Vector<>(getColumnsFromTableAsString(tableName));
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            querySb.append("`").append(columnName).append("`").append(" LIKE ").append("'%").append(value).append("%'").append(i < columns.size() - 1 ? " OR " : "");
        }

        SQLQuery findQuery = new SQLSelectQuery(querySb.toString(), true) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    for (int i = 0; i < columns.size(); i++) {
                        row.add(rs.getString(i + 1));
                    }
                    rows.add(row);
                }
                rows.add(0, columns);
            }
        };
        findQuery.exec();
        return rows;
    }

    public static Map<String, Integer> getDbStats(boolean skipRows) {
        Map<String, Integer> mapStats = new HashMap<>();
        SQLQuery rowCountQuery = new SQLSelectQuery("SELECT SUM(TABLE_ROWS) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + getConnectedDBName() + "';") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                mapStats.put("ROW_COUNT", rs.next() ? rs.getInt(1) : 0);
            }

            @Override
            public void onFailure(String errMessage) {
                mapStats.put("ROW_COUNT", -1);
            }
        };

        SQLQuery tableCountQuery = new SQLSelectQuery("SELECT TABLE_TYPE, COUNT(*) AS cnt FROM information_schema.tables WHERE table_schema = '" + getConnectedDBName() + "' GROUP BY TABLE_TYPE;") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                mapStats.put("TABLE_COUNT", 0);
                mapStats.put("VIEW_COUNT", 0);
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    int count = rs.getInt(2);
                    mapStats.put(rowCount == 1 ? "TABLE_COUNT" : "VIEW_COUNT", count);
                }
            }

            @Override
            public void onFailure(String errMessage) {
                mapStats.put("TABLE_COUNT", -1);
                mapStats.put("VIEW_COUNT", -1);
            }
        };

        SQLQuery routinesCountQuery = new SQLSelectQuery("SELECT ROUTINE_TYPE, COUNT(*) as CNT FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_SCHEMA = '"
                + getConnectedDBName() + "' GROUP BY ROUTINE_TYPE;") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                mapStats.put("FUNCTION_COUNT", 0);
                mapStats.put("PROCEDURE_COUNT", 0);
                while (rs.next()) {
                    String routineType = rs.getString(1);
                    int count = rs.getInt(2);
                    mapStats.put(routineType + "_COUNT", count);
                }
            }

            @Override
            public void onFailure(String errMessage) {
                mapStats.put("FUNCTION_COUNT", -1);
                mapStats.put("PROCEDURE_COUNT", -1);
            }
        };

        if (!skipRows) {
            rowCountQuery.exec();
        }
        tableCountQuery.exec();
        routinesCountQuery.exec();
        return mapStats;
    }

    public static boolean currentDbIsEmpty() {
        FinalValue<Boolean> isEmpty = new FinalValue<>(Boolean.FALSE);
        SQLQuery query = new SQLSelectQuery("SELECT COUNT(*) FROM information_schema.tables WHERE TABLE_SCHEMA = '" + getConnectedDBName() + "'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                int count = rs.next() ? rs.getInt(1) : 0;
                isEmpty.set(count == 0);
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Error", "No se pudo traer información sobre las tablas para la base de datos " + getConnectedDBName()
                        + ".\nEl servidor respondió con mensaje: " + errMessage, null);
            }
        };
        query.exec();
        return isEmpty.get();
    }

}
