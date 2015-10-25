package team3.promise.ufo_ball;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by plaslab on 2015/10/24.
 */
public class CancelNotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final int notifyID = intent.getIntExtra("cancel_notify_id", 0);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notificationManager.cancel(notifyID);

        Intent i = new Intent(context, UFOButton.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("canceled",1);
        context.startService(i);


    }
}
