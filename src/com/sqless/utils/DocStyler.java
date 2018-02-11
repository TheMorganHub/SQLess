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

    private static final int DEFAULT_FONT_SIZE = 12;
    private JTextPane pane;
    private Style lastUsedStyle;
    private final Style DEFAULT_STYLE;

    /**
     * Inicializa un nuevo {@code DocStyler} para un {@code JTextPane} dado.
     *
     * @param pane el {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     */
    private DocStyler(JTextPane pane) {
        this.pane = pane;
        DEFAULT_STYLE = pane.addStyle("DEFAULT_STYLE", null);
        StyleConstants.setForeground(DEFAULT_STYLE, Color.BLACK);
        StyleConstants.setFontFamily(DEFAULT_STYLE, "Segoe UI");
        StyleConstants.setFontSize(DEFAULT_STYLE, DEFAULT_FONT_SIZE);
    }

    /**
     * Retorna un {@code DocStyler} listo para ser utilizado en el
     * {@code JTextPane} dado.
     *
     * @param pane Un {@code JTextPane} al cual se le aplicará el texto
     * formateado.
     * @return Una nueva instancia de {@code DocStyler}.
     */
    public static DocStyler of(JTextPane pane) {
        return new DocStyler(pane);
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
        String newStyleName = createStyleName(style, color);
        Style newStyle = pane.addStyle(newStyleName, null);
        StyleConstants.setForeground(newStyle, color);
        StyleConstants.setBold(newStyle, style == FontStyle.BOLD);
        StyleConstants.setFontFamily(newStyle, "Segoe UI");
        StyleConstants.setFontSize(newStyle, DEFAULT_FONT_SIZE);
        appendWithStyle(text, newStyle);
        return this;
    }

    /**
     * Lleva a cabo la acción de insertar el texto dado al final del panel.
     * <br><br>
     * Dada la naturaleza casi inmutable (su nombre) de un {@code Style},
     * removemos el {@code Style} del panel inmediatamente luego de usarlo para
     * evitar conflictos a futuro. De todas formas, el {@code Style} que
     * acabamos de utilizar se guardará en memoria para uso/modificación futura.
     *
     * @param text El texto a agregar.
     * @param style El {@code Style} a utilizar.
     */
    private void appendWithStyle(String text, Style style) {
        StyledDocument doc = (StyledDocument) pane.getDocument();
        try {
            doc.insertString(doc.getLength(), text, style);
            lastUsedStyle = style;
            pane.removeStyle(lastUsedStyle.getName());
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Agrega un texto al final del panel manteniendo el estilo utilizado
     * anteriormente. De no haberse utilizado ningún estilo, se hará uso del
     * estilo default.
     *
     * @param text El texto a agregar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text) {
        appendWithStyle(text, lastUsedStyle == null ? DEFAULT_STYLE : lastUsedStyle);
        return this;
    }

    /**
     * Agrega un texto al final del panel manteniendo el color del estilo
     * anterior y cambiando sólamente el estilo de fuente. De no haberse
     * utilizado ningún estilo, se utilizará el estilo de fuente dado y el color
     * negro.
     *
     * @param text El texto a agregar.
     * @param style El estilo de fuente a utilizar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text, FontStyle style) {
        if (lastUsedStyle != null) {
            StyleConstants.setBold(lastUsedStyle, style == FontStyle.BOLD);
            return append(text);
        }
        return append(text, style, Color.BLACK);
    }

    /**
     * Agrega un texto al final del panel manteniendo el estilo de fuente y
     * cambiando sólamente el color. De no haberse utilizado ningún estilo, se
     * utilizará el estilo de fuente {@code FontStyle.NORMAL} y el color dado.
     *
     * @param text El texto a agregar.
     * @param color El color a utilizar.
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler append(String text, Color color) {
        if (lastUsedStyle != null) {
            StyleConstants.setForeground(lastUsedStyle, color);
            return append(text);
        }
        return append(text, FontStyle.NORMAL, color);
    }

    private String createStyleName(FontStyle style, Color color) {
        return style.toString() + color.getRGB();
    }

    /**
     * Resetea este {@code DocStyler}. Es decir, seteando como {@code null} al
     * último estilo utilizado.
     *
     * @return La misma instancia del {@code DocStyler}
     */
    public DocStyler reset() {
        lastUsedStyle = null;
        return this;
    }

}
