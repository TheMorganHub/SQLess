package com.sqless.userdata;

import com.sqless.file.FileManager;
import com.sqless.main.GoogleLogin;
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
    
    public void authenticateStoredCredentials(Callback<GoogleUser> callback) {
        if (active == null && UserManager.getInstance().getActive() == null) {
            if (FileManager.dirOrFileExists(GoogleLogin.CREDENTIALS_DIR.getPath())) {
                GoogleLogin login = new GoogleLogin(person -> {
                    addNew(person);
                    callback.exec(active);
                });
                login.start();                
            }
        }
    }
    
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
