package rinon.ninqueon.nsuweather.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.data.TemperatureData;
import rinon.ninqueon.nsuweather.data.TemperaturePoint;
import rinon.ninqueon.nsuweather.data.XMLParser;
import rinon.ninqueon.nsuweather.database.DatabaseHelper;
import rinon.ninqueon.nsuweather.utils.DownloadHelper;
import rinon.ninqueon.nsuweather.utils.ErrorCodes;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class WeatherService extends Service
{
    private final static String LOGGER_TAG                  = WeatherService.class.getName();
    private final static Logger logger                      = Logger.getLogger(LOGGER_TAG);

    public final static String ACTION_GET_TEMPERATURE       = "rinon.ninqueon.nsuweather.ACTION_GET_TEMPERATURE";
    public final static String ACTION_GET_BACKGROUND        = "rinon.ninqueon.nsuweather.ACTION_GET_BACKGROUND";
    public final static String ACTION_READ_TEMPERATURE      = "rinon.ninqueon.nsuweather.ACTION_READ_TEMPERATURE";
    public static final String ACTION_ERROR                 = "rinon.ninqueon.rssreader.services.action.ERROR";
    public static final String ACTION_CRITICAL_ERROR        = "rinon.ninqueon.rssreader.services.action.CRITICAL_ERROR";
    public static final String ACTION_ITEMS_DELETE_ALL      = "rinon.ninqueon.rssreader.services.action.ACTION_DELETE_ALL";

    private final static String EXTRA_URL                   = "rinon.ninqueon.nsuweather.EXTRA_URL";
    private final static String EXTRA_AVERAGE               = "rinon.ninqueon.nsuweather.EXTRA_AVERAGE";
    private final static String EXTRA_START                 = "rinon.ninqueon.nsuweather.EXTRA_START";
    private final static String EXTRA_STOP                  = "rinon.ninqueon.nsuweather.EXTRA_STOP";
    public static final String EXTRA_PERIOD                 = "rinon.ninqueon.nsuweather.EXTRA_PERIOD";
    public final static String EXTRA_ERROR_ID               = "rinon.ninqueon.nsuweather.EXTRA_ERROR_ID";

    private final static int BIND_TIMEOUT_MS                = 3000;
    private static final String APP_PREFERENCES             = LOGGER_TAG;

    private boolean isBinded;
    private boolean isStarted;
    private final Object lock = new Object();
    private final IBinder localBinder;
    private volatile Looper serviceLooper;
    private volatile ServiceHandler serviceHandler;
    private final String serviceName;

    private TemperatureData temperatureData;

    private final class ServiceHandler extends Handler
    {
        ServiceHandler(final Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg)
        {
            onHandleIntent((Intent)msg.obj);
            //stopSelf(msg.arg1);
        }
    }

    public WeatherService()
    {
        serviceName = LOGGER_TAG;
        localBinder = new LocalBinder();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        logger.info("onCreate");

        HandlerThread thread = new HandlerThread("WeatherService[" + serviceName + "]");
        thread.start();

        isBinded = false;

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        temperatureData = null;
    }

    @Override
    public void onDestroy()
    {
        logger.info("onDestroy");
        serviceLooper.quit();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        sendIntentMessage(intent);
        isStarted = true;
        return START_NOT_STICKY;
    }

    public static void startDeleteItems(final Context context)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_ITEMS_DELETE_ALL);
        context.startService(intent);
    }

    public static Intent getGetBackgroundTemperatureIntent(final Context context, final String url)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_GET_BACKGROUND);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    public static Intent getGetTemperatureIntent(final Context context, final String url)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_GET_TEMPERATURE);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    public static Intent getGetAverageTemperatureIntent(final Context context, final String url)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_GET_TEMPERATURE);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_AVERAGE, true);
        return intent;
    }

    public static Intent getReadTemperatureIntent(final Context context, final long period)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_READ_TEMPERATURE);
        intent.putExtra(EXTRA_PERIOD, period);
        return intent;
    }

    public static Intent getReadTemperaturePeriodIntent(final Context context, final long start, final long stop, final boolean average)
    {
        final Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_READ_TEMPERATURE);
        intent.putExtra(EXTRA_START, start);
        intent.putExtra(EXTRA_STOP, stop);
        intent.putExtra(EXTRA_AVERAGE, average);
        return intent;
    }

    private void sendIntentMessage(final Intent intent)
    {
        logger.info("sendIntentMessage");
        Message msg = serviceHandler.obtainMessage();
        msg.obj = intent;
        serviceHandler.sendMessage(msg);
    }

    private void onHandleIntent(final Intent intent)
    {
        logger.info("isBinded=" + isBinded);

        if (!isBinded && !isStarted && lock != null)
        {
            synchronized(lock)
            {
                try
                {
                    lock.wait(BIND_TIMEOUT_MS);
                }
                catch (final InterruptedException e)
                {
                    logger.severe(e.getMessage());
                }
            }
        }
        if (intent != null)
        {
            final String action = intent.getAction();
            logger.config(action);
            if (ACTION_GET_TEMPERATURE.equals(action))
            {
                final String url = intent.getStringExtra(EXTRA_URL);
                if (url == null)
                {
                    BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR_IO_ERROR);
                    return;
                }
                final boolean average = intent.getBooleanExtra(EXTRA_AVERAGE, false);
                actionGetTemperature(url, !average);
            }
            if (ACTION_GET_BACKGROUND.equals(action))
            {
                final String url = intent.getStringExtra(EXTRA_URL);
                if (url == null)
                {
                    BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR_IO_ERROR);
                    return;
                }
                actionGetCurrentTemperature(url);
            }
            if (ACTION_READ_TEMPERATURE.equals(action))
            {
                final long now = (new Date()).getTime();
                final long period = intent.getLongExtra(EXTRA_PERIOD, TemperatureData.PERIOD_3_DAYS_MS);

                final SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                long stop = sharedPreferences.getLong(EXTRA_STOP, now);
                long start = stop - period;

                start = intent.getLongExtra(EXTRA_START, start);
                stop = intent.getLongExtra(EXTRA_STOP, stop);
                final boolean average = intent.getBooleanExtra(EXTRA_AVERAGE, false);

                actionGetTemperature(start, stop, average);
            }
            if (ACTION_ITEMS_DELETE_ALL.equals(action))
            {
                actionDeleteItems();
            }
        }
        if (isStarted)
        {
            stopService(intent);
        }
    }

    private void actionGetTemperature(final String url, final boolean writeToDB)
    {
        if (!checkConnection())
        {
            NotificationsHelper.buildNotification(this, getString(R.string.error_connection));
            return;
        }

        TemperatureData data;
        try
        {
            data = parseXMLStream(url, false);
        }
        catch (final ParseException | IOException | XmlPullParserException e)
        {
            BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR);
            return;
        }

        try
        {
            if (writeToDB)
            {
                writeToDBTemperatureData(this, data);
            }
        }
        catch (final SQLException e)
        {
            BroadcastMessagesWorker.sendErrorMessage(this, ErrorCodes.ERROR_DATABASE);
        }

        temperatureData = data;
        NotificationsHelper.buildNotification(this, data.getCurrent());
        BroadcastMessagesWorker.sendBroadcastMessage(this, null, ACTION_GET_TEMPERATURE);
    }

    private void actionGetCurrentTemperature(final String url)
    {
        if (!checkConnection())
        {
            NotificationsHelper.buildNotification(this, getString(R.string.error_connection));
            return;
        }

        TemperatureData data;
        try
        {
            data = parseXMLStream(url, false);
        }
        catch (final ParseException | IOException | XmlPullParserException e)
        {
            BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR);
            NotificationsHelper.buildNotification(this, getString(R.string.error));
            return;
        }

        try
        {
            writeToDBTemperatureData(this, data);
        }
        catch (final SQLException e)
        {
            logger.severe(e.getMessage());
        }

        NotificationsHelper.buildNotification(this, data.getCurrent());
    }

    private void actionGetTemperature(final long start, final long stop, final boolean average)
    {
        TemperatureData data = null;

        try
        {
            TemperaturePoint points[];
            if (average)
            {
                points = readDBAverageTemperature(this, start, stop);
            }
            else
            {
                points = readDBTemperature(this, start, stop);
            }

            final float last = points[points.length - 1].getTemperature();
            data = new TemperatureData(points, last);
        }
        catch (final SQLException e)
        {
            BroadcastMessagesWorker.sendErrorMessage(this, ErrorCodes.ERROR_DATABASE);
        }

        if (data == null)
        {
            BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR);
            return;
        }

        temperatureData = data;
        BroadcastMessagesWorker.sendBroadcastMessage(this, null, ACTION_READ_TEMPERATURE);
    }

    private void actionDeleteItems()
    {
        deleteItemsAll(this);
    }

    private static TemperatureData parseXMLStream(final String url, final boolean onlyCurrent) throws ParseException, XmlPullParserException, IOException
    {
        if (url == null)
        {
            return null;
        }

        final DownloadHelper downloadHelper = new DownloadHelper();

        InputStream inputStream;
        TemperatureData temperatureData = null;

        try
        {
            inputStream = downloadHelper.openInputStream(url);
            temperatureData = XMLParser.parseXML(inputStream, downloadHelper.getCharset(), onlyCurrent);
        }
        finally
        {
            downloadHelper.closeInputStream();
        }

        return temperatureData;
    }

    private static void writeToDBTemperatureData(final Context context, final TemperatureData temperatureData)
    {
        if (temperatureData == null)
        {
            return;
        }

        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;

        try
        {
            db = mDbHelper.getWritableDatabase();
            mDbHelper.writeTemperatureData(db, temperatureData);

            //Запишем последнюю точку, проверив
            final SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            final long stop = sharedPreferences.getLong(EXTRA_STOP, 0);
            final long lastPointDate = temperatureData.get(temperatureData.size() - 1).getDate();

            if (stop == 0 || stop < lastPointDate)
            {
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(EXTRA_STOP, lastPointDate);
                editor.apply();
            }
        }
        finally
        {
            if (db != null)
            {
                db.close();
            }
        }
    }

    private static TemperaturePoint[] readDBTemperature(final Context context, final long start, final long stop)
    {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        TemperaturePoint points[] = null;

        try
        {
            db = mDbHelper.getWritableDatabase();
            points = mDbHelper.readTemperature(db, start, stop);
        }
        finally
        {
            if (db != null)
            {
                db.close();
            }
        }

        return points;
    }

    private static TemperaturePoint[] readDBAverageTemperature(final Context context, final long start, final long stop)
    {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        TemperaturePoint points[] = null;

        try
        {
            db = mDbHelper.getWritableDatabase();
            points = mDbHelper.readAverageTemperature(db, start, stop);
        }
        finally
        {
            if (db != null)
            {
                db.close();
            }
        }

        return points;
    }

    private static void deleteItemsAll(final Context context) throws SQLException
    {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;

        try
        {
            db = mDbHelper.getWritableDatabase();
            mDbHelper.deleteItemsAll(db);
        }
        finally
        {
            if (db != null)
            {
                db.close();
            }
        }
    }

    public final TemperatureData getTemperatureData()
    {
        return temperatureData;
    }

    public final boolean dataAvailable()
    {
        return (temperatureData != null);
    }

    @Override
    public boolean onUnbind(final Intent intent)
    {
        logger.info("onUnbind " + intent);
        isBinded = false;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(final Intent intent)
    {
        logger.info("onBind " + intent);
        temperatureData = null;
        sendIntentMessage(intent);
        return localBinder;
    }

    private boolean checkConnection()
    {
        if (!DownloadHelper.isOnline(this))
        {
            BroadcastMessagesWorker.sendCriticalErrorMessage(this, ErrorCodes.ERROR_NO_CONNECTION);
            return false;
        }

        return true;
    }

    public class LocalBinder extends Binder
    {
        public WeatherService getService()
        {
            logger.info("getService");
            isBinded = true;
            isStarted = false;
            synchronized(lock)
            {
                logger.info("lock.notify");
                lock.notify();
            }
            return WeatherService.this;
        }
    }
}
