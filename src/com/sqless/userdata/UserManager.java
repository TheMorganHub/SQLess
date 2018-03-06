package com.sqless.userdata;

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
    }

    public User getActiveUser() {
        return active;
    }
    
    public void logOut() {
        active = null;
    }

}
