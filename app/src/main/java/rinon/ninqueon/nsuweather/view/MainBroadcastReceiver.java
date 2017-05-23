package rinon.ninqueon.nsuweather.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import rinon.ninqueon.nsuweather.services.WeatherService;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class MainBroadcastReceiver extends BroadcastReceiver
{
    private final MainController mainController;
    private final Context context;

    MainBroadcastReceiver(final Context context,
                          final MainController mainController)
    {
        this.mainController = mainController;
        this.context = context;
    }

    final void registerBroadcastReceiver()
    {
        final IntentFilter filter = new IntentFilter(WeatherService.ACTION_GET_TEMPERATURE);
        filter.addAction(WeatherService.ACTION_READ_TEMPERATURE);
        filter.addAction(WeatherService.ACTION_ERROR);
        filter.addAction(WeatherService.ACTION_CRITICAL_ERROR);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    final void unregisterBroadcastReceiver()
    {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        if (intent.getAction().equals(WeatherService.ACTION_GET_TEMPERATURE))
        {
            mainController.addItemsFromService();
        }
        if (intent.getAction().equals(WeatherService.ACTION_READ_TEMPERATURE))
        {
            mainController.addItemsFromService();
        }
        if (intent.getAction().equals(WeatherService.ACTION_ERROR))
        {
            final Bundle args = intent.getExtras();
            final int errorCode = args.getInt(WeatherService.EXTRA_ERROR_ID);
            mainController.displayErrorToast(errorCode);
        }
        if (intent.getAction().equals(WeatherService.ACTION_CRITICAL_ERROR))
        {
            final Bundle args = intent.getExtras();
            final int errorCode = args.getInt(WeatherService.EXTRA_ERROR_ID);
            mainController.showErrorDialog(errorCode);

            mainController.startRead();
        }
    }
}
