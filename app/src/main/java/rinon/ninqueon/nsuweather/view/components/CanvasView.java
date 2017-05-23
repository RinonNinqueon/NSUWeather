package rinon.ninqueon.nsuweather.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.data.TemperatureData;
import rinon.ninqueon.nsuweather.data.TemperaturePoint;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class CanvasView extends View
{
    private final static long HOURS_FULL_DRAW      = 3 * 24 * 60 * 60 * 1000L;
    private final static long HOURS_MEDIUM_DRAW    = 10 * 24 * 60 * 60 * 1000L;
    private final static long HOURS_MINIMUM_DRAW   = 29 * 24 * 60 * 60 * 1000L;
    private final static long DEGREES_MINIMUM_DRAW = 30;

    private final Paint paint;
    private TemperaturePoint points[];
    private long startDate, stopDate;
    private int minTemperature, maxTemperature;

    //---------------------------
    private final int graphDayLineColor;
    private final int graph6HourLineColor;
    private final int graphHourLineColor;
    private final int graph5DegreeLineColor;
    private final int graphDegreeLineColor;
    private final int graph0DegreeLineColor;
    private final int graphLineColor;

    private final float graphDayLineWidth;
    private final float graph6HourLineWidth;
    private final float graphHourLineWidth;
    private final float graph5DegreeLineWidth;
    private final float graphDegreeLineWidth;
    private final float graph0DegreeLineWidth;
    private final float graphLineWidth;

    private final float graphMarginLeft;
    private final float graphMarginTop;
    private final float graphMarginRight;
    private final float graphMarginBottom;

    private final float graphDateTextMarginBottom;
    private final float graphDateTextMarginLeft;

    private final float graphDateTextSize;
    private final float graphTemperatureTextSize;
    private final Locale locale;

    public CanvasView(final Context context)
    {
        this(context, null);
    }

    public CanvasView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, R.attr.seekBarPreferenceStyle);
    }

    @SuppressWarnings("deprecation")
    public CanvasView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        final float density = getResources().getDisplayMetrics().density;
        paint = new Paint();
        points = null;
        startDate = (new Date()).getTime();
        stopDate = (new Date()).getTime();

        graphDayLineColor = getResources().getColor(R.color.graphDayLineColor);
        graph6HourLineColor = getResources().getColor(R.color.graph6HourLineColor);
        graphHourLineColor = getResources().getColor(R.color.graphHourLineColor);
        graph5DegreeLineColor = getResources().getColor(R.color.graph5DegreeLineColor);
        graphDegreeLineColor = getResources().getColor(R.color.graphDegreeLineColor);
        graph0DegreeLineColor = getResources().getColor(R.color.graph0DegreeLineColor);
        graphLineColor = getResources().getColor(R.color.graphLineColor);

        graphDayLineWidth = getResources().getInteger(R.integer.graphDayLineWidth) * density;
        graph6HourLineWidth = getResources().getInteger(R.integer.graph6HourLineWidth) * density;
        graphHourLineWidth = getResources().getInteger(R.integer.graphHourLineWidth) * density;
        graph5DegreeLineWidth = getResources().getInteger(R.integer.graph5DegreeLineWidth) * density;
        graphDegreeLineWidth = getResources().getInteger(R.integer.graphDegreeLineWidth) * density;
        graph0DegreeLineWidth = getResources().getInteger(R.integer.graph0DegreeLineWidth) * density;
        graphLineWidth = getResources().getInteger(R.integer.graphLineWidth) * density;

        graphMarginLeft = getResources().getInteger(R.integer.graphMarginLeft) * density;
        graphMarginTop = getResources().getInteger(R.integer.graphMarginTop) * density;
        graphMarginRight = getResources().getInteger(R.integer.graphMarginRight) * density;
        graphMarginBottom = getResources().getInteger(R.integer.graphMarginBottom) * density;

        graphDateTextMarginBottom = getResources().getInteger(R.integer.graphDateTextMarginBottom) * density;
        graphDateTextMarginLeft = getResources().getInteger(R.integer.graphDateTextMarginLeft) * density;

        graphDateTextSize = getResources().getDimension(R.dimen.graphDateTextSize);
        graphTemperatureTextSize = getResources().getDimension(R.dimen.graphTemperatureTextSize);

        locale = getResources().getConfiguration().locale;
    }

    public void putNewData(final TemperatureData temperatureData)
    {
        if (temperatureData == null)
        {
            return;
        }

        this.points = temperatureData.getPoints();
        startDate = this.points[0].getDate();
        stopDate = this.points[points.length-1].getDate();

        minTemperature = temperatureData.getTemperatureMin();
        maxTemperature = temperatureData.getTemperatureMax();

        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        if (points == null)
        {
            return;
        }

        final long period =  stopDate - startDate;
        final int d_hours = getAddHours(period);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);

        //--ищем ближайшее время для отрисовки
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        if (isMediumRange(period))
        {
            int needHours = 6 - calendar.get(Calendar.HOUR_OF_DAY) % 6;
            calendar.add(Calendar.HOUR_OF_DAY, needHours);
        }

        if (isLargeRange(period))
        {
            int needHours = 24 - calendar.get(Calendar.HOUR_OF_DAY) % 24;
            calendar.add(Calendar.HOUR_OF_DAY, needHours);
        }

        while (calendar.getTimeInMillis() < stopDate)
        {
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            final long timestamp = calendar.getTimeInMillis();
            final float x = graphMarginLeft + ((getWidth() - graphMarginLeft - graphMarginRight) * (timestamp - startDate) / period);

            if (hour == 0)
            {
                paint.setColor(graphDayLineColor);
                paint.setStrokeWidth(graphDayLineWidth);
                canvas.drawLine(x, graphMarginTop, x, getHeight() - graphMarginBottom, paint);
            }
            else
            if ((hour % 6 == 0) && !isLargeRange(period))
            {
                paint.setColor(graph6HourLineColor);
                paint.setStrokeWidth(graph6HourLineWidth);
                canvas.drawLine(x, graphMarginTop, x, getHeight() - graphMarginBottom, paint);
            }
            else if (isSmallRange(period))
            {
                paint.setColor(graphHourLineColor);
                paint.setStrokeWidth(graphHourLineWidth);
                canvas.drawLine(x, graphMarginTop, x, getHeight() - graphMarginBottom, paint);
            }

            calendar.add(Calendar.HOUR_OF_DAY, d_hours);
        }

        //--температура
        paint.setTextAlign(Paint.Align.RIGHT);
        final int temperatureRange = maxTemperature - minTemperature;
        for (int i = 0; i <= temperatureRange; i++)
        {
            boolean draw = true;
            final int temperature = minTemperature + i;
            final float y = graphMarginTop + ((getHeight() - graphMarginTop - graphMarginBottom) * (temperatureRange - i) / temperatureRange);

            if (temperature == 0)
            {
                paint.setColor(graph0DegreeLineColor);
                paint.setStrokeWidth(graph0DegreeLineWidth);
            }
            else if (temperature % 5 == 0)
            {
                paint.setColor(graph5DegreeLineColor);
                paint.setStrokeWidth(graph5DegreeLineWidth);
            }
            else if (temperatureRange <= DEGREES_MINIMUM_DRAW)
            {
                paint.setColor(graphDegreeLineColor);
                paint.setStrokeWidth(graphDegreeLineWidth);
            }
            else
            {
                draw = false;
            }

            if (draw)
            {
                canvas.drawLine(graphMarginLeft, y, getWidth() - graphMarginRight, y, paint);

                paint.setTextSize(graphTemperatureTextSize);
                canvas.drawText(String.valueOf(temperature), graphMarginLeft - graphDateTextMarginLeft, y + graphDateTextMarginBottom, paint);
            }
        }

        //--текст после нанесения полосок
        calendar.setTimeInMillis(startDate);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        int needHours = 24 - calendar.get(Calendar.HOUR_OF_DAY) % 24;
        calendar.add(Calendar.HOUR_OF_DAY, needHours);

        paint.setTextAlign(Paint.Align.LEFT);
        while (calendar.getTimeInMillis() < stopDate)
        {
            final long timestamp = calendar.getTimeInMillis();
            float x = graphMarginLeft + ((getWidth() - graphMarginLeft - graphMarginRight) * (timestamp - startDate) / period) + graphDateTextMarginLeft;

            paint.setColor(graphDayLineColor);
            paint.setTextSize(graphDateTextSize);
            paint.setAntiAlias(true);

            String date;
            if (isSmallRange(period))
            {
                date = calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
            }
            else
            {
                date = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1);
                paint.setTextSize(graphTemperatureTextSize);
                x -= 2 * graphDateTextMarginLeft;
            }

            canvas.drawText(date, x, getHeight() - graphDateTextMarginBottom, paint);

            if (!isVeryLargeRange(period))
            {
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            }
            else
            {
                calendar.add(Calendar.HOUR_OF_DAY, 24 * 2);
            }
        }


        paint.setColor(graphLineColor);
        paint.setStrokeWidth(graphLineWidth);
        float last_x = -1;
        float last_y = -1;
        for (int i = 0; i < points.length; i++)
        {
            final TemperaturePoint temperaturePoint = points[i];
            final long date = temperaturePoint.getDate() - startDate;
            final float temperature = temperatureRange - (temperaturePoint.getTemperature() - minTemperature);
            final float x = graphMarginLeft + ((getWidth() - graphMarginLeft - graphMarginRight) * date / period);
            final float y = graphMarginTop + ((getHeight() - graphMarginTop - graphMarginBottom) * temperature / temperatureRange);

            if (last_x == -1 && last_y == -1)
            {
                canvas.drawLine(x, y, x, y, paint);
            }
            else
            {
                canvas.drawLine(last_x, last_y, x, y, paint);
            }
            last_x = x;
            last_y = y;
        }
    }

    private boolean isSmallRange(final long period)
    {
        return period <= HOURS_FULL_DRAW;
    }

    private boolean isMediumRange(final long period)
    {
        return period > HOURS_FULL_DRAW && period < HOURS_MEDIUM_DRAW;
    }

    private boolean isLargeRange(final long period)
    {
        return period >= HOURS_MEDIUM_DRAW;
    }

    private boolean isVeryLargeRange(final long period)
    {
        return period > HOURS_MINIMUM_DRAW;
    }

    private int getAddHours(final long period)
    {
        if (isSmallRange(period))
        {
            return 1;
        }
        if (isMediumRange(period))
        {
            return 6;
        }
        if (isLargeRange(period))
        {
            return 24;
        }
        return 1;
    }
}
