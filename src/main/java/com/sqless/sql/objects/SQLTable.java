package com.sqless.sql.objects;

import com.sqless.ui.GenericWaitingDialog;
import com.sqless.utils.SQLUtils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import com.sqless.utils.AsyncAction;

public class SQLTable extends SQLDataObject implements SQLEditable, SQLRenameable,
        SQLCreatable, SQLDroppable {

    private List<SQLForeignKey> foreignKeys;
    private List<SQLForeignKey> droppedFKs;
    private List<SQLColumn> droppedColumns;
    private List<SQLColumn> movedColumns;

    public SQLTable(String tableName) {
        super(tableName);
    }

    /**
     * Inicializa una tabla nueva sin columnas y nombre vacío. Método utilizado
     * principalmente por UIs que manipulan tablas y columnas. Se asume que este
     * método se llamará dentro del contexto de una UI, así que también se
     * inicializarán los {@code ArrayList} {@code droppedColumns} y
     * {@code droppedFKs}.
     */
    public SQLTable() {
        super("", new ArrayList<>());
        foreignKeys = new ArrayList<>();
        droppedColumns = new ArrayList<>();
        droppedFKs = new ArrayList<>();
        movedColumns = new ArrayList<>();
    }

    /**
     * Inicializa una nueva tabla desde otra. Principalmente utilizado por UIs
     * que manipulan tablas. Este constructor es necesario para evitar
     * contradicciones o sobrescrituras en la información de una tabla si se
     * tiene una sola referencia en todo el programa. E.j: si mientras estoy
     * editando una tabla, la persona abre el JTree, las columnas se van a
     * volver a cargar y a partir de ahí, el comportamiento dentro de la UI se
     * vuelve impredecible. <br><br>
     * Nota: dentro de este método también se inicializan los arraylists de
     * foreign keys droppeadas y columnas droppeadas. Estos arraylists son
     * utilizados por UI que manipulan tablas.
     *
     * @param table
     */
    public SQLTable(SQLTable table) {
        super(table.getName(), new ArrayList<>());
        droppedColumns = new ArrayList<>();
        droppedFKs = new ArrayList<>();
        movedColumns = new ArrayList<>();
    }

    /**
     * Loads all columns as well as keys.
     */
    @Override
    public void loadColumns() {
        super.loadColumns();
        loadKeys();
    }

    /**
     * Carga todas las columnas en la tabla normalmente pero a cada una le setea
     * el Change statement original a modo de backup. Se espera que las columnas
     * estén siendo cargadas a alguna UI que modifique la estructura de la tabla
     * y sus columnas.
     */
    public void loadColumnsForUI() {
        loadColumns();
        for (SQLColumn column : getColumns()) {
            column.setFirstTimeChangeStatement();
        }
    }

    /**
     * Returns the {@code ArrayList} that holds the FKs that were dropped.
     *
     * @return an {@code ArrayList} containing {@code SQLForeignKey}. If called
     * outside the context of a UI, this method may return {@code null}.
     */
    public List<SQLForeignKey> getDroppedFKs() {
        return droppedFKs;
    }

    /**
     * Returns the {@code ArrayList} that holds columns that were dropped.
     *
     * @return an {@code ArrayList} containing {@code SQLColumn}. If called
     * outside the context of a UI, this method may return {@code null}.
     */
    public List<SQLColumn> getDroppedColumns() {
        return droppedColumns;
    }

    public List<SQLColumn> getMovedColumns() {
        return movedColumns;
    }

    public boolean columnHasMoved(SQLColumn column) {
        return movedColumns.contains(column);
    }

    public void addToMovedColumns(SQLColumn column) {
        if (!movedColumns.contains(column)) {
            movedColumns.add(column);
        }
    }

    public void removeFromMovedColumns(SQLColumn column) {
        if (movedColumns.contains(column)) {
            movedColumns.remove(column);
        }
    }

    private void loadKeys() {
        foreignKeys = new ArrayList<>();
        SQLUtils.loadKeys(this);
    }

    public void addFK(SQLForeignKey fk) {
        this.foreignKeys.add(fk);
    }

    public void removeFK(int i) {
        foreignKeys.remove(i);
    }

    public List<SQLForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public SQLForeignKey getForeignKeyFromColumn(SQLColumn column) {
        if (foreignKeys == null || foreignKeys.isEmpty() || column == null || !column.isFK()) {
            return null;
        }

        for (SQLForeignKey foreignKey : foreignKeys) {
            if (foreignKey.getField().equals(column.getName())) {
                return foreignKey;
            }
        }
        return null;
    }

    public void setForeignKeys(List<SQLForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public boolean hasBrandNewColumns() {
        for (SQLColumn column : getColumns()) {
            if (column.isBrandNew()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUncommittedColumns() {
        for (SQLColumn column : getColumns()) {
            if (column.hasUncommittedChanges()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUncommittedFKs() {
        for (SQLForeignKey foreignKey : foreignKeys) {
            if (foreignKey.hasUncommittedChanges()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluates whether this table is ready to have its columns moved
     * position-wise.
     *
     * @return {@code true} if:
     * <ul>
     * <li>The table DOES NOT have uncommitted columns.</li>
     * <li>The table DOES NOT have brand new columns.</li>
     * <li>The table DOES NOT have uncommitted FKs.</li>
     * <li>The primary key HAS NOT changed.</li>
     * <li>The {@code ArrayList}s that contain dropped FKs and columns are
     * empty.</li>
     * </ul>
     */
    public boolean isReadyToMoveColumns() {
        return !hasUncommittedColumns() && !hasBrandNewColumns() && !getPrimaryKey().hasChanged()
                && !hasUncommittedFKs() && droppedFKs.isEmpty() && droppedColumns.isEmpty();
    }

    public boolean columnIsFK(SQLColumn column) {
        for (SQLForeignKey foreignKey : foreignKeys) {
            String field = foreignKey.getField();
            if (field != null && field.equals(column.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPK() {
        for (SQLColumn column : getColumns()) {
            if (column.isPK()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void commit(String... args) {
        if (args.length > 0) {
            rename(args[0]);
        }
        for (SQLColumn column : getColumns()) {
            column.commit();
        }
        getPrimaryKey().commit();
        for (SQLForeignKey foreignKey : getForeignKeys()) {
            foreignKey.commit(getName());
        }
        droppedFKs.clear();
        droppedColumns.clear();
        movedColumns.clear();
    }

    @Override
    public String getDropStatement() {
        return "DROP TABLE `" + getName() + "`";
    }

    public String getRetrievePKStatement() {
        return "SHOW KEYS FROM `" + getName() + "` WHERE Key_name = 'PRIMARY'";
    }

    public String getTruncateStatement() {
        return "TRUNCATE TABLE `" + getName() + "`";
    }

    public String getRetrieveDetailedFKsStatement() {
        String connectedDb = SQLUtils.getConnectedDBName();
        return "SELECT DISTINCT i.TABLE_NAME, i.CONSTRAINT_TYPE, i.CONSTRAINT_NAME, k.REFERENCED_TABLE_NAME, k.COLUMN_NAME, \n"
                + "k.REFERENCED_COLUMN_NAME, r.UPDATE_RULE, r.DELETE_RULE\n"
                + "FROM information_schema.TABLE_CONSTRAINTS i \n"
                + "LEFT JOIN information_schema.KEY_COLUMN_USAGE k ON i.CONSTRAINT_NAME = k.CONSTRAINT_NAME\n"
                + "LEFT JOIN information_schema.REFERENTIAL_CONSTRAINTS r ON r.CONSTRAINT_NAME = k.CONSTRAINT_NAME\n"
                + "WHERE i.CONSTRAINT_TYPE = 'FOREIGN KEY' \n"
                + "AND i.TABLE_SCHEMA = '" + connectedDb + "'\n"
                + "AND i.TABLE_NAME = '" + getName() + "';";
    }

    @Override
    public String getCreateStatement() {
        StringBuilder builder = new StringBuilder("CREATE TABLE `" + getName() + "` (");
        List<SQLColumn> columns = getColumns();

        for (int i = 0; i < columns.size(); i++) {
            builder.append(i > 0 ? ",\n" : "\n").append(columns.get(i).getCreateStatement());
        }
        SQLPrimaryKey primaryKey = getPrimaryKey();
        if (!primaryKey.isEmpty()) {
            builder.append(",\n").append(primaryKey.getAddPKsStatement(false));
        }

        for (int i = 0; i < foreignKeys.size(); i++) {
            builder.append(",\n").append(foreignKeys.get(i).getCreateStatement());
        }
        builder.append("\n)");
        builder.append("\nENGINE=InnoDB");
        builder.append("\nROW_FORMAT=COMPACT");
        return builder.toString();
    }

    @Override
    public String getRenameStatement(String newName) {
        return "RENAME TABLE `" + getName() + "` to `" + newName + "`";
    }
}
