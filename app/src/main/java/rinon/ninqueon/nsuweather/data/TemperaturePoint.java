package rinon.ninqueon.nsuweather.data;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class TemperaturePoint
{
    private final float temperature;
    private final long date;

    public TemperaturePoint(final float temperature, final long date)
    {
        this.temperature = temperature;
        this.date = date;
    }

    public final float getTemperature()
    {
        return temperature;
    }

    public final long getDate()
    {
        return date;
    }
}
