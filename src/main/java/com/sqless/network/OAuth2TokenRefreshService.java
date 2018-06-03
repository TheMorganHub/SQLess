package com.sqless.network;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import us.monoid.json.JSONObject;

/**
 * Un servicio que se activa al momento de hacer log in con Google. Su tarea
 * principal es la de refrescar el access token y id token de la credencial
 * guardada cada 55 minutos. <br>
 *
 * @author Morgan
 */
public class OAuth2TokenRefreshService {

    private Credential credential;
    private static OAuth2TokenRefreshService instance;
    private ScheduledExecutorService threadPool;
    private ScheduledFuture future; //la tarea que se va a llevar a cabo
    private DataStoreFactory dataStoreFactory;
    private String idToken;

    private OAuth2TokenRefreshService(GoogleAuthorizationCodeFlow flow) throws IOException {
        credential = flow.loadCredential("user");
        this.dataStoreFactory = flow.getCredentialDataStore().getDataStoreFactory();
        this.idToken = dataStoreFactory.getDataStore("user").get("id_token").toString();
        if (future == null) {
            start();
        }
    }

    /**
     * Se debe llamar a este método cada vez que el usuario hace log in
     * nuevamente, ya que se asume que las Credenciales guardadas cambiaron o
     * fueron eliminadas. Si ya hay una instancia andando, esa instancia se
     * sobrescribe con una nueva. No es necesario parar el
     * {@code ScheduledFuture} andando ya que el método de log out en
     * {@link GoogleUserManager} se encarga de esto.
     *
     * @param flow
     * @throws IOException
     */
    public static void startInstance(GoogleAuthorizationCodeFlow flow) throws IOException {
        if (flow == null && instance == null) {
            throw new IllegalArgumentException("Se debe dar un GoogleAuthorizationCodeFlow al inicializar esta objeto por primera vez.");
        }
        if (flow != null) {
            instance = new OAuth2TokenRefreshService(flow);
        }
    }

    /**
     * Devuelve la instancia activa de {@link OAuth2TokenRefreshService}.
     * <br><br>
     * NOTA: no usar este método si es la primera vez que se llama a esta clase.
     * Usar
     * {@link #startInstance(com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow)}
     * y luego este método.
     *
     * @return la instancia del singleton.
     */
    public static OAuth2TokenRefreshService getInstance() {
        if (instance == null) {
            return null; //El OAuth2TokenRefreshService no está inicializado. Llamar a OAuth2TokenRefreshService.startInstance(GoogleAuthrorizationCodeFlow) primero.
        }
        return instance;
    }

    /**
     * Devuelve la Credencial con el token más actualizado.
     *
     * @return
     */
    public Credential getCurrentCredential() {
        return credential;
    }

    public static boolean isRunning() {
        return instance != null;
    }

    /**
     * Inicia la tarea programada de refrescar los access token y id token. Esta
     * tarea se ejecutará con una demora inicial de cual sea el valor de
     * expiración del token y a partir de ahí cada 55 minutos. Los tokens de
     * Google duran aproximadamente 60 minutos.
     */
    public final void start() {
        threadPool = Executors.newScheduledThreadPool(1);
        RestRequest expTimeReq = new GetRequest("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + credential.getAccessToken()) {
            @Override
            public void onSuccess(JSONObject json) throws Exception {
                int expires_in = Integer.parseInt(json.get("expires_in").toString());
                System.out.println("OAuth2TokenRefreshService: ejecución iniciada con demora inicial de " + expires_in + " segundos.");
                future = threadPool.scheduleWithFixedDelay(() -> {
                    try {
                        credential.refreshToken();
                        idToken = dataStoreFactory.getDataStore("user").get("id_token").toString();

                        System.out.println("OAuth2TokenRefreshService: Access token refrescado.");
                        System.out.println("OAuth2TokenRefreshService: Id token refrescado.");
                    } catch (Exception e) {
                        System.err.println("OAuth2TokenRefreshService: " + e.getMessage() + " - El token no se pudo refrescar.");
                        stop();
                    }
                }, expires_in, 3300, TimeUnit.SECONDS);
            }

            @Override
            public void onFailure(String message) {
                System.err.println("OAuth2TokenRefreshService: " + message);
            }
        };
        expTimeReq.exec();
    }

    public final void stop() {
        threadPool.shutdown();
        future = null;
        instance = null;
        System.out.println("OAuth2TokenRefreshService: ejecución finalizada");
    }

    public String getIdToken() {
        return idToken;
    }

}
