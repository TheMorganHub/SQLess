package com.sqless.network;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Un servicio que se activa al momento de hacer log in con Google. Su tarea
 * principal es la de refrescar el access token de la credencial guardada cada
 * 50 minutos. <br>
 * A tener en cuenta: los tokens generados por este servicio no son persistidos
 * en el dispositivo local, sino que son almacenados de manera temporaria en
 * memoria mientras la aplicación corre por si el usuario desea hacer una
 * llamada al backend. Una vez que el usuario cierre y vuelva abrir la
 * aplicación, es en ese momento en que un nuevo token actualizado será
 * persistido localmente
 *
 * @author Morgan
 */
public class OAuth2TokenRefreshService {

    private Credential credential;
    private static OAuth2TokenRefreshService instance;
    private ScheduledExecutorService threadPool;
    private ScheduledFuture future; //la tarea que se va a llevar a cabo

    private OAuth2TokenRefreshService(GoogleAuthorizationCodeFlow flow) throws IOException {
        credential = flow.loadCredential("user");
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
     * Inicia la tarea programada de refrescar el token de acceso. Esta tarea se
     * ejecutará después de 50 minutos de haber hecho log in y a partir de ahí
     * cada 50 minutos. Los tokens de accesso de Google duran aproximadamente 60
     * minutos.
     */
    public final void start() {
        System.out.println("OAuth2TokenRefreshService: ejecución iniciada");
        threadPool = Executors.newScheduledThreadPool(1);
        future = threadPool.scheduleWithFixedDelay(() -> {
            try {
                credential.refreshToken();
                System.out.println("OAuth2TokenRefreshService: Access token refrescado - " + credential.getAccessToken());
            } catch (Exception e) {
                System.err.println("OAuth2TokenRefreshService: " + e.getMessage() + " - El token no se pudo refrescar.");
                stop();
            }
        }, 50, 50, TimeUnit.MINUTES);
    }

    public final void stop() {
        threadPool.shutdown();
        future = null;
        instance = null;
        System.out.println("OAuth2TokenRefreshService: ejecución finalizada");
    }

}
