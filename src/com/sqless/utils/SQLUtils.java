package com.sqless.utils;

import com.mysql.jdbc.Blob;
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
import java.text.SimpleDateFormat;
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

    public static final SimpleDateFormat MYSQL_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat MYSQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String[] DEFAULT_DATA_TYPES = {"int", "decimal", "datetime", "varchar"};

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
     * @param includeMaster Whether to include the 'mysql' DB in the list.
     * @return a {@code List} of type {@code String} with the names of all the
     * databases within the engine.
     */
    public static List<String> retrieveDBNamesFromServer(boolean includeMaster) {
        List<String> dbNames = new ArrayList<>();

        SQLQuery dbNamesQuery = new SQLSelectQuery("SHOW DATABASES" + (includeMaster ? " WHERE `Database` != 'mysql'" : "")) {
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

    public static String renameObjectDefinition(SQLObject object, String newName) {
        return object.getDefinition().replaceAll(object.getName(), newName);
    }

    public static boolean isDefaultDatatype(String datatype) {
        for (String defaultType : DEFAULT_DATA_TYPES) {
            if (datatype.equalsIgnoreCase(defaultType)) {
                return true;
            }
        }
        return false;
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
                    newColumn.setDateTimePrecision(rs.getString("DATETIME_PRECISION") != null);
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

    /**
     * Wraps names with spaces in '``' for compatibility with SQL queries. If
     * the name doesn't have spaces, this method simply returns the name as it
     * was given.
     *
     * @param name The string to be sqlified.
     * @return a 'sqlfied' {@code String} compatible with SQL commands.
     */
    public static String sqlify(String name) {
        if (name.contains(".")) {
            String[] nameSplit = name.split("\\.");

            if (nameSplit[1].split(" ").length > 1) {
                return nameSplit[0] + "." + '`' + nameSplit[1] + '`';
            }
        }

        if (name.contains(" ")) {
            return '`' + name + '`';
        }

        return name;
    }

    /**
     * Remueve el mes y el año "01-01" que (por alguna razón) viene al llamar
     * {@link ResultSet#getString(int)} a una columna de tipo {@code year}.
     *
     * @param year Un String con formato {@code ####-##-##}.
     * @return un String compatible con el tipo de dato {@code year} de MySQL.
     * Por ejemplo {@code 2016-01-01} se transformará en {@code 2016}.
     */
    public static String parseSQLYear(String year) {
        return year != null && year.contains("-") ? year.substring(0, year.indexOf("-")) : year;
    }

    public static String parseBlob(Blob blob) throws SQLException {
        return blob != null ? "BLOB (" + String.format("%.2f", (float) blob.length() / 1024) + " KB)" : null;
    }

    public static boolean dataTypeIsNumeric(String dataType) {
        return dataType.equals("tinyint") || dataType.equals("smallint")
                || dataType.equals("mediumint") || dataType.equals("int")
                || dataType.equals("bigint") || dataType.equals("decimal");
    }

    public static boolean dataTypeIsInteger(String dataType) {
        return dataType.equals("tinyint") || dataType.equals("smallint")
                || dataType.equals("mediumint") || dataType.equals("int")
                || dataType.equals("bigint");
    }

    public static boolean dataTypeIsDecimal(String dataType) {
        return dataType.equals("float") || dataType.equals("decimal") || dataType.equals("double") || dataType.equals("numeric");
    }

    public static boolean dataTypeIsTimeBased(String dataType) {
        return dataType.equals("date") || dataType.equals("datetime")
                || dataType.equals("time") || dataType.equals("timestamp");
    }

    public static String getConnectedDBName() {
        return getConnectedDB().getName();
    }

    public static SQLDatabase getConnectedDB() {
        return SQLConnectionManager.getInstance().getConnectedDB();
    }

    /**
     * Converts the given object into a {@code String} that conforms with the
     * SQL {@code date} or {@code datetime} data types depending on the one from
     * the column given. <br>
     * Example: if given {@code Thu Jul 20 00:00:00 ART 2017}, this method will
     * convert that to {@code 2017-07-20 00:00:00}.
     *
     *
     * @param date a {@code Date} object to be converted.
     * @param column a {@code SQLColumn} from which to take the data type.
     * @return A formatted {@code String} compatible with MySQL date datatypes.
     * @throws IllegalArgumentException if the column given is of a data type
     * that doesn't display time.
     */
    public static String convertDateToValidSQLDate(Object date, SQLColumn column) {
        if (!column.isTimeBased()) {
            throw new IllegalArgumentException("The column must be of a data type that displays time or date.");
        }
        if (date == null) {
            return null;
        }
        if (!(date instanceof Date)) {
            return "" + date;
        }

        return column.getDataType().equals("date") ? MYSQL_DATE_FORMAT.format(date) : MYSQL_DATETIME_FORMAT.format(date);
    }

    public static String convertDefaultToValidSQLDate(SQLColumn column) {
        if (!column.isTimeBased()) {
            throw new IllegalArgumentException("The column must be of a data type that displays time or date.");
        }
        if (column.getDefaultVal() == null) {
            return null;
        }
        String defaultVal = column.getDefaultVal();
        return convertDateToValidSQLDate(!defaultVal.startsWith("CURRENT") ? new Date() : defaultVal, column);
    }

    public static String getSampleValueForColumn(SQLColumn column) {
        String value;
        if (column.isTimeBased()) {
            value = SQLUtils.convertDateToValidSQLDate(new Date(), column);
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
                parsedDate = column.getDataType().equals("date") ? MYSQL_DATE_FORMAT.parse(dateString) : MYSQL_DATETIME_FORMAT.parse(dateString);
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
                    row.setValueForUpdate(column, value);
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

}
