package com.sqless.utils;

import com.sqless.file.FileManager;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLForeignKey;
import com.sqless.sql.objects.SQLPrimaryKey;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.UIClient;
import com.sqless.ui.UICreateTableSQLess;
import com.sqless.ui.UIExecuteFromScript;
import com.sqless.ui.listeners.TableCellListener;
import java.util.List;
import javax.swing.JOptionPane;

public class HintsManager {

    public static final int COULD_BE_PK = 12;
    public static final int COULD_BE_FK = 13;
    public static final int GUESS_DATATYPE_BY_NAME = 14;
    public static final int CREATE_TABLE_IN_EMPTY_DB = 15;
    private UICreateTableSQLess ui;
    private TableCellListener tableChangeListener;
    private SQLColumn col;

    public HintsManager(UICreateTableSQLess ui, SQLColumn col, TableCellListener tableChangeListener) {
        this.ui = ui;
        this.col = col;
        this.tableChangeListener = tableChangeListener;
    }

    public HintsManager() {
    }

    public void activate(int hint) {
        switch (hint) {
            case COULD_BE_PK:
                processCanBePK();
                break;
            case COULD_BE_FK:
                processCanBeFK();
                break;
            case GUESS_DATATYPE_BY_NAME:
                processGuessDataTypeByName();
                break;
            case CREATE_TABLE_IN_EMPTY_DB:
                processCreateTableInEmptyDb();
                break;
        }
    }

    private void processCanBePK() {
        if (col.isPK()) {
            return;
        }
        if (tableChangeListener.getNewValue().equals("id")) {
            int opt = UIUtils.showYesNoOptionDialog("Sugerencia", "SQLess ha detectado que la columna que acabas de editar podría identificar a esta tabla.\n"
                    + "¿Deseas que SQLess la transforme en una Primary Key?", JOptionPane.QUESTION_MESSAGE, false, UIClient.getInstance());
            if (opt == 0) {
                col.getParentTable().getPrimaryKey().addColumn(col);
                SQLPrimaryKey pk = col.getParentTable().getPrimaryKey();
                if (!pk.hasAutoIncrementColumn()) {
                    col.setAutoincrement(true, true);
                    ui.refreshPnlExtras();
                }
            }
        }
    }

    private void processCanBeFK() {
        if (col.isFK() || !col.getDataType().equals("int")) {
            return;
        }
        String newName = tableChangeListener.getNewValue().toString();
        SQLTable parentTable = (SQLTable) col.getParentTable();
        if (newName.startsWith("id_") && newName.length() > 3) {
            String potentialRefTableName = newName.substring(newName.indexOf("_") + 1);
            if (SQLUtils.getConnectedDB().getTables() == null) {
                SQLUtils.getConnectedDB().loadTables();
            }
            SQLTable potentialRefTable = SQLUtils.getConnectedDB().getTableByName(potentialRefTableName);
            if (potentialRefTable != null) { //encontramos una tabla potencial para poder ser unida a esta columna                
                if (potentialRefTable.getColumns() == null) {
                    potentialRefTable.loadColumns();
                }
                List<SQLColumn> potentialRefTableCols = potentialRefTable.getColumns();
                if (!potentialRefTableCols.isEmpty()) {
                    for (SQLColumn potentialRefTableCol : potentialRefTableCols) {
                        //ahora revisamos la tabla potencial para ver si tiene una columna compatible. Asumimos que si hay una columna ID, esa columna identifica a esa tabla
                        if (potentialRefTableCol.getName().equals("id") && potentialRefTableCol.isPK() && potentialRefTableCol.getDataType().equals("int")) {
                            int opt = UIUtils.showYesNoOptionDialog("Sugerencia", "SQLess ha detectado que la columna que acabas de editar podría servir para unir esta tabla con la tabla '"
                                    + potentialRefTableName + "'.\n¿Deseas que SQLess cree esta unión mediante una Foreign Key?", JOptionPane.QUESTION_MESSAGE, false, UIClient.getInstance());
                            if (opt == 0) {
                                Object requiredValForColumn = SQLUtils.fetchFirstValueForColumn(potentialRefTableCol);
                                if (requiredValForColumn == null) {
                                    UIUtils.showErrorMessage("Error", "Ha habido un error al convertir esta columna en foreign key.\n"
                                            + "Revisa que la tabla referenciada pueda ser identificada por una primary key en una columna 'id' "
                                            + "y que ese valor no sea null.", UIClient.getInstance());
                                    return;
                                }
                                SQLForeignKey newFk = new SQLForeignKey("fk_" + potentialRefTableName, parentTable.getName(),
                                        newName, potentialRefTableName, potentialRefTableCol.getName(), SQLForeignKey.RULE_CASCADE, SQLForeignKey.RULE_CASCADE);
                                newFk.isBrandNew(true);
                                parentTable.addFK(newFk);
                                col.setDefaultVal(requiredValForColumn.toString(), true);
                                col.setNullable(false);
                                col.evaluateUncommittedChanges();

                                ui.forceAddFKToUITable(newFk);
                                ui.refreshPnlExtras();
                            }
                        }
                    }
                }
            }
        }
    }

    private void processGuessDataTypeByName() {
        col.updateDataTypeByName(tableChangeListener.getNewValue().toString());
        ui.refreshPnlExtras();
    }

    private void processCreateTableInEmptyDb() {
        UIClient client = UIClient.getInstance();
        int opt = UIUtils.showOptionDialog("Sugerencia", "SQLess detectó que la base de datos está vacía.\n¿Deseas crear una tabla?", client, "Sí", "Llenar desde archivo SQL...", "No");
        switch (opt) {
            case 0:
                UICreateTableSQLess createTableUI = new UICreateTableSQLess(client.getTabPaneContent(), true);
                client.sendToNewTab(createTableUI);
                break;
            case 1:
                FileManager.getInstance().loadFile(fileContents -> {
                    UIExecuteFromScript uiExecuteFromScript = new UIExecuteFromScript(fileContents);
                    uiExecuteFromScript.start();
                });
                break;
        }
    }

}
