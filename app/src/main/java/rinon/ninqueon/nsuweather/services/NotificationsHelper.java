package rinon.ninqueon.nsuweather.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.view.MainActivity;

/**
 * Created by Rinon Ninqueon on 06.03.2017.
 */

final class NotificationsHelper
{
    private final static int NOTIFICATION_ID                 = 744;

    private static boolean isNotificationsAllowed(final Context context)
    {
        return SharedPreferencesHelper.getSharedBooleanId(context, R.string.settings_notification_enable, R.bool.settings_notification_enable_default);
    }

    static void buildNotification(final Context context, final float current)
    {
        if (!isNotificationsAllowed(context))
        {
            return;
        }

        final long when = System.currentTimeMillis();
        final Intent intent = new Intent(context, MainActivity.class);

        addNotification(context, current, null, when, intent);
    }

    static void buildNotification(final Context context, final String subtitle)
    {
        if (!isNotificationsAllowed(context))
        {
            return;
        }

        final long when = System.currentTimeMillis();
        final Intent intent = new Intent(context, MainActivity.class);

        addNotification(context, 0, subtitle, when, intent);
    }

    private static void addNotification(final Context context,
                                        final float current,
                                        final String subtitle,
                                        final long when,
                                        final Intent intent)
    {
        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentIntent(notifyPendingIntent);
        notificationBuilder.setContentTitle(context.getString(R.string.temperature_near_nsu));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setWhen(when);

        if (subtitle == null)
        {
            final int temp = (int) current;

            int resId;
            if (temp >= 0)
            {
                resId = context.getResources().getIdentifier("temp_" + temp, "drawable", context.getPackageName());
            }
            else
            {
                resId = context.getResources().getIdentifier("temp__" + (-temp), "drawable", context.getPackageName());
            }

            final String currentString = context.getString(R.string.temperature_now) + current + context.getString(R.string.temperature_c_degree);
            notificationBuilder.setContentText(currentString);
            notificationBuilder.setSmallIcon(resId);
        }
        else
        {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_sun);
            notificationBuilder.setContentText(subtitle);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null)
        {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
