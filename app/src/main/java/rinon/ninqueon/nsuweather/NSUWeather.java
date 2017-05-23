package rinon.ninqueon.nsuweather;

import android.app.Application;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import rinon.ninqueon.nsuweather.utils.LogcatHandler;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class NSUWeather extends Application
{
    public NSUWeather()
    {
        final Logger logger = Logger.getLogger("rinon.ninqueon.nsuweather");

        if (BuildConfig.DEBUG)
        {
            logger.setLevel(Level.CONFIG);
            logger.setUseParentHandlers(false);
            final Handler handler = new LogcatHandler();
            logger.addHandler(handler);
        }
        else
        {
            logger.setLevel(Level.OFF);
        }
    }
}
