package rinon.ninqueon.nsuweather.view.settingsScreen;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.services.AlarmHelper;
import rinon.ninqueon.nsuweather.services.BootReceiver;
import rinon.ninqueon.nsuweather.services.WeatherService;
import rinon.ninqueon.nsuweather.view.components.SeekBarPreference;
import rinon.ninqueon.nsuweather.view.components.SeekBarPreferenceDialogFragmentCompat;

/**
 * Created by Rinon Ninqueon on 05.03.2017.
 */

public final class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(final Bundle bundle, final String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);

        final Preference preference = findPreference(getString(R.string.settings_clear_database));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(final Preference preference)
            {
                clearDataBaseData();
                return false;
            }
        });
    }

    public static void loadSettingsFragment(final FragmentActivity host, final int contentFrameId, final boolean backtrace)
    {
        final SettingsFragment fragment = new SettingsFragment();
        final FragmentManager fragmentManager = host.getSupportFragmentManager();

        if (backtrace)
        {
            fragmentManager.beginTransaction().replace(contentFrameId, fragment).addToBackStack(null).commit();
        }
        else
        {
            fragmentManager.beginTransaction().replace(contentFrameId, fragment).commit();
        }
    }

    private void clearDataBaseData()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setPositiveButton(R.string.settings_clear_database_warning_yes,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        WeatherService.startDeleteItems(getContext());
                    }
                }).setNegativeButton(R.string.settings_clear_database_warning_no,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        dialog.cancel();
                    }
                })
                .setTitle(R.string.settings_clear_database_warning)
                .setMessage(R.string.settings_clear_database_warning)
                .create();

        builder.show();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key)
    {
        if (key.equals(getString(R.string.settings_update_enable)))
        {
            final boolean updateOnDeviceBootEnabled = sharedPreferences.getBoolean(key, false);

            if (updateOnDeviceBootEnabled)
            {
                BootReceiver.enableReceiver(getContext());
            }
            else
            {
                BootReceiver.disableReceiver(getContext());
            }
        }
        if (key.equals(getString(R.string.settings_update_period)))
        {
            AlarmHelper.setAlarm(getContext());
        }
    }

    @Override
    public void onDisplayPreferenceDialog(final Preference preference)
    {
        if (preference instanceof SeekBarPreference)
        {
            final DialogFragment dialogFragment = SeekBarPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            if (dialogFragment != null)
            {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            }
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
