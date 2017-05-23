package rinon.ninqueon.nsuweather.utils;

import android.util.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Rinon Ninqueon on 01.03.2017.
 */

public final class LogcatHandler extends Handler
{
    @Override
    public void publish(final LogRecord record)
    {
        if (record == null)
        {
            return;
        }

        final String message = (null == record.getMessage() ? "" : record.getMessage());

        if (record.getLevel() == Level.SEVERE)
        {
            Log.e(record.getLoggerName(), message);
        }
        else if (record.getLevel() == Level.WARNING)
        {
            Log.w(record.getLoggerName(), message);
        }
        else if (record.getLevel() == Level.CONFIG)
        {
            Log.d(record.getLoggerName(), message);
        }
        else
        {
            Log.i(record.getLoggerName(), message);
        }
    }


    @Override
    public void flush()
    {

    }

    @Override
    public void close() throws SecurityException
    {

    }
}
