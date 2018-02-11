package com.sqless.utils;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Una clase que facilita la acción de insertar texto formateado en un
 * {@link JTextPane}. <br>
 * El formato dado a un texto es acumulable si es que no se utiliza
 * {@link #append(java.lang.String, com.sqless.utils.DocStyler.FontStyle, java.awt.Color)},
 * es decir, si por ejemplo el usuario acabó de insertar un texto en negrita, y
 * después decide insertar un texto en color verde, el resultado final de esto
 * será un primer texto en negrita, y el otro en verde Y negrita. Si el usuario,
 * después de esto, decide cambiar el estilo de fuente a normal, esto dará como
 * resultado un texto sin negrita, pero manteniendo el color verde. Es decir,
 * los atributos se van acumulando hasta sobrescribirse o llamar al método
 * {@link #reset()}.
 *
 * @author Morgan
 */
public class DocStyler {

    /**
     * Los estilos aplicables a una fuente.
     */
    public enum FontStyle {
        NORMAL, BOLD;
    }

    private int fontSize;
    private String fontName;
    private JTextPane pane;
    private Style mainStyle;

    /**
     * Inicializa un nuevo {@code DocStyler} para un {@code JTextPane} dado y
     * remueve cualquier estilo creado (si lo hay) por algún {@code DocStyler}
     * anterior que actuó en ese panel.
     *
     * @param pane el {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     */
    private DocStyler(JTextPane pane, String fontName, int fontSize) {
        if (pane.getStyle("MAIN_STYLE") != null) {
            pane.removeStyle("MAIN_STYLE");
        }
        mainStyle = pane.addStyle("MAIN_STYLE", null);
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.pane = pane;

        loadDefaults();
    }

    /**
     * Carga los valores default para la primera inicialización del estilo
     * principal.
     */
    private void loadDefaults() {
        StyleConstants.setForeground(mainStyle, Color.BLACK);
        StyleConstants.setFontFamily(mainStyle, fontName);
        StyleConstants.setFontSize(mainStyle, fontSize);
    }

    /**
     * Retorna un {@code DocStyler} listo para ser utilizado en el
     * {@code JTextPane} dado utilizando valores default. La fuente será "Segoe
     * UI" de tamaño 12.
     *
     * @param pane Un {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     * @return Una nueva instancia de {@code DocStyler}.
     */
    public static DocStyler of(JTextPane pane) {
        return new DocStyler(pane, "Segoe UI", 12);
    }

    /**
     * Retorna un {@code DocStyler} listo para ser utilizado en el
     * {@code JTextPane} dado utilizando valores por default para el tamaño de
     * la letra. En este caso, el usuario otorga un nombre de fuente a utilizar.
     *
     * @param pane Un {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     * @param fontName El nombre de la fuente a utilizar.
     * @return Una nueva instancia de {@code DocStyler}.
     */
    public static DocStyler of(JTextPane pane, String fontName) {
        return new DocStyler(pane, fontName, 12);
    }

    /**
     * Retorna un {@code DocStyler} listo para ser utilizado en el
     * {@code JTextPane} dado, utilizando un nombre de fuente y tamaño otorgado
     * por la persona.
     *
     * @param pane Un {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     * @param fontName El nombre de la fuente a utilizar.
     * @param fontSize Una nueva instancia de {@code DocStyler}.
     * @return Una nueva instnacia de {@code DocStyler}
     */
    public static DocStyler of(JTextPane pane, String fontName, int fontSize) {
        return new DocStyler(pane, fontName, fontSize);
    }

    /**
     * Remplaza el texto del panel manteniendo el estilo utilizado
     * anteriormente.
     *
     * @param text El texto a setear.
     * @return La misma instancia del {@code DocStyler}
     * @see #append(java.lang.String)
     */
    public DocStyler set(String text) {
        TextUtils.emptyDoc(pane);
        return append(text);
    }

    /**
     * Remplaza el texto del panel con el texto dado y utiliza el
     * {@link FontStyle} dado.
     *
     * @param text El texto a setear.
     * @param style El estilo de texto.
     * @return La misma instancia del {@code DocStyler}
     * @see #append(java.lang.String, com.sqless.utils.DocStyler.FontStyle)
     */
    public DocStyler set(String text, FontStyle style) {
        TextUtils.emptyDoc(pane);
        return append(text, style);
    }

    /**
     * Remplaza el texto del panel con el texto dado y utiliza el {@code Color}
     * dado.
     *
     * @param text El texto a setear.
     * @param color El color de texto.
     * @return La misma instancia del {@code DocStyler}
     * @see #append(java.lang.String, java.awt.Color)
     */
    public DocStyler set(String text, Color color) {
        TextUtils.emptyDoc(pane);
        return append(text, color);
    }

    /**
     * Remplaza el texto del panel con el {@link FontStyle} y {@code Color}
     * dados.
     *
     * @param text El texto a setear.
     * @param style El estilo de texto.
     * @param color El color del texto.
     * @return La misma instancia del {@code DocStyler}
     * @see #append(java.lang.String, com.sqless.utils.DocStyler.FontStyle,
     * java.awt.Color)
     */
    public DocStyler set(String text, FontStyle style, Color color) {
        TextUtils.emptyDoc(pane);
        return append(text, style, color);
    }

    /**
     * Agrega texto al panel utilizando el {@link FontStyle} y {@code Color}
     * dado.
     *
     * @param text El texto a agregar.
     * @param style El estilo de texto.
     * @param color Un color.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text, FontStyle style, Color color) {
        StyleConstants.setForeground(mainStyle, color);
        StyleConstants.setBold(mainStyle, style == FontStyle.BOLD);
        appendWithStyle(text);
        return this;
    }

    /**
     * Lleva a cabo la acción de insertar el texto dado al final del panel.
     *
     * @param text El texto a agregar.
     * @param style El {@code Style} a utilizar.
     */
    private void appendWithStyle(String text) {
        StyledDocument doc = (StyledDocument) pane.getDocument();
        try {
            doc.insertString(doc.getLength(), text, mainStyle);
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Agrega un texto al final del panel manteniendo el estilo utilizado
     * anteriormente.
     *
     * @param text El texto a agregar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text) {
        appendWithStyle(text);
        return this;
    }

    /**
     * Agrega un texto al final del panel manteniendo el color del estilo
     * anterior y cambiando sólamente el estilo de fuente.
     *
     * @param text El texto a agregar.
     * @param style El estilo de fuente a utilizar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text, FontStyle style) {
        StyleConstants.setBold(mainStyle, style == FontStyle.BOLD);
        appendWithStyle(text);
        return this;
    }

    /**
     * Agrega un texto al final del panel manteniendo el estilo de fuente y
     * cambiando sólamente el color.
     *
     * @param text El texto a agregar.
     * @param color El color a utilizar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text, Color color) {
        StyleConstants.setForeground(mainStyle, color);
        appendWithStyle(text);
        return this;
    }

    /**
     * Resetea este {@code DocStyler}.
     *
     * @return La misma instancia del {@code DocStyler}
     * @see #loadDefaults()
     */
    public DocStyler reset() {
        loadDefaults();
        return this;
    }

}
