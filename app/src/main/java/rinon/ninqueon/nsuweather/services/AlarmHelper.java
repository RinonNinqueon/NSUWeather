package rinon.ninqueon.nsuweather.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 06.03.2017.
 */

public final class AlarmHelper
{
    private final static int MILLISECONDS_IN_SECOND     = 1000;
    private final static int SECONDS_IN_MINUTE          = 60;

    private final static String ROOT_URL                = "http://weather.nsu.ru/xml.php";

    public static void setAlarm(final Context context)
    {
        final int settings_update_periodString = SharedPreferencesHelper.getSharedIntegerId(context, R.string.settings_update_period, R.integer.settings_update_period_default);
        final long delayMilliseconds = minutesToMilliseconds(settings_update_periodString);

        final PendingIntent pendingIntent = getPendingIntent(context);

        final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMilliseconds, delayMilliseconds, pendingIntent);
    }

    private static PendingIntent getPendingIntent(final Context context)
    {
        final Intent serviceIntent = WeatherService.getGetBackgroundTemperatureIntent(context, ROOT_URL);
        return PendingIntent.getService(
                context,
                0,
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    static void disableAlarm(final Context context)
    {
        final PendingIntent pendingIntent = getPendingIntent(context);

        final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static long minutesToMilliseconds(long minutes)
    {
        return minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    }
}
