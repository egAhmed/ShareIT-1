package matheus.costa.shareit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.settings.GlobalSettings;

/**
 * Created by Matheus on 02/12/2017.
 */

public class NotificationService extends Service {

    private final String LTAG = getClass().getSimpleName();
    private String userUid;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(LTAG,"Starting NotificationService -> onCreate()");

        userUid = GlobalSettings.getInstance().getAuthenticatedUser().getUserUid();
        Database.getInstance().monitoringNotification(this,userUid);

        Log.i(LTAG,"NotificationService Started!");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
