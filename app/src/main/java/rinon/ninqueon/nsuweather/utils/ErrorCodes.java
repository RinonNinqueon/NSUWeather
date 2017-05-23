package rinon.ninqueon.nsuweather.utils;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 13.03.2017.
 */

public final class ErrorCodes
{
    public final static int ERROR_NO_CONNECTION            = -1;
    public final static int ERROR_DATABASE                 = -5;
    public final static int ERROR_IO_ERROR                 = -8;
    public final static int ERROR                          = 1;
    private ErrorCodes()
    {
        throw new UnsupportedOperationException("ErrorCodes.Constructor");
    }

    public static int getErrorMessageId(final int errorCode)
    {
        switch (errorCode)
        {
            case ERROR_NO_CONNECTION:
                return R.string.error_no_internet_connection;
            case ERROR_DATABASE:
                return R.string.error_database;
            case ERROR_IO_ERROR:
                return R.string.error_io;
            default:
                return R.string.error;
        }
    }
}
