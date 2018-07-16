package com.sqless.sql.connection;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import com.sqless.settings.UserPreferencesLoader;
import com.sqless.utils.UIUtils;
import com.sqless.ui.UIConnectionWizard;
import com.sqless.sql.objects.SQLDatabase;
import com.sqless.ui.GenericWaitingDialog;
import com.sqless.ui.UIClient;
import java.awt.Frame;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

/**
 * This class manages everything related to SQLess' connection with the SQLDB
 * engine. Whenever a connection is made, this class' {@code SQLDatabase} object
 * will hold the database to which SQLess is currently connected.
 * <p>
 * A lot of the methods in this class are mirrored by {@code SQLUtils} for the
 * sake of convenience, always referencing this class.</p>
 * <p>
 * This class follows the singleton pattern, because there will only be one set
 * of drivers loaded and one connection to the engine.</p>
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SQLConnectionManager {

    private static final SQLConnectionManager INSTANCE = new SQLConnectionManager();
    private Connection connection;
    private SQLDatabase connectedDB;
    private String username;
    private String password;
    private String hostName;
    private String port;
    private String serverHostName;
    private GenericWaitingDialog restoreDialog;

    public Connection newQueryConnection() {
        Connection newCon = null;
        try {
            DriverManager.setLoginTimeout(5);
            newCon = DriverManager.getConnection("jdbc:mysql://" + hostName + ":" + port + "/" + connectedDB.getName()
                    + "?zeroDateTimeBehavior=convertToNull&allowMultiQueries=true", username, password);
        } catch (SQLException e) {
            System.err.println("Connection timed out.");
        }
        return newCon;
    }

    public void restoreLostConnection() {
        restoreDialog = new GenericWaitingDialog("La conexión con el servidor se perdió. Intentando restablecer...");
        restoreDialog.display(() -> {
            SwingUtilities.invokeLater(() -> UIClient.getInstance().setCursor(UIUtils.WAIT_CURSOR));
            setNewConnectionNoRepair(connectedDB.getName(), UIClient.getInstance());
            UIClient.getInstance().refreshEntireJTree();
            SwingUtilities.invokeLater(() -> UIClient.getInstance().setCursor(UIUtils.DEFAULT_CURSOR));
        });
    }

    public Connection newBatchQueryConnection() {
        Connection newCon = null;
        try {
            DriverManager.setLoginTimeout(3);
            newCon = DriverManager.getConnection("jdbc:mysql://" + hostName + ":" + port + "/" + connectedDB.getName()
                    + "", username, password);
        } catch (SQLException e) {
        }
        return newCon;
    }

    private boolean connectToDatabase(String dbName, String username, String password,
            String hostName, String port, Frame parent) {
        try {
            closeConnection();
            long start = System.currentTimeMillis();
            DriverManager.setLoginTimeout(3);
            connection = DriverManager.getConnection("jdbc:mysql://" + hostName + ":" + port + "/" + dbName
                    + "?zeroDateTimeBehavior=convertToNull", username, password);

            long elapsed = System.currentTimeMillis() - start;
            this.hostName = hostName;
            this.username = username;
            this.password = password;
            this.port = port;
            System.out.println("[ConnectionManager]: Connected to " + dbName + " at " + hostName + ":" + port
                    + " as " + username + " in " + elapsed + "ms");
            return true;
        } catch (SQLException e) {
            if (restoreDialog != null && restoreDialog.isVisible()) {
                restoreDialog.dispose();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println("La conexión no pudo cerrarse.");
            }
            UIUtils.showErrorMessage("Error al conectar al motor de base de datos", e.getMessage(), parent);
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean connectionIsClosed() {
        try {
            if (connection != null && connection.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Attempts to connect to a database using the saved host in
     * {@code UserPreferencesLoader}.
     *
     * @param dbName The name of the DB to connect through the saved host.
     * @param parent The parent {@code Frame} that will display any errors that
     * occur within this operation.
     */
    private void connectToSavedHost(String dbName, boolean dbIsBrandNew, boolean showRepairConnection, Frame parent) {
        UserPreferencesLoader userPrefs = UserPreferencesLoader.getInstance();
        String hostName = userPrefs.getProperty("Connection.Host");
        String port = userPrefs.getProperty("Connection.Port");
        String username = userPrefs.getProperty("Connection.Username");
        String password = userPrefs.getProperty("Connection.Password");
        boolean success = connectToDatabase(dbName, username, password, hostName, port, parent);
        if (success) {
            connectedDB = dbIsBrandNew ? new SQLDatabase(dbName, true) : new SQLDatabase(dbName);
            if (!showRepairConnection) { //este parámetro solo está en falso cuando este método es llamado mediante restoreLostConnection
                if (restoreDialog != null && restoreDialog.isVisible()) {
                    restoreDialog.dispose();
                    UIUtils.showMessage("Aviso", "La conexión fue restablecida.", UIClient.getInstance());
                }
            }
        } else {
            if (showRepairConnection) {
                UIConnectionWizard fixConnection = new UIConnectionWizard(parent,
                        UIConnectionWizard.Task.REPAIR);
                fixConnection.setVisible(true);
                connectToSavedHost(dbName, dbIsBrandNew, true, parent);
            }
        }
    }

    /**
     * Convenience method for {@link #setNewConnection(String, boolean, Frame)}.
     * The DB will not be considered brand new.
     *
     * @param dbName The DB name to connect to.
     * @param parent The {@code Frame} that will display any errors that occur
     * within this operation.
     */
    public void setNewConnection(String dbName, Frame parent) {
        connectToSavedHost(dbName, false, true, parent);
    }

    public void setNewConnectionNoRepair(String dbName, Frame parent) {
        connectToSavedHost(dbName, false, false, parent);
    }

    public void setNewConnectionNoRepair(String dbName, boolean dbIsBrandNew, Frame parent) {
        connectToSavedHost(dbName, dbIsBrandNew, false, parent);
    }

    /**
     * Closes any active connection and sets a new one. Use this method to start
     * a connection with any database using the saved host. This method makes a
     * call to {@code private} method
     * {@link #connectToSavedHost(java.lang.String, java.awt.Frame)}; this takes
     * care of any and all operations involved in connecting a client to a DB
     * engine as well as error handling.
     *
     * @param dbName The name of the DB to which the client wants to connect.
     * @param dbIsBrandNew Whether the DB to connect to is brand new.
     * @param parent The {@code Frame} that will display any errors that occur
     * within this operation.
     */
    public void setNewConnection(String dbName, boolean dbIsBrandNew, Frame parent) {
        connectToSavedHost(dbName, dbIsBrandNew, true, parent);
    }

    /**
     * Tests a specified connection by attempting to connect to database "mysql"
     * in MySQL using a temporary {@code Connection} object. If successful, the
     * {@code Connection} is then promptly closed.
     *
     * @param username
     * @param password
     * @param hostName
     * @param port
     * @param parent
     * @return {@code true} if the connection to the host name was successful,
     * {@code false} otherwise.
     */
    public boolean testConnection(String username, String password, String hostName, String port, Frame parent) {
        try (Connection testCon = DriverManager.getConnection("jdbc:mysql://" + hostName + ":" + port + "/mysql", username, password)) {
            System.out.println("Testing connection at " + hostName + ":" + port + " as " + username);
            System.out.println("Test successful.");
            return true;
        } catch (SQLException e) {
            UIUtils.showErrorMessage("Error al conectar al motor de base de datos", e.getMessage(), parent);
            System.out.println("Test failed.");
            System.err.println(e.getMessage());
        }
        return false;
    }

    public String getServerHostname() {
        if (serverHostName != null) {
            return serverHostName;
        }
        SQLQuery hostNameQuery = new SQLSelectQuery("SELECT @@hostname") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                serverHostName = rs.next() ? rs.getString(1) : "[Unavailable hostname]";
            }
        };
        hostNameQuery.exec();
        return serverHostName;
    }

    public String getClientHostname() {
        String hostname = "Unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException ex) {
            System.err.println("Hostname can not be resolved");
        }
        return hostname;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[ConnectionManager]: Closed connection with database " + connectedDB.getName());
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public SQLDatabase getConnectedDB() {
        return connectedDB;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPort() {
        return port;
    }

    public static SQLConnectionManager getInstance() {
        return INSTANCE;
    }

}
