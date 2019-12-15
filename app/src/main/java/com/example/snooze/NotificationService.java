package com.example.snooze;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends NotificationListenerService {

       /*Estos son los nombres de paquete de las aplicaciones. para lo cual queremos escuchar las notificaciones*/

    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
        public static final String TWITTER_PACK_NAME = "com.twitter.android";
        public static final String GMAIL_PACK_NAME = "com.google.android.gm";
    }

    /* Estos son los códigos de retorno que utilizamos en el método que intercepta las notificaciones, para decidir si debemos hacer algo o no. */


    public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int FACEBOOK_MESSENGER_CODE = 2;
        public static final int WHATSAPP_CODE = 3;
        public static final int INSTAGRAM_CODE = 4;
        public static final int TWITTER_CODE = 5;
        public static final int GMAIL_CODE = 6;
        public static final int OTHER_NOTIFICATIONS_CODE = 7; // Ignoramos las notificaciones con codigo == 7
    }


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn){


        int notificationCode = matchNotificationCode(sbn);




        if(((notificationCode == InterceptedNotificationCode.WHATSAPP_CODE) || (notificationCode == InterceptedNotificationCode.GMAIL_CODE)) &&
                ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0)){
            Intent intent = new  Intent("com.example.snooze");

            // Eliminar la notificacion del cajon de notificaciones

            String key = sbn.getKey();
            cancelNotification(key);

            // Obtener la informacion a guardar en la aplicacion

            String pack = sbn.getPackageName();
            String Text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            String Title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();

            intent.putExtra("Codigo de la notificacion", notificationCode);
            intent.putExtra("Titulo de la notificacion",Title);
            intent.putExtra("Texto de la notificacion",Text);
            intent.putExtra("Aplicacion envia",pack);

            sendBroadcast(intent);

        }


        if((notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) && (notificationCode != InterceptedNotificationCode.WHATSAPP_CODE) &&
                (notificationCode != InterceptedNotificationCode.GMAIL_CODE) && (sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT) != null)){

            Intent intent = new  Intent("com.example.snooze");
            // Eliminar la notificacion del cajon de notificaciones

            String key = sbn.getKey();
            cancelNotification(key);

            // Obtener la informacion a guardar en la aplicacion

            String pack = sbn.getPackageName();
            String Text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            String Title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();

            intent.putExtra("Codigo de la notificacion", notificationCode);
            intent.putExtra("Titulo de la notificacion",Title);
            intent.putExtra("Texto de la notificacion",Text);
            intent.putExtra("Aplicacion envia",pack);

            sendBroadcast(intent);


            Log.d("messengerService",key);



        }



    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
       Log.d("Msg","Notificacion removida");
    }


    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_MESSENGER_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.TWITTER_PACK_NAME)){
            return(InterceptedNotificationCode.TWITTER_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.GMAIL_PACK_NAME)){
            return(InterceptedNotificationCode.GMAIL_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }


    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Toast.makeText(this, "Connected to the service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Toast.makeText(this, "Disconnected to the service", Toast.LENGTH_SHORT).show();
    }
}
