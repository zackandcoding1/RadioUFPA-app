package com.radiowebufpa.rdioufpa;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

import static com.radiowebufpa.rdioufpa.App.CHANNEL_ID;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    public static MediaPlayer mediaPlayer;
    String radioURL;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String radio = sharedPreferences.getString("station", "100");

        if (radio != null) {
            radioURL = "http://www2.radio.ufpa.br/aovivo";
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(radioURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Criando a notificação do serviço ao iniciar a rádio
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        // Conteúdo a ser exibido na notificação do aplicativo
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Em reprodução")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_play)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon))
                .setContentIntent(pendingIntent)
                .build();

        // Permite que o serviço continue executando sem que o Android encerre a task
        startForeground(1, notification);

        // Recebendo a url da rádio e iniciando o player
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(radioURL);
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        mediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    // Para o player e encerra o serviço
    @Override
    public void onDestroy() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}