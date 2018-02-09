package com.sqless.queries;

import com.sqless.sql.connection.SQLConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A class that represents a query that doesn't return ResultSets and performs
 * UPDATE/DELETE/INSERT operations on a table. In other words, DDL operations.
 * This class utilises a standalone connection from which to create the query
 * statement, this is due to potential errors that could arise if using SQLess'
 * main connection to the engine.
 * <br><br>
 * Note: queries of this class are executed on the same thread this object is
 * created. This is intentional. Exercise caution when running queries that
 * could take a long time, especially if called from within Swing's <i>Event
 * Dispatch Thread</i> as this could freeze the UI.
 *
 * @author Morgan
 */
public class SQLUpdateQuery extends SQLQuery {

    private Connection standaloneUpdateConnection;

    public SQLUpdateQuery(String sql) {
        super(sql);
    }

    @Override
    public void exec() {
        try {
            standaloneUpdateConnection = SQLConnectionManager.getInstance().newQueryConnection();
            statement = standaloneUpdateConnection.createStatement();
            int affectedRows = statement.executeUpdate(getSql());
            onSuccess(affectedRows);
        } catch (SQLException ex) {
            onFailure(ex.getMessage());
        } finally {
            closeQuery();
        }
    }
    
    /**
     * Called upon successful completion of a query. This method is empty by
     * default. Children that inherit from this class have the option to
     * override it. <br><br>
     * Note: overriding this method on children of this class that return a
     * {@code ResultSet} will have no effect, instead please override
     * {@link SQLQuery#onSuccess(java.sql.ResultSet)}.
     *
     * @param updateCount for Update queries, the number of rows that were
     * updated by this query.
     */
    public void onSuccess(int updateCount) {
    }

    @Override
    public void closeQuery() {
        super.closeQuery();
        try {
            if (standaloneUpdateConnection != null) {
                standaloneUpdateConnection.close();
            }
        } catch (SQLException e) {
        }
    }
}
