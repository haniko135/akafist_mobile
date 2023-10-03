package net.energogroup.akafist.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class to receive notification. Extends BroadcastReceiver
 * @author Nastya Izotina
 * @version 1.1.0
 */
public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("AUDIOS")
                .putExtra("actionName", intent.getAction()));
    }
}
