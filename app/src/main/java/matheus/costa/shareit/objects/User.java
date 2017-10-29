package matheus.costa.shareit.objects;

import java.io.Serializable;

/**
 * Created by Matheus on 23/10/2017.
 */

public class User implements Serializable{

    private String userEmail;
    private String userName;
    private String userProfileImage; //firebase storage image url
    private String userUid; //firebase auth.uid

    public String getUserEmail() {
        return userEmail;
    }

    public User setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public User setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
        return this;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfileImage='" + userProfileImage + '\'' +
                ", userUid='" + userUid + '\'' +
                '}';
    }
}
