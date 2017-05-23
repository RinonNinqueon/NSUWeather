package rinon.ninqueon.nsuweather.data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class TemperatureData
{
    public final static long PERIOD_3_DAYS_MS              =  3 * 24 * 60 * 60 * 1000L;
    public final static long PERIOD_10_DAYS_MS             = 10 * 24 * 60 * 60 * 1000L;
    public final static long PERIOD_30_DAYS_MS             = 30 * 24 * 60 * 60 * 1000L;

    private final int DEFAULT_MIN = -1;
    private final int DEFAULT_MAX = 1;

    private int temperatureMax = DEFAULT_MIN;
    private int temperatureMin = DEFAULT_MAX;

    private final float current;
    private final ArrayList<TemperaturePoint> points;

    TemperatureData(final ArrayList<TemperaturePoint> points, final float current)
    {
        this.points = new ArrayList<>();
        this.points.addAll(points);

        this.current = current;

        calcMinMax();
    }

    public TemperatureData(final TemperaturePoint points[], final float current)
    {
        this.points = new ArrayList<>();
        Collections.addAll(this.points, points);

        this.current = current;

        calcMinMax();
    }

    private void calcMinMax()
    {
        int min = DEFAULT_MIN;
        int max = DEFAULT_MAX;

        for (int i = 0; i < points.size(); i++)
        {
            final TemperaturePoint point = points.get(i);
            final int temperature = (int)point.getTemperature();
            if (temperature > max)
            {
                max = temperature;
            }
            if (temperature < min)
            {
                min = temperature;
            }
        }

        temperatureMin = min - 1;
        temperatureMax = max + 1;
    }

    public int getTemperatureMax()
    {
        return temperatureMax;
    }

    public int getTemperatureMin()
    {
        return temperatureMin;
    }

    public TemperaturePoint[] getPoints()
    {
        TemperaturePoint result[] = new TemperaturePoint[points.size()];
        result = points.toArray(result);
        return result;
    }

    public int size()
    {
        return points.size();
    }

    public TemperaturePoint get(final int id)
    {
        return points.get(id);
    }

    public float getCurrent()
    {
        return current;
    }

}
