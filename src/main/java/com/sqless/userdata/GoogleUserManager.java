package com.sqless.userdata;

import com.sqless.file.FileManager;
import com.sqless.main.GoogleLogin;
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

    public void authenticateStoredCredentials(Callback<GoogleUser> callback, UIGoogleWaitDialog waitDialog){
        if (active == null) {
            if (credentialsExistLocally()) {
                GoogleLogin login = new GoogleLogin(person -> {
                    addNew(person);
                    callback.exec(active);
                }, waitDialog);
                login.start();
            }
        }
    }
    
    public boolean credentialsExistLocally() {
        return FileManager.dirOrFileExists(GoogleLogin.CREDENTIALS_DIR.getPath()) && !FileManager.isDirEmpty(GoogleLogin.CREDENTIALS_DIR.getPath());
    }

    /**
     * Simula un log out con Google. Esto se logra eliminando la carpeta que
     * contiene las Credenciales de Google de la PC y seteando el usuario activo
     * a null. Al eliminar las credenciales guardadas, la próxima vez que el
     * usuario intente iniciar sesión con Google se abrirá el navegador.
     */
    public void logOut() {
        active = null;
        cleanUp();
    }

    public void cleanUp() {
        try {
            if (FileManager.dirOrFileExists(GoogleLogin.CREDENTIALS_DIR.getPath())) {
                FileManager.delete(GoogleLogin.CREDENTIALS_DIR);
            }
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Log out", "Ha ocurrido un error inesperado. No se puede hacer cerrar sesión en este momento", null);
            ex.printStackTrace();
        }
    }
}
