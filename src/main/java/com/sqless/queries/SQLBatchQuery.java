package com.sqless.queries;

import com.sqless.sql.connection.SQLConnectionManager;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLBatchQuery extends SQLQuery {

    private Connection conn;
    private int batchCount = 0;

    public SQLBatchQuery() {
    }

    public void addBatch(String sql) {
        try {
            if (conn == null) {
                conn = SQLConnectionManager.getInstance().newBatchQueryConnection();
                statement = conn.createStatement();
            }
            batchCount++;
            statement.addBatch(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exec() {
        try {
            System.out.println("Executing batch...");
            int[] updateCounts = statement.executeBatch();
            System.out.println("Finished execution.");
            onSuccess(updateCounts);
        } catch (SQLException sqlEx) {
            if (sqlEx instanceof BatchUpdateException) {
                onFailure(((BatchUpdateException) sqlEx).getUpdateCounts(), sqlEx.getMessage());
            } else {
                super.onFailure(sqlEx.getMessage());
            }
        } finally {
            closeQuery();
        }
    }

    public int getBatchCount() {
        return batchCount;
    }

    public void onSuccess(int[] updateCounts) {
    }

    public void onFailure(int[] updateCounts, String errMessage) {
    }

    @Override
    public void closeQuery() {
        try {
            if (conn != null) {
                statement.cancel();
                conn.close();
            }
        } catch (SQLException e) {
        }
    }

}
