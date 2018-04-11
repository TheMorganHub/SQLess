package com.sqless.utils;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.sql.objects.*;
import com.sqless.ui.UIEditTable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    /**
     * Filtra la palabra clave 'DELIMITER' y todos los delimitadores que no sean
     * ';' asociados a esa palabra clave en la query dada ya que no es válida en
     * JDBC.<br>
     * Por ejemplo: <br><pre>
     * <code>
     * DELIMITER $$ <br>
     *
     * CREATE PROCEDURE film_not_in_stock(IN p_film_id INT, IN p_store_id INT,
     * OUT p_film_count INT) READS SQL DATA <br>
     * BEGIN <br>
     * SELECT inventory_id FROM inventory WHERE film_id = p_film_id AND store_id
     * = p_store_id AND NOT inventory_in_stock(inventory_id); <br>
     *
     * SELECT FOUND_ROWS() INTO p_film_count; <br>
     * END $$
     * </code></pre>
     * <br>
     * <br>
     * Se convertirá en: <br>
     * <pre><code>
     * CREATE PROCEDURE film_not_in_stock(IN p_film_id INT, IN p_store_id INT,
     * OUT p_film_count INT) READS SQL DATA <br>
     * BEGIN <br>
     * SELECT inventory_id FROM inventory WHERE film_id = p_film_id AND store_id
     * = p_store_id AND NOT inventory_in_stock(inventory_id); <br>
     *
     * SELECT FOUND_ROWS() INTO p_film_count; <br>
     * END ;
     * </code></pre>
     *
     * @param sql La query SQL a filtrar.
     * @return La misma query SQL sin nada relacionado a {@code DELIMITER}. Si
     * la query original no hace uso de la palabra clave {@code DELIMITER} será
     * retornada sin modificaciones.
     */
    public static String filterDelimiterKeyword(String sql) {
        Pattern delimiterPat = Pattern.compile("DELIMITER *(\\S*)");
        Matcher matcher = delimiterPat.matcher(sql);
        Set<String> delimitersToReplace = new HashSet<>();
        while (matcher.find()) {
            String delimiter = matcher.group(1);
            if (!delimiter.equals(";")) {
                //delimitadores que necesitan ser escapados antes de ser aplicados en expresiones regulares
                if (delimiter.contains("/") || delimiter.contains("$")) {
                    StringBuilder escapedDelimiters = new StringBuilder();
                    for (int i = 0; i < delimiter.length(); i++) {
                        if (delimiter.charAt(i) == '/' || delimiter.charAt(i) == '$') {
                            escapedDelimiters.append('\\').append(delimiter.charAt(i));
                        }
                    }
                    delimiter = escapedDelimiters.toString();
                }
                delimitersToReplace.add(delimiter);
            }
        }

        if (delimitersToReplace.isEmpty()) {
            return sql;
        }
        String replaced = sql.replaceAll(String.join("|", delimitersToReplace), ";");
        return replaced.replaceAll("DELIMITER *(\\S*)", "");
    }

    public static String[] getEnumLikeValuesAsArray(String enumLikeValues) {
        if (enumLikeValues == null) {
            return null;
        }
        String original = enumLikeValues;
        Pattern enumPattern = Pattern.compile("(enum|set)\\((.*)\\)$");
        Matcher matcher = enumPattern.matcher(original);
        String group = matcher.find() ? matcher.group(2) : "";
        String groupNoLeadingAndEndQuot = group.substring(1, group.length() - 1);
        return groupNoLeadingAndEndQuot.split("','");
    }

    public static Map<String, String> getMySQLInfo() {
        Map<String, String> info = new HashMap<>();
        SQLQuery mysqlInfoQuery = new SQLSelectQuery("SHOW VARIABLES;") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String key = rs.getString(1);
                    String value = rs.getString(2);
                    info.put(key, value);
                }
            }
        };
        mysqlInfoQuery.exec();
        return info;
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

    public static List<String> getTablesFromDBAsString() {
        List<String> tableNames = new ArrayList<>();
        SQLQuery showTablesQuery = new SQLSelectQuery("SHOW TABLES FROM `" + getConnectedDBName() + "`") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    tableNames.add(rs.getString(1));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("ERROR", "Could not retrieve table names from server: " + errMessage, null);
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
                UIUtils.showErrorMessage("ERROR", "Could not retrieve Column names from server: " + errMessage, null);
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
                UIUtils.showErrorMessage("ERROR", "Could not retrieve views from database: " + errMessage, null);
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
        FinalValue<Integer> autoIncrement = new FinalValue<>();

        SQLQuery getAutoIncrementQuery = new SQLSelectQuery("SELECT `AUTO_INCREMENT`\n"
                + "FROM  INFORMATION_SCHEMA.TABLES\n"
                + "WHERE TABLE_SCHEMA = '" + getConnectedDBName() + "'\n"
                + "AND   TABLE_NAME   = '" + table.getName() + "'") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                autoIncrement.set(rs.next() ? rs.getInt("AUTO_INCREMENT") : 1);
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
                    if (newColumn.getDataType().equals("enum") || newColumn.getDataType().equals("set")) {
                        newColumn.setEnumLikeValues(rs.getString("COLUMN_TYPE"));
                    }
                    columns.add(newColumn);
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("ERROR", "Could not retrieve column data for " + tableObject.getName() + ": " + errMessage, null);
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
                    collationAndCharset.put("collation", rs.getString("COLLATION_NAME"));
                    collationAndCharset.put("charset", rs.getString("character_set_name"));
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
                UIUtils.showErrorMessage("ERROR", "Could not retrieve indexes for " + tableObject.getName() + ": " + errMessage, null);
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
                UIUtils.showErrorMessage("ERROR", "Could not retrieve triggers for " + tableObject.getName() + ": " + errMessage, null);
            }
        };
        getTriggersQuery.exec();
        return triggers;
    }

    public static void loadKeys(SQLTable table) {
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
                UIUtils.showErrorMessage("ERROR", "There was an error retrieving primary keys for "
                        + table.getName() + ": " + errMessage, null);
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
                UIUtils.showErrorMessage("ERROR", "There was an error retrieving foreign keys for "
                        + table.getName() + ": " + errMessage, null);
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
        SQLQuery query = new SQLSelectQuery("SELECT `" + column.getName() + "` FROM `" + column.getParentTable().getName() + "` LIMIT 1") {
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

}
