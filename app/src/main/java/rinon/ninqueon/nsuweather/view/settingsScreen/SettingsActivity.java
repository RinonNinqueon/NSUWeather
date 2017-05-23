package rinon.ninqueon.nsuweather.view.settingsScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 05.03.2017.
 */

public final class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        initToolBar(R.id.toolbar);

        setTitle(R.string.settings_title);

        SettingsFragment.loadSettingsFragment(this, R.id.content_frame, false);
    }

    private void initToolBar(final int toolBarId)
    {
        final Toolbar toolbar = (Toolbar) findViewById(toolBarId);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private static Intent getInstance(final Context host)
    {
        return new Intent(host, SettingsActivity.class);
    }

    public static void openSettings(final Context context)
    {
        Intent intent = SettingsActivity.getInstance(context);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }
}
