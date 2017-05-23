package rinon.ninqueon.nsuweather.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class MainActivity extends AppCompatActivity
{
    private MainController mainController;
    private MainBroadcastReceiver mainBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController(this, findViewById(android.R.id.content));
        mainBroadcastReceiver = new MainBroadcastReceiver(this, mainController);
        mainController.onCreate();
    }

    public final Toolbar initToolBar(final int toolBarId)
    {
        final Toolbar toolbar = (Toolbar) findViewById(toolBarId);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return toolbar;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mainController.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mainController.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mainBroadcastReceiver.registerBroadcastReceiver();
        mainController.checkAndLoadDataFromService();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mainBroadcastReceiver.unregisterBroadcastReceiver();
        mainController.unBindService();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.period_menu, menu);
        mainController.setMenuItemsVisibility(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        mainController.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}
