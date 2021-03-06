package com.sqless.queries;

import com.sqless.sql.connection.SQLConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLSelectQuery extends SQLQuery {

    public SQLSelectQuery(String sql) {
        super(sql);
    }

    /**
     * @see SQLQuery#SQLQuery(java.lang.String, boolean)
     * @param sql
     * @param defaultErrorHandling
     */
    public SQLSelectQuery(String sql, boolean defaultErrorHandling) {
        super(sql, defaultErrorHandling);
    }

    @Override
    public void exec() {
        try {
            Connection con = SQLConnectionManager.getInstance().getConnection();
            if (!con.isClosed()) {
                statement = con.createStatement();
                ResultSet rs = statement.executeQuery(getSql());
                onSuccess(rs);
            } else {
                System.out.println("Intentando restablecer...");
                SQLConnectionManager.getInstance().restoreLostConnection();
            }
        } catch (SQLException e) {
            if (defaultErrorHandling) {
                onFailureStandard(e.getMessage());
            } else {
                onFailure(e.getMessage());
            }
        } finally {
            closeQuery();
        }
    }

    /**
     * Called upon successful completion of a query that returns a
     * {@link ResultSet}. This method is empty by default.
     *
     * @param rs The {@code ResultSet} resulting from this query.
     * @throws java.sql.SQLException If sometime during the execution of this
     * method a {@code SQLException} is thrown,
     * {@link SQLQuery#onFailure(java.lang.String)} will be called.
     */
    public void onSuccess(ResultSet rs) throws SQLException {
    }

}
