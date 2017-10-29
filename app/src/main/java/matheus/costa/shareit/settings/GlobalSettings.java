package matheus.costa.shareit.settings;

import matheus.costa.shareit.objects.User;

/**
 * Created by Matheus on 23/10/2017.
 */

public class GlobalSettings {

    private static final GlobalSettings ourInstance = new GlobalSettings();
    public static GlobalSettings getInstance() {
        return ourInstance;
    }

    private GlobalSettings() {
    }


    private User authenticatedUser;     //the user who is authenticated on the device


    public User getAuthenticatedUser(){
        return authenticatedUser;
    }

    public void saveAuthenticatedUser(User user){
        this.authenticatedUser = user;
    }
}
