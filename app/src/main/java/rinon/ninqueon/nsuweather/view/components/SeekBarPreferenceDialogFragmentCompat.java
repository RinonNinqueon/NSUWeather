package rinon.ninqueon.nsuweather.view.components;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seppius.i18n.plurals.PluralResources;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 04.04.2017.
 */

public final class SeekBarPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements SeekBar.OnSeekBarChangeListener
{
    private SeekBar seekBar;
    private TextView textView;
    private String prefix;
    private String postfix;
    private String postfixPlurals;
    private int minimum;
    private PluralResources pluralResources;

    private final static String PLURAL_HOUR = "minutes";
    private final static String PLURAL_DAY  = "days";

    public static SeekBarPreferenceDialogFragmentCompat newInstance(final String key)
    {
        final SeekBarPreferenceDialogFragmentCompat fragment = new SeekBarPreferenceDialogFragmentCompat();
        final Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onBindDialogView(final View view)
    {
        super.onBindDialogView(view);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        textView = (TextView) view.findViewById(R.id.textView);

        seekBar.setOnSeekBarChangeListener(this);

        if (seekBar == null)
        {
            throw new IllegalStateException("Dialog view must contain" +
                    " a SeekBar with id 'seekBar'");
        }

        if (textView == null)
        {
            throw new IllegalStateException("Dialog view must contain" +
                    " a TextView with id 'textView'");
        }

        final DialogPreference preference = getPreference();
        if (preference instanceof SeekBarPreference)
        {
            int seekBarProgress = ((SeekBarPreference) preference).getValue();
            seekBar.setProgress(seekBarProgress);
            prefix = ((SeekBarPreference) preference).getPrefix();
            postfix = ((SeekBarPreference) preference).getPostfix();
            postfixPlurals = ((SeekBarPreference) preference).getPostfixPlurals();
            minimum = ((SeekBarPreference) preference).getMinimum();
            final int maximum = ((SeekBarPreference) preference).getMaximum();

            seekBar.setMax(maximum);

            try
            {
                pluralResources = new PluralResources(getResources());
            }
            catch (NoSuchMethodException e)
            {
                pluralResources = null;
            }

            setProgressText(seekBarProgress);
        }
    }

    private void setProgressText(final int progress)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (prefix != null)
        {
            stringBuilder = stringBuilder.append(prefix).append(' ');
        }

        if (postfixPlurals != null && pluralResources != null)
        {
            stringBuilder = stringBuilder.append(' ');

            if (postfixPlurals.equals(PLURAL_HOUR))
            {
                String plural = pluralResources.getQuantityString(R.plurals.minutes_plurals, progress, progress);
                stringBuilder = stringBuilder.append(plural);
            }
            if (postfixPlurals.equals(PLURAL_DAY))
            {
                String plural = pluralResources.getQuantityString(R.plurals.days_plurals, progress, progress);
                stringBuilder = stringBuilder.append(plural);
            }
        }
        else
        {
            stringBuilder = stringBuilder.append(progress);
        }

        if (postfix != null)
        {
            stringBuilder = stringBuilder.append(' ').append(postfix);
        }

        textView.setText(stringBuilder);
    }

    @Override
    public void onDialogClosed(final boolean positiveResult)
    {
        if (!positiveResult)
        {
            return;
        }

        final int progress = seekBar.getProgress();
        final DialogPreference preference = getPreference();
        if (preference instanceof SeekBarPreference)
        {
            SeekBarPreference seekBarPreference = (SeekBarPreference) preference;
            if (seekBarPreference.callChangeListener(progress))
            {
                seekBarPreference.setValue(progress);
            }
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
    {
        if (progress < minimum)
        {
            seekBar.setProgress(minimum);
            return;
        }
        setProgressText(progress);
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar)
    {

    }
}
