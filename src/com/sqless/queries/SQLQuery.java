package com.sqless.queries;

import com.sqless.utils.SQLUtils;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SQLQuery {

    protected Statement statement;
    private String sql;

    public SQLQuery(String sql) {
        this.sql = SQLUtils.filterDelimiterKeyword(sql);
    }

    public SQLQuery() {
    }

    public String getSql() {
        return sql;
    }

    protected final void setSql(String sql) {
        this.sql = SQLUtils.filterDelimiterKeyword(sql);
    }

    /**
     * Called upon execution failure of a query. This method is empty by
     * default. Children are free to override it as they please.
     *
     * @param errMessage The error message produced by the SQL engine.
     */
    public void onFailure(String errMessage) {
    }

    /**
     * Executes this query.
     */
    public abstract void exec();

    /**
     * Closes this SQL query's {@link Statement} object.
     */
    public void closeQuery() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
        }
    }
}
