package com.sqless.queries;

import com.sqless.utils.UIUtils;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SQLQuery {

    protected Statement statement;
    private String sql;
    /**
     * Si es falso, al haber un error en la query, el error no será mostrado en
     * pantalla y se asume que el usuario dio una implementación propia de
     * {@link #onFailure(java.lang.String)}. Si es verdadero, SQLess mostrará un
     * diálogo de error por default con texto sacado de la excepción. <br><br>
     * Nota: este campo es ignorado por {@link SQLUIQuery}.
     */
    protected boolean defaultErrorHandling;

    public SQLQuery() {
    }

    public SQLQuery(String sql) {
        this.sql = sql;
    }

    public void filterDelimiterKeyword() {
        this.sql = filterDelimiterKeyword(sql);
    }

    /**
     * Inicializa una nueva query. Usar este constructor si se desea que al
     * fallar la query se muestre un mensaje por default sin tener que
     * implementar {@link #onFailure(java.lang.String)}
     *
     * @param sql
     * @param defaultErrorHandling
     */
    public SQLQuery(String sql, boolean defaultErrorHandling) {
        this(sql);
        this.defaultErrorHandling = defaultErrorHandling;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Called upon execution failure of a query IF {@code defaultErrorHandling}
     * is false. This method is empty by default. Children are free to override
     * it as they please.
     *
     * @param errMessage The error message produced by the SQL engine.
     */
    public void onFailure(String errMessage) {
    }

    public void onFailureStandard(String errMessage) {
        UIUtils.showErrorMessage("Error", errMessage, null);
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

    /**
     * Filtra la palabra clave 'DELIMITER' y todos los delimitadores que no sean
     * ';' asociados a esa palabra clave en la query dada ya que no es válida en
     * JDBC.<br>
     * Por ejemplo: <br><pre>
     * <code>
     * DELIMITER $$ <br>
     *
     * CREATE PROCEDURE film_not_in_stock(IN p_film_id INT, IN p_store_id INT,
     * OUT p_film_count INT) READS SQL DATA <br>
     * BEGIN <br>
     * SELECT inventory_id FROM inventory WHERE film_id = p_film_id AND store_id
     * = p_store_id AND NOT inventory_in_stock(inventory_id); <br>
     *
     * SELECT FOUND_ROWS() INTO p_film_count; <br>
     * END $$
     * </code></pre>
     * <br>
     * <br>
     * Se convertirá en: <br>
     * <pre><code>
     * CREATE PROCEDURE film_not_in_stock(IN p_film_id INT, IN p_store_id INT,
     * OUT p_film_count INT) READS SQL DATA <br>
     * BEGIN <br>
     * SELECT inventory_id FROM inventory WHERE film_id = p_film_id AND store_id
     * = p_store_id AND NOT inventory_in_stock(inventory_id); <br>
     *
     * SELECT FOUND_ROWS() INTO p_film_count; <br>
     * END ;
     * </code></pre>
     *
     * @param sql La query SQL a filtrar.
     * @return La misma query SQL sin nada relacionado a {@code DELIMITER}. Si
     * la query original no hace uso de la palabra clave {@code DELIMITER} será
     * retornada sin modificaciones.
     */
    private static String filterDelimiterKeyword(String sql) {
        Pattern delimiterPat = Pattern.compile("DELIMITER *(\\S*)");
        Matcher matcher = delimiterPat.matcher(sql);
        Set<String> delimitersToReplace = new HashSet<>();
        while (matcher.find()) {
            String delimiter = matcher.group(1);
            if (!delimiter.equals(";")) {
                //delimitadores que necesitan ser escapados antes de ser aplicados en expresiones regulares
                if (delimiter.contains("/") || delimiter.contains("$")) {
                    StringBuilder escapedDelimiters = new StringBuilder();
                    for (int i = 0; i < delimiter.length(); i++) {
                        if (delimiter.charAt(i) == '/' || delimiter.charAt(i) == '$') {
                            escapedDelimiters.append('\\').append(delimiter.charAt(i));
                        }
                    }
                    delimiter = escapedDelimiters.toString();
                }
                delimitersToReplace.add(delimiter);
            }
        }

        if (delimitersToReplace.isEmpty()) {
            return sql;
        }
        String replaced = sql.replaceAll(String.join("|", delimitersToReplace), ";");
        return replaced.replaceAll("DELIMITER *(\\S*)", "");
    }
}
