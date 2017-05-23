package rinon.ninqueon.nsuweather.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Rinon Ninqueon on 11.04.2017.
 */

final class BroadcastMessagesWorker
{
    static void sendBroadcastMessage(final Context context, final Bundle args, final String action)
    {
        if (action == null)
        {
            return;
        }

        final Intent transmitIntent = new Intent();
        transmitIntent.setAction(action);
        if (args != null)
        {
            transmitIntent.putExtras(args);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(transmitIntent);
    }

    static void sendErrorMessage(final Context context, final int errorId)
    {
        final Bundle args = new Bundle();
        args.putInt(WeatherService.EXTRA_ERROR_ID, errorId);
        sendBroadcastMessage(context, args, WeatherService.ACTION_ERROR);
    }

    static void sendCriticalErrorMessage(final Context context, final int errorId)
    {
        final Bundle args = new Bundle();
        args.putInt(WeatherService.EXTRA_ERROR_ID, errorId);

        sendBroadcastMessage(context, args, WeatherService.ACTION_CRITICAL_ERROR);
    }
}
