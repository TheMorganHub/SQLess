package com.sqless.queries;

import com.mysql.jdbc.Blob;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.ui.UIPanelResult;
import com.sqless.ui.UIQueryPanel;
import com.sqless.utils.SQLUtils;
import com.sqless.utils.UIUtils;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;

public class SQLUIQuery extends SQLQuery {

    private Status queryStatus;
    private UIQueryPanel queryPanel;
    private Thread queryThread;
    private Timer queryTimer;
    private Connection queryConnection;
    private TableFiller currentTableFiller;

    public enum Status {
        LOADING, STOPPED, SUCCESSFUL, FAILED;
    }

    public SQLUIQuery(String sql, UIQueryPanel queryPanel) {
        super(sql);
        try {
            queryConnection = SQLConnectionManager.getInstance().newQueryConnection();
            statement = queryConnection.createStatement();
            this.queryPanel = queryPanel;
            queryTimer = new Timer(75, new ActionQueryTimer(queryPanel));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exec() {
        long startTime = System.currentTimeMillis();
        if (queryPanel != null) {
            queryPanel.clearMessages();
            queryStatus = Status.LOADING;
            queryPanel.updateStatusLabel(Status.LOADING);
            queryPanel.updateRowsLabel(0);
            queryPanel.enableStopBtn(true);
            queryPanel.enableRunBtn(false);
            queryTimer.start();
        }

        queryThread = new Thread(new Runnable() {
            private int rowsTotalAffected = 0;
            private String errorMessage = "";

            @Override
            public void run() {
                try {
                    boolean hasResult;
                    int updateCount = 0;
                    hasResult = statement.execute(getSql());
                    while (hasResult || (updateCount = statement.getUpdateCount()) != -1) {
                        if (hasResult) {
                            fillTable(statement.getResultSet());
                        }
                        hasResult = statement.getMoreResults();
                        rowsTotalAffected += updateCount;
                    }
                    queryStatus = Status.SUCCESSFUL;
                } catch (SQLException | InterruptedException e) {
                    queryStatus = queryStatus.equals(Status.STOPPED) ? Status.STOPPED : Status.FAILED;
                    errorMessage = e.getMessage();
                } finally {
                    closeQuery();
                }

                if (queryPanel != null) {
                    queryTimer.stop();
                    long elapsed = System.currentTimeMillis() - startTime;
                    if (elapsed < 75) {
                        ((ActionQueryTimer) queryTimer.getActionListeners()[0]).forceTime(elapsed);
                    }
                    EventQueue.invokeLater(() -> {
                        queryPanel.enableStopBtn(false);
                        queryPanel.enableRunBtn(true);
                        queryPanel.updateStatusLabel(queryStatus);
                        queryPanel.setMessage(queryStatus, errorMessage);
                    });
                }
            }
        });
        queryThread.start();
    }

    @Override
    public void closeQuery() {
        try {
            if (statement != null) {
                statement.cancel();
                queryConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interrumpe la query que se está corriendo actualmente. Este método se
     * ejecuta en un Thread aparte ya que si la query tarda en cerrar, la UI se
     * freezará. Una query puede tardar en cerrar si el ResultSet que devuelve
     * es gigante.
     */
    public void stopQuery() {
        Thread closeThread = new Thread(() -> {
            closeQuery();
            queryStatus = Status.STOPPED;
        });
        closeThread.start();
    }

    public synchronized void fillTable(ResultSet result) throws InterruptedException {
        currentTableFiller = new TableFiller(result);
        currentTableFiller.execute();
        if (!currentTableFiller.isDone()) {
            wait();
        }
    }

    private class TableFiller extends SwingWorker<Void, Vector> {

        private ResultSet rs;
        private int columnCount;
        private JXTable table;
        private int rowCount;
        private String[] typeNames;
        static final int MAX_ROWS = 1000000;
        private DefaultTableModel newTableModel;
        private UIPanelResult uiPanelResult;

        public TableFiller(ResultSet rs) {
            this.rs = rs;
            uiPanelResult = queryPanel.newResultPanel();
            this.table = uiPanelResult.getTable();
        }

        @Override
        protected Void doInBackground() {
            try {
                if (!rs.isClosed()) {
                    newTableModel = makeModel(rs.getMetaData());
                }

                while (!rs.isClosed() && rs.next()) {
                    Vector row = new Vector();
                    row.add("" + ++rowCount);
                    for (int columnIndex = 1; columnIndex < columnCount; columnIndex++) {
                        String columnType = typeNames[columnIndex];
                        String cellValue = rs.getString(columnIndex);
                        if (cellValue != null) {
                            if (columnType.equalsIgnoreCase("year")) {
                                cellValue = SQLUtils.parseSQLYear(cellValue);
                            } else if (columnType.equalsIgnoreCase("blob")) {
                                cellValue = SQLUtils.parseBlob((Blob) rs.getBlob(columnIndex));
                            } else if (SQLUtils.dataTypeIsTimeBased(columnType)) {
                                cellValue = cellValue.endsWith(".0") ? cellValue.substring(0, cellValue.length() - 2) : cellValue;
                            }
                        }
                        row.add(cellValue);
                    }
                    newTableModel.addRow(row);
                    if (rowCount == MAX_ROWS) {
                        queryTimer.stop();
                        UIUtils.showWarning("Número máximo de filas excedido", "El número máximo de filas (" + MAX_ROWS + ") que SQLess puede mostrar para esta query será excedido. "
                                + "SQLess mostrará los resultados hasta ahora y pasará a la siguiente query en cola.", null);
                        break;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        public DefaultTableModel makeModel(ResultSetMetaData rsmd) throws SQLException {
            DefaultTableModel model = new DefaultTableModel();
            columnCount = rsmd.getColumnCount() + 1;
            model.addColumn("#");
            typeNames = new String[columnCount];
            for (int i = 1; i < columnCount; i++) {
                typeNames[i] = rsmd.getColumnTypeName(i).toLowerCase();
                model.addColumn(rsmd.getColumnName(i));
            }
            return model;
        }

        @Override
        protected void done() {
            if (newTableModel != null) {
                UIPanelResult.NullSQLCellRenderer nullCellRenderer = new UIPanelResult.NullSQLCellRenderer();
                queryPanel.addResultPanel(uiPanelResult);
                table.setModel(newTableModel);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    TableColumn tableColumn = table.getColumn(i);
                    tableColumn.setMinWidth(i == 0 ? 10 : 20);
                    tableColumn.setPreferredWidth(i == 0 ? 25 : 100);
                    tableColumn.setCellRenderer(nullCellRenderer);
                }
                queryPanel.updateRowsLabel();
            }

            synchronized (SQLUIQuery.this) {
                SQLUIQuery.this.notify();
            }
        }
    }

    private class ActionQueryTimer implements ActionListener {

        private static final int MSECS_PER_SEC = 1000;
        private static final int SECS_PER_MIN = 60;
        private static final int MIN_PER_HR = 60;
        private static final String TIME_FORMAT = "%02d:%02d:%02d,%03d";
        private long startTime;
        private UIQueryPanel queryPanel;

        public ActionQueryTimer(UIQueryPanel queryPanel) {
            this.queryPanel = queryPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (startTime == 0L) {
                startTime = System.currentTimeMillis();
                return;
            }

            long currentTime = System.currentTimeMillis();
            int diffTime = (int) (currentTime - startTime);

            int mSecs = diffTime % MSECS_PER_SEC;
            diffTime /= MSECS_PER_SEC;

            int sec = diffTime % SECS_PER_MIN;
            diffTime /= SECS_PER_MIN;

            int min = diffTime % MIN_PER_HR;
            diffTime /= MIN_PER_HR;

            int hours = diffTime;

            String time = String.format(TIME_FORMAT, hours, min, sec, mSecs);
            queryPanel.updateTimerLabel(time);
        }

        public void forceTime(long ms) {
            String time = String.format(TIME_FORMAT, 0, 0, 0, ms);
            queryPanel.updateTimerLabel(time);
        }
    }
}