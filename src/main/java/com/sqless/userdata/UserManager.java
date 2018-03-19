package com.sqless.userdata;

import com.sqless.network.PostRequest;
import com.sqless.network.RestRequest;
import com.sqless.settings.PreferenceLoader;
import com.sqless.utils.Callback;
import com.sqless.utils.UIUtils;
import us.monoid.web.JSONResource;

public class UserManager {

    private static UserManager instance;
    private User active;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void addNew(User user) {
        this.active = user;
        storeToken();
    }

    public User getActive() {
        return active;
    }

    /**
     * Autentica el token almacenado en el cliente y ejecuta la acción dada a
     * modo de callback, pasándole como parámetro el usuario que pertenece a ese
     * token. El usuario que pertenece al token será asignado como activo en
     * esta clase. <br><br>
     * Nota: este método sólo se ejecutará si el usuario activo es null.
     *
     * @param action La acción que se ejecutará si la autenticación del token
     * guardado contra el servidor es exitosa.
     */
    public void authenticateStoredToken(Callback<User> action) {
        if (active == null) {
            PreferenceLoader prefs = PreferenceLoader.getInstance();
            String userToken = prefs.get(PreferenceLoader.PATH_PROFILE, PreferenceLoader.PrefKey.jwt_token);
            if (!userToken.equals("-1")) {
                RestRequest testRequest = new PostRequest("http://sqless.ddns.net:8080/ws/tokentest", "token=" + userToken) {
                    @Override
                    public void onSuccess(JSONResource json) throws Exception {
                        int user_id = Integer.parseInt(json.get("user_id").toString());
                        if (user_id != -1) {
                            active = new User(json.get("user_data.username").toString(), userToken);
                            System.out.println("User recovered from token: " + active.getUsername());
                            action.exec(active);
                        } else {
                            UIUtils.showErrorMessage("Log in", "Tu sesión ha caducado. Por favor, haz log in de nuevo.", null);
                            prefs.set(PreferenceLoader.PATH_PROFILE, PreferenceLoader.PrefKey.jwt_token, "-1");
                        }
                    }
                };
                testRequest.exec();
            }
        }
    }

    public void logOut() {
        active = null;
        PreferenceLoader.getInstance().set(PreferenceLoader.PATH_PROFILE, PreferenceLoader.PrefKey.jwt_token, -1 + "");
    }

    private void storeToken() {
        PreferenceLoader prefs = PreferenceLoader.getInstance();
        prefs.set(PreferenceLoader.PATH_PROFILE, PreferenceLoader.PrefKey.jwt_token, active.getToken());
    }

}
