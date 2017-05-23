package rinon.ninqueon.nsuweather.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.data.TemperatureData;
import rinon.ninqueon.nsuweather.services.WeatherService;
import rinon.ninqueon.nsuweather.utils.ErrorCodes;
import rinon.ninqueon.nsuweather.view.settingsScreen.SettingsActivity;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

final class MainController
{
    private final static String ROOT_URL            = "http://weather.nsu.ru/xml.php";
    private final static String ADD_TEN             = "?std=ten";
    private final static String ADD_MONTH           = "?std=month";
    private final static String ADD_PERIOD          = "?std=various";
    private final static String ADD_START           = "&start=";
    private final static String ADD_STOP            = "&end=";
    private final static String ADD_AVERAGE         = "&average=true";

    private final Context context;
    private WeatherService bindedService;
    private final ServiceConnection serviceConnection;
    private boolean serviceIsBinded;

    private final MainView mainView;
    private final String[] drawerModel;
    private long start = 0;
    private long stop = 0;
    private boolean isAverage = false;
    private boolean textSubtitle = false;
    private int menuPosition;
    private final DateFormat dateFormat;

    private final static int NO_MENU_POSITION       = -1;
    private final static int MENU_3_DAYS            = 0;
    private final static int MENU_10_DAYS           = 1;
    private final static int MENU_MONTH             = 2;
    private final static int MENU_PERIOD            = 3;

    private boolean dataLoaded = false;

    MainController(final Context context,
                     final View rootView)
    {
        this.context = context;
        drawerModel = context.getResources().getStringArray(R.array.menu_items);
        mainView = new MainView(context, rootView, drawerModel, this);

        serviceConnection = new ServiceConnection()
        {

            @Override
            public void onServiceConnected(final ComponentName className, final IBinder service)
            {
                final WeatherService.LocalBinder binder = (WeatherService.LocalBinder) service;
                bindedService = binder.getService();
                serviceIsBinded = true;
            }

            @Override
            public void onServiceDisconnected(final ComponentName arg0)
            {
                bindedService = null;
                serviceIsBinded = false;
            }
        };
        dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    }

    final void onCreate()
    {
        if (start == 0 && stop == 0)
        {
            stop = System.currentTimeMillis();
            start = stop - TemperatureData.PERIOD_3_DAYS_MS;
        }

        menuPosition = NO_MENU_POSITION;
        selectItem(MENU_3_DAYS);
    }

    private void selectItem(final int menuPosition)
    {
        if (this.menuPosition == menuPosition)
        {
            return;
        }

        this.menuPosition = menuPosition;

        switch (menuPosition)
        {
            case MENU_3_DAYS:
            case MENU_10_DAYS:
            case MENU_MONTH:
                final String title = context.getString(R.string.app_name) + ": " + drawerModel[menuPosition];
                mainView.setTitle(title);
                break;
            case MENU_PERIOD:
                mainView.setTitle(context.getString(R.string.app_name));
                break;
        }

        textSubtitle = false;
        startUpdate();

        mainView.invalidateOptionsMenu();

        mainView.closeDrawers();
        mainView.setItemChecked(menuPosition, true);
    }

    final void onItemClick(final int position)
    {
        selectItem(position);
    }

    final void syncState()
    {
        mainView.syncState();
    }

    final void onConfigurationChanged(final Configuration newConfig)
    {
        mainView.onConfigurationChanged(newConfig);
    }

