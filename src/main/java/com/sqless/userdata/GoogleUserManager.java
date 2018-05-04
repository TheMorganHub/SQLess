package com.sqless.userdata;

import com.sqless.file.FileManager;
import com.sqless.network.GetRequest;
import com.sqless.network.GoogleLogin;
import com.sqless.network.OAuth2TokenRefreshService;
import com.sqless.network.RestRequest;
import com.sqless.ui.UIGoogleWaitDialog;
import com.sqless.utils.Callback;
import com.sqless.utils.UIUtils;
import java.io.IOException;

public class GoogleUserManager {

    private static GoogleUserManager instance;
    private GoogleUser active;

    public static GoogleUserManager getInstance() {
        if (instance == null) {
            instance = new GoogleUserManager();
        }
        return instance;
    }

    public void addNew(GoogleUser user) {
        System.out.println("Added new Google user: " + user);
        this.active = user;
    }

    public GoogleUser getActive() {
        return active;
    }

    public void logIn(Callback<GoogleUser> callback, UIGoogleWaitDialog waitDialog) {
        GoogleLogin login = new GoogleLogin(googleUser -> {
            addNew(googleUser);
            callback.exec(active);
        }, waitDialog, GoogleLogin.Type.FIRST_LOGIN);
        login.start();
    }

    public void authenticateStoredCredentials(Callback<GoogleUser> callback, UIGoogleWaitDialog waitDialog) {
        if (active == null) {
            if (credentialsExistLocally()) {
                GoogleLogin login = new GoogleLogin(person -> {
                    addNew(person);
                    callback.exec(active);
                }, waitDialog, GoogleLogin.Type.LOCAL_CREDENTIAL_AUTHENTICATION);
                login.start();
            }
        }
    }

    public boolean credentialsExistLocally() {
        return FileManager.dirOrFileExists(GoogleLogin.CREDENTIALS_DIR.getPath()) && !FileManager.isDirEmpty(GoogleLogin.CREDENTIALS_DIR.getPath());
    }

    /**
     * Hace un log out con Google. Esto se logra enviando un request GET a los
     * servidores de Google con el access token, eliminando la carpeta que
     * contiene las Credenciales de Google de la PC y seteando el usuario activo
     * a null. Al eliminar las credenciales guardadas, la próxima vez que el
     * usuario intente iniciar sesión con Google se abrirá el navegador. No nos
     * importa la respuesta que nos pueda llegar a dar Google, ya que al borrar
     * la carpeta con las credenciales, esto sería como un log out implícito.
     */
    public void logOut() {
        boolean serviceIsRunning = OAuth2TokenRefreshService.isRunning();
        String token = serviceIsRunning ? OAuth2TokenRefreshService.getInstance().getCurrentCredential().getAccessToken() : null;
        if (token != null) {
            RestRequest revokeRequest = new GetRequest("https://accounts.google.com/o/oauth2/revoke?token=" + token);
            revokeRequest.exec();
            if (serviceIsRunning) {
                OAuth2TokenRefreshService.getInstance().stop();
            }
        }
        active = null;
        cleanUp();
    }

    public void cleanUp() {
        try {
            if (FileManager.dirOrFileExists(GoogleLogin.CREDENTIALS_DIR.getPath())) {
                FileManager.delete(GoogleLogin.CREDENTIALS_DIR);
            }
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Cerrar sesión", "Ha ocurrido un error inesperado. No se puede hacer cerrar sesión en este momento.", null);
            ex.printStackTrace();
        }
    }
}
