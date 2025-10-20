package com.example.trabajofinal.ui.theme.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.trabajofinal.R;

public class DataUploadService extends Service {

    private static final String TAG = "DataUploadService";
    private static final String CHANNEL_ID = "upload_channel";

    private static final int NOTIFICATION_ID = 1;
    private static final int MAX_PROGRESS = 100;
    private static final int PROGRESS_INCREMENT = 20;
    private static final int DELAY_MS = 1000;
    private static final int FINAL_DELAY_MS = 2000;

    private NotificationManager manager;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        crearCanalNotificacion();
        Log.d(TAG, "Servicio creado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado");

        if (isRunning) {
            Log.w(TAG, "El servicio ya está en ejecución");
            return START_NOT_STICKY;
        }
        isRunning = true;

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.uploading_data))
                .setContentText(getString(R.string.preparing_upload))
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        new Thread(() -> {
            try {
                procesarEnvioDatos();
            } catch (InterruptedException e) {
                manejarErrorInterrupcion();
            } finally {
                finalizarServicio();
            }
        }).start();

        return START_NOT_STICKY;
    }


    private void procesarEnvioDatos() throws InterruptedException {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        for (int progress = 0; progress <= MAX_PROGRESS; progress += PROGRESS_INCREMENT) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Hilo interrumpido durante el envío");
            }

            builder.setContentTitle(getString(R.string.uploading_data))
                    .setContentText(getString(R.string.upload_progress, progress))
                    .setProgress(MAX_PROGRESS, progress, false);

            manager.notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Progreso de envío: " + progress + "%");
            Thread.sleep(DELAY_MS);
        }

        boolean envioExitoso = realizarEnvioReal();
        mostrarResultadoEnvio(builder, envioExitoso);
        Thread.sleep(FINAL_DELAY_MS);
    }


    private boolean realizarEnvioReal() {
        try {
            Log.i(TAG, "Realizando envío de datos...");
            return Math.random() > 0.1;
        } catch (Exception e) {
            Log.e(TAG, "Error en envío real: " + e.getMessage());
            return false;
        }
    }


    private void mostrarResultadoEnvio(NotificationCompat.Builder builder, boolean exito) {
        if (exito) {
            builder.setContentTitle(getString(R.string.upload_success))
                    .setContentText(getString(R.string.upload_success_detail))
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done);
            Log.i(TAG, "Envío completado exitosamente");
        } else {
            builder.setContentTitle(getString(R.string.upload_error))
                    .setContentText(getString(R.string.upload_error_detail))
                    .setSmallIcon(android.R.drawable.stat_notify_error);
            Log.w(TAG, "Error en el envío de datos");
        }

        builder.setProgress(0, 0, false).setOngoing(false);
        manager.notify(NOTIFICATION_ID, builder.build());
    }


    private void manejarErrorInterrupcion() {
        Log.e(TAG, "Servicio interrumpido");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.upload_error))
                .setContentText(getString(R.string.unexpected_error))
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }


    private void finalizarServicio() {
        Log.d(TAG, "Finalizando servicio");
        stopForeground(false);
        isRunning = false;
    }


    private void crearCanalNotificacion() {
        NotificationChannel canal = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.upload_service_channel),
                NotificationManager.IMPORTANCE_LOW
        );
        canal.setDescription(getString(R.string.upload_service_description));
        manager.createNotificationChannel(canal);
        Log.d(TAG, "Canal de notificación creado");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Servicio destruido");
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
