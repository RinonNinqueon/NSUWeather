package rinon.ninqueon.nsuweather.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Rinon Ninqueon on 06.03.2017.
 */

public final class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            AlarmHelper.setAlarm(context);
        }
    }

    public static void enableReceiver(final Context context)
    {
        final ComponentName receiver = new ComponentName(context, BootReceiver.class);
        final PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmHelper.setAlarm(context);
    }

    public static void disableReceiver(final Context context)
    {
        final ComponentName receiver = new ComponentName(context, BootReceiver.class);
        final PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        AlarmHelper.disableAlarm(context);
    }
}
