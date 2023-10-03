package net.energogroup.akafist.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import net.energogroup.akafist.R;
import net.energogroup.akafist.models.LinksModel;

/**
 * Notification class for music
 * @author Nastya Izotina
 * @version 1.1.0
 */
public class NotificationForPlay {
    public static final String CHANNEL_ID="playing_audios";

    public static final String ACTION_PLAY="actionplay";

    public static Notification notification;

    /**
     * This method creates a music notification
     * @param context Current fragment context
     * @param linksModel Current song's data
     * @param playButton Current state of play button
     */
    public static void createNotification(Context context, LinksModel linksModel, int playButton) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

        Bitmap image = BitmapFactory.decodeResource(context.getResources(), linksModel.getImage());

        Intent playIntent = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    playIntent, PendingIntent.FLAG_IMMUTABLE);
        }

        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(linksModel.getName())
                .setContentText(linksModel.getUrl())
                .setLargeIcon(image)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(playButton, "Включить", pendingIntentPlay)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        try{
            notificationManagerCompat.notify(1, notification);
        } catch (SecurityException e){
            Log.e("NOTIFICATION ERROR", e.getLocalizedMessage());
        }
    }
}
