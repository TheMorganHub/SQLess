package com.sqless.sql.objects;

import com.sqless.utils.DataTypeUtils;
import com.sqless.utils.SQLUtils;
import java.util.List;
import java.util.Map;

/**
 * This class represents a column in a table.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SQLColumn extends SQLObject implements SQLSelectable, SQLEditable,
        SQLRenameable, SQLDroppable, SQLCreatable, Comparable<SQLColumn> {

    private SQLDataObject parentTable;
    /**
     * Denotes the position of the column within its parent table. Starts at 1.
     * e.g: to retrieve the columns that precedes this one, you must do
     * this.ordinalPosition - 2.
     */
    private int ordinalPosition;
    private boolean nullable;
    private String dataType;
    private String length;
    /**
     * Numero de decimales
     */
    private String numericScale;
    /**
     * Lo mismo que length pero para números
     */
    private String numericPrecision;
    private boolean autoincrement;
    private String defaultVal;
    private String uncommittedName; //utilizado para forms que cambian el nombre a columnas
    /**
     * Bandera que sirve para notificar que esta columna tiene cambios no
     * guardados. La interfaz SWING que trabaje con actualizar columnas se
     * encargará se cambiar su estado. De todas maneras, para valores como
     * Default o Auto increment, este flag se cambiará automaticamente a true,
     * ya que esos valores siempre serán false o null por defecto y si cambian
     * quiere decir que la columna fue modificada.
     */
    private boolean hasUncommittedChanges;
    private boolean isBrandNew;
    private String characterSet;
    private String collation;
    private boolean unsigned;
    private boolean dateTimePrecision;
    private String firstTimeChangeStatement;
    //used by UIMoveColumns
    private int firstTimeOrdinalPosition;
    private boolean onUpdateCurrentTimeStamp;
    private String enumLikeValues;
    private String nonSupportedDataType;

    public SQLColumn(String name, String dataType, String charMaxLength, String numericPrecision,
            String numericScale, boolean nullable, int ordinalPosition,
            SQLDataObject parentTable) {
        super(name);
        this.dataType = dataType;
        if (!DataTypeUtils.isDefaultDatatype(dataType)) {
            nonSupportedDataType = dataType;
        }
        this.length = charMaxLength;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.nullable = nullable;
        this.ordinalPosition = ordinalPosition;
        this.parentTable = parentTable;
        uncommittedName = name;
        hasUncommittedChanges = false;
    }

    /**
     * Crea una columna manualmente sin traer todos sus datos desde el motor de
     * la base de datos. <br>
     * <b>Importante: </b> la columna se crea con {@code parentTable} en null, y
     * se le asignará una tabla dentro de los métodos
     * {@link SQLTable#addColumn(com.sqless.sql.objects.SQLColumn)} o {@link SQLTable#insertColumn(int, com.sqless.sql.objects.SQLColumn)
     * }
     *
     * @param nombre El nombre de la columna
     */
    public SQLColumn(String nombre) {
        super(nombre);
        uncommittedName = nombre;
        updateDataTypeByName(nombre);
        hasUncommittedChanges = false;
        nullable = true;
        characterSet = "latin1";
        collation = "latin1_swedish_ci";
        isBrandNew = true;
    }

    public void setParentTable(SQLDataObject parentObject) {
        this.parentTable = parentObject;
    }

    public void setFirstTimeChangeStatement() {
        this.firstTimeChangeStatement = getChangeColumnStatement();
        firstTimeOrdinalPosition = ordinalPosition;
    }

    /**
     * Provee una manera de decidir el tipo de dato de la columna en base a un
     * nombre. Por ejemplo: si el nombre es id, el tipo de dato será int.
     *
     * @param name El nombre.
     */
    public void updateDataTypeByName(String name) {
        if (name.startsWith("id")) {
            setDataType("int");
        } else if (name.startsWith("fecha") || name.startsWith("date")) {
            setDataType("datetime");
        } else if (name.startsWith("precio")) {
            setDataType("decimal");
        } else {
            setDataType("varchar");
        }
    }

    public boolean isBrandNew() {
        return isBrandNew;
    }

    public void setEnumLikeValues(String enumLikeValues) {
        this.enumLikeValues = enumLikeValues;
    }

    public String getEnumLikeValues() {
        return enumLikeValues == null ? dataType + "()" : enumLikeValues.substring(enumLikeValues.indexOf(dataType));
    }

    public String getNonSupportedDataType() {
        return nonSupportedDataType;
    }

    public void setUnsigned(boolean unsigned, boolean evaluateChanges) {
        this.unsigned = unsigned;
        if (evaluateChanges) {
            evaluateUncommittedChanges();
        }
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public void setOnUpdateCurrentTimeStamp(boolean flag) {
        this.onUpdateCurrentTimeStamp = flag;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setDateTimePrecision(boolean dateTimePrecision) {
        this.dateTimePrecision = dateTimePrecision;
    }

    public void setUncommittedName(String uncommittedName) {
        if (!isBrandNew) {
            this.uncommittedName = uncommittedName;
        } else {
            rename(uncommittedName);
        }
    }

    /**
     * Returns this column's uncommitted name.
     *
     * @return this column's uncommitted name UNLESS the column is brand new, in
     * which case it will return its original name by calling
     * {@link SQLObject#getName()}.
     */
    public String getUncommittedName() {
        return !isBrandNew ? uncommittedName : getName();
    }

    /**
     * Checks whether the position of this column within its parent table has
     * changed since load. Used by UIMoveColumns.
     *
     * @return {@code true} if
     * {@code firstTimeOrdinalPosition != ordinalPosition}
     */
    public boolean positionChanged() {
        return firstTimeOrdinalPosition != ordinalPosition;
    }

    public int getFirstTimeOrdinalPosition() {
        return firstTimeOrdinalPosition;
    }

    public boolean hasUncommittedChanges() {
        return hasUncommittedChanges;
    }

    @Override
    public void commit(String... args) {
        hasUncommittedChanges = false;
        if (!isBrandNew) {
            rename(uncommittedName);
        } else {
            //en una columna que no es brand new, el uncommittedname siempre va a inicializarse como el nombre original. 
            //Luego de hacer el commit, la columna va a dejar de ser brand new, es por eso que le asignamos el nombre original como uncommittedName para el futuro.
            uncommittedName = getName();
        }
        isBrandNew = false;
        setFirstTimeChangeStatement();
    }

    /**
     * Evaluates first and then marks this column as having uncommitted changes
     * if and only if:
     * <ul>
     * <li>The column isn't brand new.</li>
     * <li>The column's original change statement IS NOT EQUAL to the change
     * statement resulting from the edit that called this method. This prevents
     * the columns from being updated needlessly.</li>
     * </ul>
     *
     * @return
     */
    public boolean evaluateUncommittedChanges() {
        if (!isBrandNew && !firstTimeChangeStatement.equals(getChangeColumnStatement())) {
            this.hasUncommittedChanges = true;
        }
        if (hasUncommittedChanges && firstTimeChangeStatement.equals(getChangeColumnStatement())) {
            this.hasUncommittedChanges = false;
        }
        return hasUncommittedChanges;
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCollation() {
        return collation;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void moveUp() {
        if (ordinalPosition == 1) {
            return;
        }
        List<SQLColumn> parentCols = parentTable.getColumns();

        SQLColumn previousCol = parentCols.remove(ordinalPosition - 2);
        setOrdinalPosition(ordinalPosition - 1);
        previousCol.setOrdinalPosition(ordinalPosition + 1);
        parentCols.add(ordinalPosition, previousCol);
        if (!isBrandNew()) {
            if (ordinalPosition != firstTimeOrdinalPosition) {
                ((SQLTable) parentTable).addToMovedColumns(this);
            } else {
                ((SQLTable) parentTable).removeFromMovedColumns(this);
            }
        }
    }

    public void moveDown() {
        List<SQLColumn> parentCols = parentTable.getColumns();
        if (ordinalPosition == parentCols.size()) {
            return;
        }

        SQLColumn previousCol = parentCols.remove(ordinalPosition);
        setOrdinalPosition(ordinalPosition + 1);
        previousCol.setOrdinalPosition(ordinalPosition - 1);
        parentCols.add(ordinalPosition - 2, previousCol);
        if (!isBrandNew()) {
            if (ordinalPosition != firstTimeOrdinalPosition) {
                ((SQLTable) parentTable).addToMovedColumns(this);
            } else {
                ((SQLTable) parentTable).removeFromMovedColumns(this);
            }
        }
    }

    public void setNumericPrecision(String numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public void setLength(String length) {
        if (isStringBased()) {
            int intLength = Integer.parseInt(length);
            this.length = dataType.equals("text") ? intLength + "" : intLength > 255 ? "255" : length;
        } else {
            setNumericPrecision(length);
        }
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
        length = dataType.equals("varchar") ? "255" : null;

        if (DataTypeUtils.dataTypeIsStringBased(dataType)) {
            if (collation == null || characterSet == null) {
                Map<String, String> charsetAndCollation = SQLUtils.getDbCollationAndCharSetName();
                collation = charsetAndCollation.get("collation");
                characterSet = charsetAndCollation.get("charset");
            }
        }

        numericPrecision = DataTypeUtils.dataTypeIsNumeric(dataType) ? "10" : null;
        numericScale = DataTypeUtils.dataTypeIsDecimal(dataType) ? "2" : null;
        dateTimePrecision = DataTypeUtils.dataTypeIsTimeBased(dataType);
        unsigned = DataTypeUtils.dataTypeCanBeUnsigned(dataType);
        onUpdateCurrentTimeStamp = dataType.equals("timestamp");

        if (autoincrement && !DataTypeUtils.dataTypeIsNumeric(dataType)) {
            autoincrement = false;
        }
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDataPrecision() {
        if (length != null && length.equals("-1")) {
            return "(255)";
        }
        if (isDecimalBased()) {
            return "(" + numericPrecision + "," + numericScale + ")";
        }

        if (numericPrecision != null) {
            return "(" + numericPrecision + ")";
        }

        if (length != null && isStringBased()) {
            return "(" + length + ")";
        }

        return "";
    }

    public String getAfterStatement() {
        if (ordinalPosition == 1) {
            return "FIRST";
        }
        if (isBrandNew()) {
            return "AFTER `" + parentTable.getColumns().get(ordinalPosition - 2).getUncommittedName() + "`";
        }
        SQLColumn prevCol = parentTable.findClosestNonBrandNewColumnOf(this);
        return prevCol == this ? "FIRST" : "AFTER `" + prevCol.getUncommittedName() + "`";

    }

    /**
     * If the values within this column have to be wrapped with ` `.
     *
     * @return {@code true} if the NUMERIC_PRECISION of this column is null.
     */
    public boolean isStringBased() {
        return numericPrecision == null && !isTimeBased() && !dataType.equals("year");
    }

    public boolean isTimeBased() {
        return dateTimePrecision || DataTypeUtils.dataTypeIsTimeBased(dataType);
    }

    /**
     * Sets the decimal part of this Column.
     *
     * @param numericScale
     */
    public void setNumericScale(String numericScale) {
        this.numericScale = numericScale;
    }

    public String getLength() {
        if (isStringBased()) {
            return length;
        } else {
            return numericPrecision;
        }
    }

    public String getNumericScale() {
        return numericScale;
    }

    public String getNumericPrecision() {
        return numericPrecision;
    }

    public boolean isDecimalBased() {
        return dataType.equals("decimal") || (numericScale != null && Integer.parseInt(numericScale) > 0);
    }

    public boolean isPK() {
        return parentTable.columnIsPK(this);
    }

    public boolean isFK() {
        return ((SQLTable) parentTable).columnIsFK(this);
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal, boolean evaluateChanges) {
        if (defaultVal == null) {
            this.defaultVal = defaultVal;
            if (evaluateChanges) {
                evaluateUncommittedChanges();
            }
            return;
        }

        if (!defaultVal.equals(this.defaultVal)) {
            this.defaultVal = defaultVal;
            if (evaluateChanges) {
                evaluateUncommittedChanges();
            }
        }

    }

    public void setAutoincrement(boolean autoincrement, boolean evaluateChanges) {
        this.autoincrement = autoincrement;
        if (evaluateChanges) {
            evaluateUncommittedChanges();
        }
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public SQLDataObject getParentTable() {
        return parentTable;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    /**
     * Transforma el valor dado en un valor listo para ser agregado a la base de
     * datos. Es en este método en donde SQLess busca evitar que se inserten
     * valores nulos en columnas que no acepten null, trayendo un valor
     * "ejemplo" desde
     * {@link SQLUtils#getSampleValueForColumn(com.sqless.sql.objects.SQLColumn)}.
     * Si la columna acepta valores nulos, el valor nulo se mantendrá. Por otro
     * lado, si el valor no es nulo y la columna es basada en tiempo, se llamará
     * a
     * {@link DataTypeUtils#convertDateToValidSQLDate(java.lang.Object, com.sqless.sql.objects.SQLColumn)}
     * para convertir ese valor de tiempo a formato de fecha de SQL compatible
     * con esa columna para hacer el insert.
     *
     * @param value el valor a transformar.
     * @return un valor transformado, o null si el valor original es nulo y la
     * columna acepta valores nulos. Si el valor es nulo y la columna no acepta
     * valores nulos, se tomará un valor de
     * {@link SQLUtils#getSampleValueForColumn(com.sqless.sql.objects.SQLColumn)}.
     */
    public Object formatUserValue(Object value) {
        if (value == null) {
            if (defaultVal == null) {
                value = isNullable() ? null : SQLUtils.getSampleValueForColumn(this);
            }
        } else {
            value = isTimeBased() ? DataTypeUtils.convertDateToValidSQLDate(value, this) : value;
        }

        return value;
    }

    @Override
    public String toString() {
        return getName() + " ("
                + dataType + getDataPrecision()
                + (nullable ? ", null" : ", not null")
                + ")";
    }

    @Override
    public String getRenameStatement(String newName) {
        uncommittedName = newName; //esto cambio temporario hace que el changeColumnStatement use CHANGE en vez de MODIFY
        String renameStmt = "ALTER TABLE `" + parentTable.getName() + "` " + getChangeColumnStatement();
        uncommittedName = getName();
        return renameStmt;
    }

    public String getChangeColumnStatement() {
        return ((!getName().equals(uncommittedName) ? "CHANGE COLUMN `" + getName() + "` `" + uncommittedName + "` " : "MODIFY COLUMN `" + getName() + "` ")
                + (getDataType().equals("enum") || getDataType().equals("set") ? getEnumLikeValues() : getDataType() + getDataPrecision())
                + (unsigned ? " UNSIGNED" : "")
                + " " + (isStringBased() ? "CHARACTER SET " + characterSet + " " + "COLLATE " + collation : "")
                + " " + (nullable ? "NULL" : "NOT NULL")
                + " " + (autoincrement ? "AUTO_INCREMENT " : "")
                + (defaultVal == null || defaultVal.isEmpty() ? "" : "DEFAULT " + (isStringBased() ? "'" + defaultVal + "'" : defaultVal))
                + (onUpdateCurrentTimeStamp ? " ON UPDATE CURRENT_TIMESTAMP" : "")
                + " " + getAfterStatement()).trim();
    }

    @Override
    public String getCreateStatement() {
        return "`" + getUncommittedName() + "` " + dataType + getDataPrecision() + " " + (nullable ? "NULL" : "NOT NULL") + " "
                + (autoincrement ? "AUTO_INCREMENT " : "")
                + (defaultVal == null || defaultVal.isEmpty() ? "" : "DEFAULT " + (isStringBased() ? "'" + defaultVal + "'" : defaultVal));
    }

    public String getCreateStatement(boolean addColumnKeywords) {
        return (addColumnKeywords ? "ADD COLUMN" : "") + " " + getCreateStatement() + " " + getAfterStatement();
    }

    @Override
    public String getDropStatement() {
        return "DROP COLUMN `" + getName() + "`";
    }

    @Override
    public String getSelectStatement(int limit) {
        return "SELECT `" + getName() + "` FROM `"
                + parentTable.getName() + "`" + (limit == SQLSelectable.ALL ? "" : " LIMIT " + limit);
    }

    @Override
    public int compareTo(SQLColumn col) {
        return getName().compareTo(col.getName());
    }

}
