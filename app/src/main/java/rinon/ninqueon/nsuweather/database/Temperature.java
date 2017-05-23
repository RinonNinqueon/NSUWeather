package rinon.ninqueon.nsuweather.database;

import android.provider.BaseColumns;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

final class TemperatureTable
{
    private TemperatureTable()
    {
        throw new UnsupportedOperationException("TemperatureTable.Constructor");
    }

    final static class TemperatureTableEntry implements BaseColumns
    {
        final static String TABLE_NAME = "temperatureTable";
        final static String COL_TEMPERATURE = "temperature";
        final static String COL_DATE = "dateStamp";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TemperatureTableEntry.TABLE_NAME + " (" +
                        TemperatureTableEntry._ID + " INTEGER PRIMARY KEY, " +
                        TemperatureTableEntry.COL_TEMPERATURE + " REAL, " +
                        TemperatureTableEntry.COL_DATE + " INTEGER, " +
                        "CONSTRAINT " + TemperatureTableEntry.COL_DATE + "_unique UNIQUE(" + TemperatureTableEntry.COL_DATE + "))";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TemperatureTableEntry.TABLE_NAME;
    }
}
