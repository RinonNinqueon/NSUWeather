package rinon.ninqueon.nsuweather.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

import rinon.ninqueon.nsuweather.R;
import rinon.ninqueon.nsuweather.data.TemperatureData;
import rinon.ninqueon.nsuweather.view.components.CanvasView;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

final class MainView
{
    private final Context context;
    private final ListView drawerList;
    private final DrawerLayout drawerLayout;
    private final ActionBarDrawerToggle drawerToggle;
    private final CanvasView canvasView;
    private final ProgressDialog progressDialog;
    private final MainController mainController;

    MainView(final Context context,
             final View rootView,
             final String menuItems[],
             final MainController mainController)
    {
        this.context = context;
        this.mainController = mainController;
        drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawerList = (ListView) rootView.findViewById(R.id.left_drawer);
        canvasView = (CanvasView) rootView.findViewById(R.id.canvas);

        drawerList.setAdapter(new ArrayAdapter<>(context, R.layout.drawer_item, menuItems));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
            {
                mainController.onItemClick(position);
            }
        });

        Toolbar toolbar = ((MainActivity)context).initToolBar(R.id.toolbar);

        drawerToggle = new ActionBarDrawerToggle(((MainActivity)context), drawerLayout, toolbar, R.string.menu_open, R.string.menu_close)
        {
            public void onDrawerClosed(final View view)
            {
                ((MainActivity)context).invalidateOptionsMenu();
            }

            public void onDrawerOpened(final View drawerView)
            {
                ((MainActivity)context).invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
    }

    final void syncState()
    {
        drawerToggle.syncState();
    }

    final void onConfigurationChanged(final Configuration newConfig)
    {
        drawerToggle.onConfigurationChanged(newConfig);
    }

    final void closeDrawers()
    {
        drawerLayout.closeDrawers();
    }

    final void setItemChecked(final int position, final boolean value)
    {
        drawerList.setItemChecked(position, value);
    }

    final void putNewData(final TemperatureData temperatureData)
    {
        canvasView.putNewData(temperatureData);
    }

    final void setSubtitle(final int subtitle)
    {
        android.support.v7.app.ActionBar toolbar = ((MainActivity)context).getSupportActionBar();
        if (toolbar != null)
        {
            toolbar.setSubtitle(subtitle);
        }
    }

    final void setTitle(final String title)
    {
        ((MainActivity)context).setTitle(title);
    }

    final void setSubtitle(final String subtitle)
    {
        android.support.v7.app.ActionBar toolbar = ((MainActivity)context).getSupportActionBar();
        if (toolbar != null)
        {
            toolbar.setSubtitle(subtitle);
        }
    }

    final void showErrorToast(int messageId)
    {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    final void showProgressDialog()
    {
        progressDialog.show();
    }

    final void hideProgressDialog()
    {
        progressDialog.hide();
    }

    final void invalidateOptionsMenu()
    {
        ((MainActivity)context).invalidateOptionsMenu();
    }

    final void showDatePicker(final long dateMs, final long min, final long max, final boolean start)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMs);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month, final int dayOfMonth)
                    {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        if (start)
                        {
                            mainController.onDateStartSelect(calendar.getTimeInMillis());
                        }
                        else
                        {
                            mainController.onDateStopSelect(calendar.getTimeInMillis());
                        }
                        invalidateOptionsMenu();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        calendar.setTimeInMillis(min);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        calendar.setTimeInMillis(max);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.menu_cancel_button), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == DialogInterface.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
            }
        });
        //datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, context.getString(R.string.menu_ok_button), datePickerDialog);
        datePickerDialog.show();
    }
}
