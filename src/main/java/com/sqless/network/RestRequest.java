package com.sqless.network;

import com.sqless.utils.UIUtils;
import java.awt.EventQueue;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public abstract class RestRequest {

    protected String url;
    protected Resty rest;

    public RestRequest(String url) {
        this.rest = new Resty();
        this.url = url;
    }

    /**
     * Es llamado por {@link #executePostExec(us.monoid.web.JSONResource)} al
     * completarse el request contra el servidor sin errores.
     *
     * @param json
     * @throws Exception si hubo algún error dentro de este método. Por ejemplo,
     * si el usuario manipula el JSON de manera erronea. Todas las excepciones
     * de este método serán atrapadas por {@link #onFailure(java.lang.String)}
     */
    public void onSuccess(JSONResource json) throws Exception {
    }

    /**
     * Es llamado por {@link #executePostExec(us.monoid.web.JSONResource)} al
     * completarse el request contra el servidor <b>exitosamente</b> y si la
     * autenticación del token de usuario fue exitosa.
     *
     * @param json
     * @throws Exception si hubo algún error dentro de este método. Por ejemplo,
     * si el usuario manipula el JSON de manera erronea. Todas las excepciones
     * de este método serán atrapadas por {@link #onFailure(java.lang.String)}
     */
    public void onTokenSuccess(JSONResource json) throws Exception {
    }

    /**
     * Si hubo alguna excepción al mandar el request contra el servidor o si el
     * usuario manipuló de forma erronea el JSON del request.
     *
     * @param message El mensaje de error.
     */
    public void onFailure(String message) {
        UIUtils.showErrorMessage("Request error", message, null);
    }

    /**
     * Cada request se ejecuta de distinta forma, pero las tareas post-ejecución
     * siempre son iguales, es por eso que toda implementación de
     * {@link #exec()} debe llamar a este método y pasarle el JSON resultante.
     * Es en este método en donde se ejecutarán los callback dados dependiendo
     * el resultado de la ejecución. <br><br>
     * Nota: este método se llamará dentro del <i>Event Dispatch Thread</i> de
     * Swing.
     *
     * @see #onSuccess(us.monoid.web.JSONResource)
     * @see #onTokenSuccess(us.monoid.web.JSONResource)
     * @see #onFailure(java.lang.String)
     *
     * @param json El JSON resultante que el servidor devolvió al ser ejecutado
     * el request.
     */
    protected final void executePostExec(JSONResource json) {
        EventQueue.invokeLater(() -> {
            try {
                onSuccess(json);
                int tokenStatus = Integer.parseInt(json.get("user_id").toString());
                if (tokenStatus != -1) {
                    onTokenSuccess(json);
                }
            } catch (Exception e) {
                onFailure(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Ejecuta este HTTP request contra el servidor. Para el correcto
     * funcionamiento de este método, es indispensable que toda implementación
     * de éste incluya una llamada a
     * {@link #executePostExec(us.monoid.web.JSONResource)}.
     */
    public abstract void exec();
}
