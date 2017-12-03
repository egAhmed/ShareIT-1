package matheus.costa.shareit.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import matheus.costa.shareit.R;

/**
 * Created by Matheus on 02/12/2017.
 */

public class NotificationHelper {

    private static final int FRIEND_NOTIF_ID = 100;


    public static void showNewFriendNotification(Context context, String username){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(FRIEND_NOTIF_ID,
                new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.new_friend_notification))
                .setStyle(new Notification.BigTextStyle().bigText(context.getString(R.string.new_friend_notification2,username)))
                /*.setContentText(context.getString(R.string.new_friend_notification2,username))*/
                .setVibrate(new long[]{150,300,150,300}).build()
        );
    }
}
