 package com.radiowebufpa.rdioufpa;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "SERVICE_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Rádio UFPA",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            serviceChannel.setDescription("no sound");
            serviceChannel.setSound(null,null); // criando a notificação sem som
            serviceChannel.enableLights(false);
            serviceChannel.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
