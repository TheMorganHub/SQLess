package com.sqless.utils;

import com.sqless.ui.UIClient;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import jsyntaxpane.SyntaxDocument;

/**
 * A class that provides tools suited for text manipulation and document
 * handling.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class TextUtils {

    public static final Color DARK_GREY = new Color(0x777777);
    public static final Color BRIGHT_GREY = new Color(0xC4C4C4);

    /**
     * Empties the text contents of a given {@code JEditorPane}. We use the
     * document in order to do this so a removal event is fired.
     *
     * @param pane The {@code JEditorPane} from which to remove text.
     */
    public static void emptyDoc(JEditorPane pane) {
        Document doc = pane.getDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
        }
    }

    /**
     * Replaces the Windows-specific line ending '\r\n' with the UNIX-specific
     * '\n' to improve compatibility with operations that deal with text. This
     * method is the equivalent of calling {@code s.replaceAll("\r\n", "\n")}
     *
     * @param s The {@code String}
     *
     * @return a normalised {@code String} in UNIX format.
     */
    public static String normaliseLineEndings(String s) {
        return s.replaceAll("\r\n", "\n");
    }

    /**
     * Splits a given string into lines comprised of 15 words each. Line breaks
     * added to {@code message} by the user will be honoured and treated as
     * paragraphs.
     *
     * @param message a string.
     * @return a {@code String} split into lines.
     */
    public static String splitIntoLines(String message) {
        String[] messageSplitLineBreak = message.split("\n");
        StringBuilder mainBuilder = new StringBuilder();
        for (int i = 0; i < messageSplitLineBreak.length; i++) {
            String[] whiteSpaceSplit = messageSplitLineBreak[i].split(" ");
            StringBuilder tempBuilder = new StringBuilder();
            for (int j = 0; j < whiteSpaceSplit.length; j++) {
                tempBuilder.append(whiteSpaceSplit[j]).append(" ");
                if (j != 0 && j % 14 == 0
                        && message.length() > tempBuilder.length() + mainBuilder.length()) {
                    tempBuilder.append("\n");
                }
            }
            mainBuilder.append(tempBuilder).append("\n");
        }
        return mainBuilder.toString();
    }

    /**
     * Transforms a table into a {@code String} representation of itself.
     *
     * @param table A {@code JTable}
     * @param includeHeaders Whether to include the table's headers.
     * @param commaDelimited Cells will be separated by commas (,). If
     * {@code false} Tab '{@code \t}' will be used.
     * @param saveWhole Whether to ignore selections and attempt to save the
     * entire table.
     * @return A {@code String} representation of the table compatible with
     * table-based software such as Microsoft Excel.
     */
    public static String tableToString(JTable table, boolean includeHeaders, boolean commaDelimited, boolean saveWhole) {
        if (saveWhole) {
            table.setColumnSelectionInterval(1, table.getColumnCount() - 1);
            table.setRowSelectionInterval(0, table.getRowCount() - 1);
        }
        int[] selectedColumns = table.getSelectedColumns();
        int[] selectedRows = table.getSelectedRows();
        StringBuilder builder = new StringBuilder();
        if (includeHeaders) {
            for (int i = 0; i < selectedColumns.length; i++) {
                builder.append(table.getColumnName(selectedColumns[i]))
                        .append(i < selectedColumns.length - 1
                                ? (commaDelimited ? "," : "\t") : "");
            }
            builder.append("\r\n");
        }

        for (int rows = 0; rows < selectedRows.length; rows++) {
            for (int columns = 0; columns < selectedColumns.length; columns++) {
                builder.append(table.getModel().getValueAt(selectedRows[rows], selectedColumns[columns]))
                        .append(columns < selectedColumns.length - 1
                                ? (commaDelimited ? "," : "\t") : "");
            }
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public static void writeTableToFileAsJSON(JTable table, String filePath) {
        table.setColumnSelectionInterval(1, table.getColumnCount() - 1);
        table.setRowSelectionInterval(0, table.getRowCount() - 1);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        boolean firstRow = true;
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("[");
            StringBuilder json = new StringBuilder();
            for (int i = 0; i < rowCount; i++) {
                json.setLength(0);
                json.append("{");
                for (int j = 1; j < columnCount; j++) {
                    Object value = model.getValueAt(i, j);

                    String valueStr = value != null ? value.toString() : null;
                    String columnName = model.getColumnName(j);
                    json.append(j > 1 ? "," : "").append("\"").append(columnName).append("\":");
                    if (valueStr != null && TextUtils.isNumeric(valueStr)) {
                        if (valueStr.contains(".")) {
                            json.append(Double.parseDouble(valueStr));
                        } else if (valueStr.contains("x")) {
                            json.append(Integer.parseInt(valueStr, 16));
                        } else {
                            BigInteger bigInt = new BigInteger(valueStr);
                            if (bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                                json.append("\"").append(bigInt.toString()).append("\"");
                            } else {
                                json.append(bigInt.toString());
                            }
                        }
                    } else {
                        json.append("\"").append(valueStr).append("\"");
                    }
                }
                json.append("}");
                fw.write(firstRow ? json.toString() : ("," + json.toString()));
                firstRow = false;
            }
            fw.write("]");
            SwingUtilities.invokeLater(() -> {
                UIUtils.showMessage("Exportar tabla", "Los datos fueron exportados con éxito.", UIClient.getInstance());
                MiscUtils.openDirectory(filePath);
            });
        } catch (IOException e) {
            UIUtils.showErrorMessage("Exportar tabla", "Hubo un error al convertir la tabla a formato JSON", UIClient.getInstance());
        }
    }

    /**
     * Checks whether a string is a valid Java number. Taken from Apache
     * NumberUtils.
     *
     * @param str
     * @return {@code true} if the string is a number.
     */
    public static boolean isNumeric(final String str) {
        if (str.isEmpty()) {
            return false;
        }
        final char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0' && !str.contains(".")) { // leading 0, skip if is a decimal number
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') { // leading 0x/0X
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            } else if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                int i = start + 1;
                for (; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                    && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                    || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    /**
     * Prepends a given {@code String} prefix to a {@code SyntaxDocument} in a
     * {@code JEditorPane}.
     * <p>
     * This method knows where to prepend the prefix by querying the
     * {@code JEditorPane} for a selection start and selection end. If no
     * selection is found, the method will prepend the prefix at the line that
     * holds the caret.</p>
     * <p>
     * <b>Note:</b> this method triggers an insert update in the document.</p>
     *
     * @see TextUtils#removePrependedText(java.lang.String,
     * javax.swing.JEditorPane)
     * @param prefix a {@code String} to prepend.
     * @param area the {@code JEditorPane} that contains the
     * {@code SyntaxDocument}.
     */
    public static void prependToText(String prefix, JEditorPane area) {
        if (area.getText().isEmpty()) {
            return;
        }

        int affectedLines = 0;
        int selectStart = area.getSelectionStart();
        int selectEnd = area.getSelectionEnd();
        String[] selectedLines = getSelectedLines(area);
        StringBuilder commented = new StringBuilder();
        for (String selectedLine : selectedLines) {
            if (!selectedLine.startsWith("\n")) {
                commented.append(prefix).append(selectedLine);
                affectedLines++;
            } else {
                commented.append(selectedLine);
            }
        }

        if (affectedLines == 0) {
            area.select(selectStart, selectEnd); //restauramos seleccion original porque el método "getSelectedLines()" hace una selección interna
            return;
        }

        area.replaceSelection(commented.toString());
        area.select(selectStart + prefix.length(), selectEnd + (affectedLines * prefix.length()));
    }

    /**
     * Removes a prepended prefix from a {@code JEditorPane}'s
     * {@code SyntaxDocument}.
     * <p>
     * <b>Note:</b> this method triggers a remove update in the document.</p>
     *
     * @see TextUtils#prependToText(java.lang.String, javax.swing.JEditorPane)
     * @param prefix The {@code String} to remove.
     * @param area The {@code JEditorPane} that contains the
     * {@code SyntaxDocument}
     */
    public static void removePrependedText(String prefix, JEditorPane area) {
        if (area.getText().isEmpty()) {
            return;
        }

        int affectedLines = 0;
        int selectStart = area.getSelectionStart();
        SyntaxDocument sDoc = (SyntaxDocument) area.getDocument();
        int selectEnd = area.getSelectionEnd();
        String[] selectedLines = getSelectedLines(area);
        StringBuilder commented = new StringBuilder();
        boolean firstLineCommented = false;
        int lineCount = 0;
        for (String selectedLine : selectedLines) {
            if (selectedLine.startsWith(prefix)) {
                if (lineCount == 0) {
                    firstLineCommented = true;
                }
                commented.append(selectedLine.substring(prefix.length()));
                affectedLines++;
            } else {
                commented.append(selectedLine);
            }
            lineCount++;
        }

        if (affectedLines == 0) {
            area.select(selectStart, selectEnd); //restauramos seleccion original porque el método "getSelectedLines()" hace una selección interna
            return;
        }
        area.replaceSelection(commented.toString());
        area.select(selectStart, selectEnd);

        //si la primera linea está comentada y el comienzo de la selección no es el comienzo de la linea, 
        //va a hacer falta mover el comienzo de la selección para adelante, sino mantenemos el comienzo como estaba
        area.select(firstLineCommented && sDoc.getLineStartOffset(selectStart - prefix.length()) != selectStart ? selectStart - prefix.length() : selectStart, selectEnd - (affectedLines * prefix.length()));
    }

    /**
     * Devuelve todas las lineas seleccionadas. Por ejemplo, si un solo caracter
     * en una linea es seleccionado, este método devolverá la linea entera.
     *
     * @param pane
     * @return
     */
    public static String[] getSelectedLines(JEditorPane pane) {
        SyntaxDocument sDoc = (SyntaxDocument) pane.getDocument();
        int selStart = sDoc.getLineStartOffset(pane.getSelectionStart());
        int selEnd = sDoc.getLineEndOffset(pane.getSelectionEnd());
        String[] selectedLines = new String[0];

        try {
            selectedLines = sDoc.getText(selStart, selEnd - selStart).split("(?<=\n)");
            pane.select(selStart, selEnd);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return selectedLines;
    }

    /**
     * Checks if a specified string starts with a number.
     *
     * @param s a {@code String}.
     * @return {@code true} if the char at index 0 is a number.
     */
    public static boolean startsWithNumber(String s) {
        return Character.isDigit(s.charAt(0));
    }
}
