package rinon.ninqueon.nsuweather.data;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rinon Ninqueon on 25.04.2017.
 */

public final class XMLParser
{
    private final static String TAG_WEATHER = "weather";
    private final static String TAG_CURRENT = "current";
    private final static String TAG_AVERAGE = "average";
    private final static String TAG_GRAPH = "graph";
    private final static String TAG_TEMP = "temp";
    private final static String ATTRIBUTE_TIMESTAMP = "timestamp";

    public static TemperatureData parseXML(final InputStream inputStream, final String inputEncoding, final boolean onlyCurrent) throws XmlPullParserException, IOException, ParseException
    {
        final XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);

        final XmlPullParser xmlParser = xmlFactory.newPullParser();
        xmlParser.setInput(inputStream, inputEncoding);

        return parseXML(xmlParser, onlyCurrent);
    }

    private static TemperatureData parseXML(final XmlPullParser xmlParser, final boolean onlyCurrent) throws XmlPullParserException, IOException, ParseException
    {
        int eventType = xmlParser.getEventType();

        if (eventType != XmlPullParser.START_DOCUMENT)
        {
            throw new ParseException("Invalid XML File", xmlParser.getLineNumber());
        }

        xmlParser.nextTag();

        xmlParser.require(XmlPullParser.START_TAG, null, TAG_WEATHER);

        eventType = xmlParser.nextTag();

        float current = 0;
        final ArrayList<TemperaturePoint> points = new ArrayList<>();

        while (eventType != XmlPullParser.END_TAG)
        {
            String tagName = xmlParser.getName();
            switch (tagName)
            {
                case TAG_AVERAGE:
                    readTagText(xmlParser, TAG_AVERAGE);
                    break;
                case TAG_CURRENT:
                    final String currentString = readTagText(xmlParser, TAG_CURRENT);
                    try
                    {
                        current = Float.parseFloat(currentString);
                    }
                    catch (final NumberFormatException e)
                    {
                        throw new ParseException("Wrong number format", xmlParser.getLineNumber());
                    }

                    if (onlyCurrent)
                    {
                        TemperaturePoint point = new TemperaturePoint(current, (new Date()).getTime());
                        points.add(point);
                        return new TemperatureData(points, current);
                    }

                    break;
                case TAG_GRAPH:
                    points.addAll(parsePoints(xmlParser));
                    break;
            }
            eventType = xmlParser.nextTag();
        }

        return new TemperatureData(points, current);
    }

    private static String readTagText(final XmlPullParser xmlParser, final String tag) throws IOException, XmlPullParserException
    {
        xmlParser.require(XmlPullParser.START_TAG, null, tag);
        String text = readText(xmlParser);
        xmlParser.require(XmlPullParser.END_TAG, null, tag);
        return text;
    }

    private static String readText(final XmlPullParser xmlParser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (xmlParser.next() == XmlPullParser.TEXT)
        {
            result = xmlParser.getText();
            xmlParser.nextTag();
        }
        return result;
    }

    private static ArrayList<TemperaturePoint> parsePoints(final XmlPullParser xmlParser) throws IOException, XmlPullParserException, ParseException
    {
        xmlParser.require(XmlPullParser.START_TAG, null, TAG_GRAPH);
        int eventType = xmlParser.nextTag();

        final ArrayList<TemperaturePoint> points = new ArrayList<>();

        while (eventType != XmlPullParser.END_TAG)
        {
            String tagName = xmlParser.getName();
            if (TAG_TEMP.equals(tagName))
            {
                final String timestampStrings = xmlParser.getAttributeValue(null, ATTRIBUTE_TIMESTAMP);
                final String temperatureString = readTagText(xmlParser, TAG_TEMP);
                try
                {
                    final long timestamp = Long.parseLong(timestampStrings) * 1000;
                    final float temperature = Float.parseFloat(temperatureString);
                    final TemperaturePoint temperaturePoint = new TemperaturePoint(temperature, timestamp);
                    points.add(temperaturePoint);

                }
                catch (final NumberFormatException e)
                {
                    throw new ParseException("Wrong number format", xmlParser.getLineNumber());
                }
            }
            else
            {
                throw new ParseException("Wrong XML format", xmlParser.getLineNumber());
            }
            eventType = xmlParser.nextTag();
        }

        return points;
    }
}
