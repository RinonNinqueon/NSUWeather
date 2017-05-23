package rinon.ninqueon.nsuweather.view.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import rinon.ninqueon.nsuweather.R;

/**
 * Created by Rinon Ninqueon on 04.04.2017.
 */

public final class SeekBarPreference extends DialogPreference
{
    private int progress;
    private final int minimum;
    private final int maximum;
    private final static int DEFAULT_ID = 0;
    private final static int DEFAULT_VALUE = 0;
    private final static int DEFAULT_MIN_VALUE = 0;
    private final static int DEFAULT_MAX_VALUE = 100;
    private final String postfix;
    private final String prefix;
    private final String postfixPlurals;
    private final static String COMPONENTS_NAMESPACE = "http://schemas.android.com/apk/lib/rinon.ninqueon.nsuweather.view.components";

    public SeekBarPreference(final Context context)
    {
        this(context, null);
    }

    public SeekBarPreference(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, R.attr.seekBarPreferenceStyle);
    }

    public SeekBarPreference(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreference(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        final String postfixPluralsName = getContext().getResources().getResourceEntryName(R.attr.postfix_plurals);
        final String prefixName = getContext().getResources().getResourceEntryName(R.attr.prefix);
        final String postfixName = getContext().getResources().getResourceEntryName(R.attr.postfix);
        final String minimumName = getContext().getResources().getResourceEntryName(R.attr.minimum);
        final String maximumName = getContext().getResources().getResourceEntryName(R.attr.maximum);

        final int minimumResId = attrs.getAttributeResourceValue(COMPONENTS_NAMESPACE, minimumName, DEFAULT_ID);
        final int maximumResId = attrs.getAttributeResourceValue(COMPONENTS_NAMESPACE, maximumName, DEFAULT_ID);
        final int prefixResId = attrs.getAttributeResourceValue(COMPONENTS_NAMESPACE, prefixName, DEFAULT_ID);
        final int postfixResId = attrs.getAttributeResourceValue(COMPONENTS_NAMESPACE, postfixName, DEFAULT_ID);

        if (minimumResId != DEFAULT_ID)
        {
            minimum = getContext().getResources().getInteger(minimumResId);
        }
        else
        {
            minimum = attrs.getAttributeIntValue(COMPONENTS_NAMESPACE, minimumName, DEFAULT_MIN_VALUE);
        }

        if (maximumResId != DEFAULT_ID)
        {
            maximum = getContext().getResources().getInteger(maximumResId);
        }
        else
        {
            maximum = attrs.getAttributeIntValue(COMPONENTS_NAMESPACE, maximumName, DEFAULT_MAX_VALUE);
        }

        if (prefixResId != DEFAULT_ID)
        {
            prefix = getContext().getResources().getString(prefixResId);
        }
        else
        {
            prefix = attrs.getAttributeValue(COMPONENTS_NAMESPACE, prefixName);
        }

        if (postfixResId != DEFAULT_ID)
        {
            postfix = getContext().getResources().getString(postfixResId);
        }
        else
        {
            postfix = attrs.getAttributeValue(COMPONENTS_NAMESPACE, postfixName);
        }


        postfixPlurals = attrs.getAttributeValue(COMPONENTS_NAMESPACE, postfixPluralsName);

        progress = DEFAULT_VALUE;
    }

    final int getValue()
    {
        return progress;
    }

    final void setValue(final int value)
    {
        progress = value;
        persistInt(progress);
    }

    final String getPostfix()
    {
        return postfix;
    }

    final String getPrefix()
    {
        return prefix;
    }

    final String getPostfixPlurals()
    {
        return postfixPlurals;
    }

    int getMinimum()
    {
        return minimum;
    }

    int getMaximum()
    {
        return maximum;
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index)
    {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue)
    {
        if (restorePersistedValue)
        {
            final int persistedInt = getPersistedInt(progress);
            setValue(persistedInt);
        }
        else
        {
            setValue((int) defaultValue);
        }
    }

    @Override
    public int getDialogLayoutResource()
    {
        return R.layout.seek_bar_preference;
    }
}
