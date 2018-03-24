package com.sqless.network;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.sqless.ui.UIGoogleWaitDialog;
import com.sqless.userdata.GoogleUser;
import com.sqless.userdata.GoogleUserManager;
import com.sqless.utils.Callback;
import com.sqless.utils.UIUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import us.monoid.web.JSONResource;

public class GoogleLogin {

    private Callback<GoogleUser> callback;
    private UIGoogleWaitDialog waitDialog;

    public GoogleLogin(Callback<GoogleUser> callback) {
        initTransportAndStore();
        this.callback = callback;
    }

    /**
     * Construye una nueva instancia de Log in. Se interactuará directamente con
     * el {@link UIGoogleWaitDialog} dado en caso de que haya algún error de
     * autenticación. Si no es necesaria la interacción entre esta clase y un
     * {@code UIGoogleWaitDialog}, es recomendable utilizar el constructor
     * normal.
     *
     * @param callback el callback que se ejecutará si la operación fue exitosa.
     * @param waitDialog el {@code UIGoogleWaitDialog} que interactuará con esta
     * clase en caso de error.
     */
    public GoogleLogin(Callback<GoogleUser> callback, UIGoogleWaitDialog waitDialog) {
        this(callback);
        this.waitDialog = waitDialog;
    }

    private void initTransportAndStore() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            serverReceiver = new LocalServerReceiver();
            if (waitDialog != null) {
                waitDialog.setServerReceiver(serverReceiver);
            }
        } catch (IOException | GeneralSecurityException t) {
            t.printStackTrace();
        }
    }

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME
            = "SQLess";

    public static final java.io.File CREDENTIALS_DIR = new java.io.File(System.getProperty("user.home"), ".credentials");

    /**
     * Directory to store user credentials for this application.
     */
    public static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/sqless");

    /**
     * Instance of the {@link FileDataStoreFactory}.
     */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Instance of the HTTP transport.
     */
    private HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/people.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES
            = Arrays.asList(Oauth2Scopes.USERINFO_PROFILE, Oauth2Scopes.USERINFO_EMAIL, Oauth2Scopes.PLUS_ME);

    private GoogleAuthorizationCodeFlow flow;

    private LocalServerReceiver serverReceiver;

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws java.io.IOException
     */
    public Credential authorize() throws IOException {
        InputStream in = GoogleLogin.class.getResourceAsStream("/google/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, serverReceiver).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public void startOauth2Service() throws IOException {
        Credential credential = authorize();
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        Userinfoplus userinfo = oauth2.userinfo().get().execute(); //esto actualiza el access token usando el refresh token si es necesario automáticamente
        if (callback != null) {
            callback.exec(new GoogleUser(userinfo.getId(), userinfo.getName(), userinfo.getEmail()));
        }

        OAuth2TokenRefreshService.startInstance(flow);

        //llamamos al backend con el access token. El backend autentica este token y crea (o no) la cuenta si es necesario.
        //NOTA: si el token no pudo ser actualizado en el paso anterior o hubo algún error con el refresh token, la exception va a saltar antes de que se
        //ejecute esta sección
        RestRequest rest = new PostRequest(RestRequest.AUTH_URL, "access_token=" + credential.getAccessToken()) {
            @Override
            public void onSuccess(JSONResource json) throws Exception {
                //si la autenticación con el backend fue exitosa, el json va a contener token_info. Si no fue exitosa, esto va a tirar una exception e ir a onFailure()
                json.get("token_info");
            }

            @Override
            public void onFailure(String message) {
                UIUtils.showErrorMessage("Autenticación Google", "Hubo un error al hacer la autenticación con Google.", null);
                if (waitDialog != null) {
                    waitDialog.cancel();
                } else {
                    GoogleUserManager.getInstance().logOut();
                }
            }
        };
        rest.exec();
    }

    public void start() {
        try {
            startOauth2Service();
        } catch (IOException e) {
            UIUtils.showErrorMessage("Autenticación Google", "Hubo un error al hacer la autenticación con Google.", null);
            waitDialog.cancel();
        } catch (NullPointerException e) {
            //va a saltar desde adentro de las librerías de Google si el usuario cancela el proceso de autenticación.
            //el proceso de cancelación consiste en darle stop() al LocalServerReceiver
            UIUtils.showMessage("Autenticación con Google", "El usuario canceló el inicio de sesión", null);
        }
    }

    public LocalServerReceiver getServerReceiver() {
        return serverReceiver;
    }
}
