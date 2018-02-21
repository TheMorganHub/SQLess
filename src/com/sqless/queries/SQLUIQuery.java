package com.sqless.queries;

import com.mysql.jdbc.Blob;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.ui.UIPanelResult;
import com.sqless.ui.UIQueryPanel;
import com.sqless.utils.SQLUtils;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
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
            queryTimer = new Timer(15, new ActionQueryTimer(queryPanel));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exec() {
        if (queryPanel != null) {
            queryPanel.clearMessages();
            queryStatus = Status.LOADING;
            queryPanel.updateStatusLabel(Status.LOADING);
            queryPanel.updateRowsLabel(0);
            queryPanel.enableStopBtn(true);
            queryPanel.enableRunBtn(false);
        }

        queryThread = new Thread(new Runnable() {
            private int rowsTotalAffected = 0;
            private String errorMessage = "";

            @Override
            public void run() {
                queryTimer.start();
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
                } catch (SQLException e) {
                    queryStatus = queryStatus.equals(Status.STOPPED) ? Status.STOPPED : Status.FAILED;
                    errorMessage = e.getMessage();
                } finally {
                    closeQuery();
                }

                if (queryPanel != null) {
                    queryTimer.stop();
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
        }
    }

    public void stopQuery() {
        closeQuery();
        queryStatus = Status.STOPPED;
    }

    public synchronized void fillTable(ResultSet result) {
        UIPanelResult panelResult = queryPanel.addResultPanel();
        currentTableFiller = new TableFiller(result, panelResult.getTable());
        currentTableFiller.execute();

        while (!currentTableFiller.isDone()) {
            try {
                wait(); //pauses this thread until the tablefiller finishes
            } catch (InterruptedException ex) {
                Logger.getLogger(SQLUIQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class TableFiller extends SwingWorker<Void, Vector> {

        private ResultSet rs;
        private int columnCount;
        private String[] columnNames;
        private JXTable table;
        private int rowCount;
        private boolean packed;
        private String[] typeNames;

        public TableFiller(ResultSet rs, JXTable table) {
            this.rs = rs;
            this.table = table;
        }

        @Override
        protected Void doInBackground() {
            try {
                table.setModel(makeModel(rs.getMetaData()));

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
                    if (rowCount % 1000 == 0) {
                        Thread.sleep(15); //le permite a la UI refrescar
                    }
                    publish(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
            }

            return null;
        }

        @Override
        protected void process(List<Vector> chunks) {
            for (Vector row : chunks) {
                ((DefaultTableModel) table.getModel()).addRow(row);
            }
            if (rowCount % 5000 == 0) { //will update rows every 5000
                queryPanel.updateRowsLabel(rowCount);
            }

            if (!packed) { //attempts to pack the first time this method runs
                table.packAll();
                packed = true;
            }
        }

        public DefaultTableModel makeModel(ResultSetMetaData rsmd) throws SQLException {
            DefaultTableModel model = new DefaultTableModel();
            UIPanelResult.NullSQLCellRenderer nullCellRenderer = new UIPanelResult.NullSQLCellRenderer();
            columnCount = rsmd.getColumnCount() + 1;
            columnNames = new String[columnCount];
            columnNames[0] = "#";
            typeNames = new String[columnCount];
            for (int i = 1; i < columnCount; i++) {
                columnNames[i] = rsmd.getColumnName(i);
                typeNames[i] = rsmd.getColumnTypeName(i).toLowerCase();
            }
            EventQueue.invokeLater(() -> {
                model.setRowCount(0);
                model.setColumnIdentifiers(columnNames);
                for (int i = 1; i < columnCount; i++) {
                    table.getColumn(i).setCellRenderer(nullCellRenderer);
                }
            });

            return model;
        }

        @Override
        protected void done() {
            queryPanel.updateRowsLabel();
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
    }
}
