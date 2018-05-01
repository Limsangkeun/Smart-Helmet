package com.example.sangkeunlim.smartsafetyhelmetv2.firebase;

/**
 * Created by SangKeun LIM on 2018-04-30.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.example.sangkeunlim.smartsafetyhelmetv2.FragmentM.MFragmentActivity;
import com.example.sangkeunlim.smartsafetyhelmetv2.MyAlert;
import com.example.sangkeunlim.smartsafetyhelmetv2.R;
import com.google.firebase.messaging.RemoteMessage;
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    static Vibrator vibrator = null;
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        sendNotification(message, title);
    }

    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Resources resources = getResources();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alert)
               // .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.logo))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        intent = new Intent(this, MyAlert.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userID/DangerKind",messageBody);
        startActivity(intent);
    }


}
