package com.sqless.main;

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
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sqless.userdata.GoogleUser;
import com.sqless.userdata.GoogleUserManager;
import com.sqless.utils.Callback;
import com.sqless.utils.UIUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleLogin {

    private Callback<GoogleUser> callback;

    public GoogleLogin(Callback<GoogleUser> callback) {
        this();
        this.callback = callback;
    }

    private GoogleLogin() {
        initTransportAndStore();
    }

    private void initTransportAndStore() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
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
            System.getProperty("user.home"), ".credentials/gplus.googleapis.com-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/people.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES
            = Arrays.asList(PlusScopes.PLUS_ME, PlusScopes.USERINFO_PROFILE, PlusScopes.USERINFO_EMAIL);

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = GoogleLogin.class.getResourceAsStream("/google/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized G+ client service.
     *
     * @return an authorized G+ client service
     * @throws IOException
     */
    public Plus getPlusService() throws IOException {
        Credential credential = authorize();
        return new Plus.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void start() {
        try {
            Plus service = getPlusService();
            Person mePerson = service.people().get("me").execute();
            ArrayList personEmails = (ArrayList) mePerson.get("emails");
            JsonObject emailObj = new Gson().fromJson(personEmails.get(0).toString(), JsonObject.class);
            if (callback != null) {
                callback.exec(new GoogleUser(mePerson.getId(), mePerson.getDisplayName(), emailObj.get("value").getAsString()));
            }
        } catch (IOException e) {
            UIUtils.showErrorMessage("Log in OAuth2", "Hubo un error al hacer log in con Google", null);
            GoogleUserManager.getInstance().cleanUp();
            e.printStackTrace();
        }
    }

    class Wrapper {

    }

}
