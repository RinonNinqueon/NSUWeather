package rinon.ninqueon.nsuweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.logging.Logger;

import rinon.ninqueon.nsuweather.data.TemperatureData;
import rinon.ninqueon.nsuweather.data.TemperaturePoint;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class DatabaseHelper extends SQLiteOpenHelper
{
    private final static String LOGGER_TAG      = DatabaseHelper.class.getName();
    private final static Logger logger          = Logger.getLogger(LOGGER_TAG);
    private final static int DATABASE_VERSION   = 1;
    private final static String DATABASE_NAME   = "NSUWeather.db";

    public DatabaseHelper(final Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db)
    {
        try
        {
            db.execSQL(TemperatureTable.TemperatureTableEntry.SQL_CREATE_ENTRIES);
        }
        catch (final SQLException ex)
        {
            logger.severe(ex.getMessage());
        }
    }

    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        try
        {
            db.execSQL(TemperatureTable.TemperatureTableEntry.SQL_DELETE_ENTRIES);
        }
        catch (final SQLException ex)
        {
            logger.severe(ex.getMessage());
        }
        onCreate(db);
    }

    public final void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public final void writeTemperatureData(final SQLiteDatabase db, final TemperatureData temperatureData)
    {
        if (db == null || temperatureData == null || !db.isOpen())
        {
            return;
        }

        final TemperaturePoint points[] = temperatureData.getPoints();

        beginTransaction(db);

        try
        {
            for (int i = 0; i < points.length; i++)
            {
                final TemperaturePoint temperaturePoint = points[i];

                final ContentValues values = new ContentValues();
                values.put(TemperatureTable.TemperatureTableEntry.COL_DATE, temperaturePoint.getDate());
                values.put(TemperatureTable.TemperatureTableEntry.COL_TEMPERATURE, temperaturePoint.getTemperature());

                try
                {
                    db.insertOrThrow(TemperatureTable.TemperatureTableEntry.TABLE_NAME, null, values);
                }

                catch (final SQLiteConstraintException e)
                {
                    logger.warning("Value already exists.");
                }
            }
            setTransactionSuccessful(db);
        }
        finally
        {
            endTransaction(db);
        }
    }

    public final TemperaturePoint[] readTemperature(final SQLiteDatabase db, final long start, final long stop)
    {
        if (db == null)
        {
            return null;
        }

        final String query = "SELECT * " +
                " FROM " + TemperatureTable.TemperatureTableEntry.TABLE_NAME +
                " WHERE " + TemperatureTable.TemperatureTableEntry.COL_DATE + ">=" + start +
                " AND " + TemperatureTable.TemperatureTableEntry.COL_DATE + "<=" + stop +
                " ORDER BY " + TemperatureTable.TemperatureTableEntry.COL_DATE + " ASC";

        final Cursor cursor = db.rawQuery(query, null);
        final ArrayList<TemperaturePoint> result = new ArrayList<>();

        while(cursor.moveToNext())
        {
            final long date = cursor.getLong(cursor.getColumnIndexOrThrow(TemperatureTable.TemperatureTableEntry.COL_DATE));
            final float temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(TemperatureTable.TemperatureTableEntry.COL_TEMPERATURE));

            final TemperaturePoint feedEntry = new TemperaturePoint(temperature, date);

            result.add(feedEntry);
        }
        cursor.close();

        TemperaturePoint resultArray[] = new TemperaturePoint[result.size()];
        resultArray = result.toArray(resultArray);

        return resultArray;
    }

    public final TemperaturePoint[] readAverageTemperature(final SQLiteDatabase db, final long start, final long stop)
    {
        if (db == null)
        {
            return null;
        }

        /*
        SELECT strftime('%s', DATE(t.t_date/1000, 'unixepoch')) AS d_date,  avg(t.t_temp) AS average
        FROM temp t
        WHERE t.t_date>=1492923600000 AND t.t_date<=1493182800000
        GROUP BY DATE(t.t_date/1000, 'unixepoch')
        ORDER BY t.t_date ASC
        */

        final String query = "SELECT strftime('%s', DATE(t." + TemperatureTable.TemperatureTableEntry.COL_DATE + "/1000, 'unixepoch')) AS " + TemperatureTable.TemperatureTableEntry.COL_DATE + "," +
                " avg(t." + TemperatureTable.TemperatureTableEntry.COL_TEMPERATURE + ") AS " + TemperatureTable.TemperatureTableEntry.COL_TEMPERATURE +
                " FROM " + TemperatureTable.TemperatureTableEntry.TABLE_NAME +  " AS t" +
                " WHERE t." + TemperatureTable.TemperatureTableEntry.COL_DATE + ">=" + start +
                " AND t." + TemperatureTable.TemperatureTableEntry.COL_DATE + "<=" + stop +
                " GROUP BY DATE(t." + TemperatureTable.TemperatureTableEntry.COL_DATE + "/1000, 'unixepoch')" +
                " ORDER BY " + TemperatureTable.TemperatureTableEntry.COL_DATE + " ASC";

        final Cursor cursor = db.rawQuery(query, null);
        final ArrayList<TemperaturePoint> result = new ArrayList<>();

        while(cursor.moveToNext())
        {
            final long date = cursor.getLong(cursor.getColumnIndexOrThrow(TemperatureTable.TemperatureTableEntry.COL_DATE)) * 1000;
            final float temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(TemperatureTable.TemperatureTableEntry.COL_TEMPERATURE));

            logger.info("DATE " + date);

            final TemperaturePoint feedEntry = new TemperaturePoint(temperature, date);

            result.add(feedEntry);
        }
        cursor.close();

        TemperaturePoint resultArray[] = new TemperaturePoint[result.size()];
        resultArray = result.toArray(resultArray);

        return resultArray;
    }

    public final void deleteItemsAll(final SQLiteDatabase db)
    {
        if (db == null)
        {
            return;
        }

        db.delete(TemperatureTable.TemperatureTableEntry.TABLE_NAME, null, null);
    }

    private void beginTransaction(final SQLiteDatabase db)
    {
        if (db == null || !db.isOpen())
        {
            return;
        }

        if (!db.inTransaction())
        {
            db.beginTransaction();
        }
    }

    private void setTransactionSuccessful(final SQLiteDatabase db)
    {
        if (db == null || !db.isOpen())
        {
            return;
        }

        if (db.inTransaction())
        {
            db.setTransactionSuccessful();
        }
    }

    private void endTransaction(final SQLiteDatabase db)
    {
        if (db == null || !db.isOpen())
        {
            return;
        }

        if (db.inTransaction())
        {
            db.endTransaction();
        }
    }
}