    private void bindService(final Intent intent)
    {
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    final void unBindService()
    {
        if (serviceIsBinded || bindedService != null)
        {
            context.unbindService(serviceConnection);
            bindedService = null;
            serviceIsBinded = false;
        }
    }

    void displayErrorToast(final int errorCode)
    {
        mainView.showErrorToast(ErrorCodes.getErrorMessageId(errorCode));
    }

    final void showErrorDialog(final int errorCode)
    {
        mainView.hideProgressDialog();
        final int errorTextId = ErrorCodes.getErrorMessageId(errorCode);

        if (errorCode == ErrorCodes.ERROR_NO_CONNECTION)
        {
            textSubtitle = true;
            mainView.setSubtitle(errorTextId);
        }
        else
        {
            mainView.showErrorToast(errorTextId);
        }
        unBindService();
    }

    final void addItemsFromService()
    {
        if (bindedService == null)
        {
            return;
        }

        final TemperatureData temperatureData = bindedService.getTemperatureData();
        if (temperatureData != null)
        {
            final float current = temperatureData.getCurrent();
            final String currentString = context.getString(R.string.temperature_now) + current + context.getString(R.string.temperature_c_degree);
            mainView.putNewData(temperatureData);

            if (!textSubtitle)
            {
                mainView.setSubtitle(currentString);
            }
        }

        dataLoaded = true;
        mainView.hideProgressDialog();
        unBindService();
    }

    private void startUpdate()
    {
        mainView.showProgressDialog();
        dataLoaded = false;
        String url;
        Intent intent;
        switch (menuPosition)
        {
            case MENU_3_DAYS:
                url = ROOT_URL;
                intent = WeatherService.getGetTemperatureIntent(context, url);
                break;
            case MENU_10_DAYS:
                url = ROOT_URL + ADD_TEN;
                intent = WeatherService.getGetTemperatureIntent(context, url);
                break;
            case MENU_MONTH:
                url = ROOT_URL + ADD_MONTH;
                intent = WeatherService.getGetTemperatureIntent(context, url);
                break;
            case MENU_PERIOD:
                url = ROOT_URL + ADD_PERIOD + ADD_START + (start / 1000) + ADD_STOP + (stop / 1000);
                if (isAverage)
                {
                    url += ADD_AVERAGE;
                    intent = WeatherService.getGetAverageTemperatureIntent(context, url);
                }
                else
                {
                    intent = WeatherService.getGetTemperatureIntent(context, url);
                }
                break;
            default:
                return;
        }

        if (intent == null)
        {
            return;
        }

        bindService(intent);
    }

    final void startRead()
    {
        dataLoaded = false;
        long period;
        Intent intent = null;
        switch (menuPosition)
        {
            case MENU_3_DAYS:
                period = TemperatureData.PERIOD_3_DAYS_MS;
                intent = WeatherService.getReadTemperatureIntent(context, period);
                break;
            case MENU_10_DAYS:
                period = TemperatureData.PERIOD_10_DAYS_MS;
                intent = WeatherService.getReadTemperatureIntent(context, period);
                break;
            case MENU_MONTH:
                period = TemperatureData.PERIOD_30_DAYS_MS;
                intent = WeatherService.getReadTemperatureIntent(context, period);
                break;
            case MENU_PERIOD:
                intent = WeatherService.getReadTemperaturePeriodIntent(context, start, stop, isAverage);
                break;

        }

        if (intent != null)
        {
            bindService(intent);
        }
    }

    final void checkAndLoadDataFromService()
    {
        if (bindedService != null)
        {
            if (bindedService.dataAvailable())
            {
                addItemsFromService();
            }
        }
        else if (!dataLoaded)
        {
            startUpdate();
        }
    }

    final void setMenuItemsVisibility(final Menu menu)
    {
        final MenuItem menuDateFrom = menu.findItem(R.id.menu_date_from);
        final MenuItem menuDateTo = menu.findItem(R.id.menu_date_to);
        final MenuItem menuAverage = menu.findItem(R.id.menu_average);

        if (menuPosition == MENU_PERIOD)
        {
            String fromString = context.getString(R.string.menu_date_from) +
                    dateFormat.format(new Date(start));
            String toString = context.getString(R.string.menu_date_to) +
                    dateFormat.format(new Date(stop));

            menuDateFrom.setTitle(fromString);
            menuDateTo.setTitle(toString);

            menuAverage.setChecked(isAverage);
            if (menuAverage.isChecked())
            {
                menuAverage.setIcon(R.drawable.ic_action_on);
            }
            else
            {
                menuAverage.setIcon(R.drawable.ic_action_off);
            }

            menuDateFrom.setVisible(true);
            menuDateTo.setVisible(true);
            menuAverage.setVisible(true);
        }
        else
        {
            menuDateFrom.setVisible(false);
            menuDateTo.setVisible(false);
            menuAverage.setVisible(false);
        }
    }

    final void onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_news_refresh:
                startUpdate();
                break;
            case R.id.menu_average:
                isAverage = !item.isChecked();
                mainView.invalidateOptionsMenu();
                break;
            case R.id.menu_date_from:
                mainView.showDatePicker(start, System.currentTimeMillis() - 3 * 365 * 24 * 60 * 60 * 1000L, stop - 3 * 24 * 60 * 60 * 1000L, true);
                break;
            case R.id.menu_date_to:
                mainView.showDatePicker(stop, start + 3 * 24 * 60 * 60 * 1000L, System.currentTimeMillis(), false);
                break;
            case R.id.menu_settings:
                SettingsActivity.openSettings(context);
                break;
        }
    }

    final void onDateStartSelect(final long start)
    {
        this.start = start;
    }

    final void onDateStopSelect(final long stop)
    {
        this.stop = stop;
    }
}
