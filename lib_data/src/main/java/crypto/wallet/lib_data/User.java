package crypto.wallet.lib_data;

import java.util.UUID;

/**
 * Created by Fynov on 27/02/17.
 */

public class User {

    private String ID;
    private String username;
    private String mail;

    public User(String name, String mailt) {
        this.ID = UUID.randomUUID().toString().replaceAll("-", "");
        this.mail = mailt;
        this.username = name;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public String getID() {
        return ID;
    }

    public void setMail(String mailt) {
        this.mail = mailt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" + mail + "}: " + username + "|";
    }
}
