package matheus.costa.shareit.objects;

import java.util.Date;

/**
 * Created by Matheus on 02/12/2017.
 */

public class AppNotification {

    private String notificationUserOwner; //One notificion entry by user in database
    private String notificationType;
    private String notificationContent;
    private String notificationTimeStamp;
    private boolean notificationChecked; //true = no new notification, false = new notification

    public AppNotification() {
    }

    public String getNotificationUserOwner() {
        return notificationUserOwner;
    }

    public AppNotification setNotificationUserOwner(String notificationUserOwner) {
        this.notificationUserOwner = notificationUserOwner;
        return this;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public AppNotification setNotificationType(String notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public AppNotification setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
        return this;
    }

    public String getNotificationTimeStamp() {
        return notificationTimeStamp;
    }

    public AppNotification setNotificationTimeStamp(String notificationTimeStamp) {
        this.notificationTimeStamp = notificationTimeStamp;
        return this;
    }

    public boolean isNotificationChecked() {
        return notificationChecked;
    }

    public AppNotification setNotificationChecked(boolean notificationChecked) {
        this.notificationChecked = notificationChecked;
        return this;
    }

    /**
     *
     * @return the name of user.
     */
    public String getContentForFriendRequest(){
        String[] splitted = notificationContent.split(",");
        return splitted[0];
    }

}
