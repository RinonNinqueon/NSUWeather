package rinon.ninqueon.nsuweather.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Rinon Ninqueon on 04.04.2017.
 */

final class SharedPreferencesHelper
{

    private static SharedPreferences getDefaultSharedPreferences(final Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static int getSharedInteger(final Context context, final String key, final int defaultValue)
    {
        final SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getInt(key, defaultValue);
    }

    static int getSharedIntegerId(final Context context, final int keyId, final int defaultValueId)
    {
        final String key = context.getResources().getString(keyId);
        final int defaultValue = context.getResources().getInteger(defaultValueId);
        return getSharedInteger(context, key, defaultValue);
    }

    private static boolean getSharedBoolean(final Context context, final String key, final boolean defaultValue)
    {
        final SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(key, defaultValue);
    }

    static boolean getSharedBooleanId(final Context context, final int keyId, final int defaultValueId)
    {
        final String key = context.getResources().getString(keyId);
        final boolean defaultValue = context.getResources().getBoolean(defaultValueId);
        return getSharedBoolean(context, key, defaultValue);
    }
}
