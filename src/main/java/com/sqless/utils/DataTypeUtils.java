package com.sqless.utils;

import com.mysql.cj.jdbc.Blob;
import com.sqless.sql.objects.SQLColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTypeUtils {

    public static final SimpleDateFormat MYSQL_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat MYSQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String[] DEFAULT_DATA_TYPES = {"int", "decimal", "datetime", "varchar", "enum", "set"};

    public static boolean dataTypeIsNumeric(String dataType) {
        return dataTypeIsInteger(dataType) || dataTypeIsDecimal(dataType);
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

    public static boolean dataTypeIsStringBased(String dataType) {
        return dataType.equals("varchar") || dataType.equals("enum") || dataType.equals("set") || dataType.equals("text");
    }

    public static boolean dataTypeCanBeUnsigned(String dataType) {
        return dataType.equals("tinyint") || dataType.equals("smallint") || dataType.equals("mediumint");
    }

    public static boolean dataTypeSupportsLength(String dataType) {
        return !dataType.equals("enum") && !dataType.equals("set")
                && !dataType.equals("text") && !dataType.equals("year")
                && !dataTypeIsTimeBased(dataType);
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

    /**
     * Converts the given object into a {@code String} that conforms with the
     * SQL {@code date} or {@code datetime} data types depending on the one from
     * the column given. <br>
     * Example: if given {@code Thu Jul 20 00:00:00 ART 2017}, this method will
     * convert that to {@code 2017-07-20 00:00:00}.
     *
     * @param date   a {@code Date} object to be converted.
     * @param column a {@code SQLColumn} from which to take the data type.
     * @return A formatted {@code String} compatible with MySQL date datatypes.
     * @throws IllegalArgumentException if the column given is of a data type
     *                                  that doesn't display time.
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

    public static boolean isDefaultDatatype(String datatype) {
        for (String defaultType : DEFAULT_DATA_TYPES) {
            if (datatype.equalsIgnoreCase(defaultType)) {
                return true;
            }
        }
        return false;
    }
}
