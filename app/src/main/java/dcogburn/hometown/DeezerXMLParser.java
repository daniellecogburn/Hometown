package dcogburn.hometown;

import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 7/28/2017.
 */

public class DeezerXMLParser {
    String TAG = "DeezerXMLParser";

    // We don't use namespaces
    private static final String ns = null;

    public URL parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private URL readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList albums = new ArrayList();
        int eventType = parser.getEventType();
        String text = "";
        URL clip = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagname = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equals("track")) {

                    }
                    if (tagname.equals("preview")) {

                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (tagname.equals("preview")) {
                        clip = new URL(text);
                        return clip;
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
            //Log.d(TAG, text);
            Log.d(TAG, "clip: "+ clip);

        }
        return clip;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private URL readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "track");
        URL clip = null;
        Log.d(TAG, parser.getProperty("preview").toString());
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nm = parser.getName();
            if (nm.equals("preview")) {
                Log.d(TAG, "is preview "+ parser.getText());
                clip = new URL(parser.getText());
            } else if (nm.equals("image") && parser.getAttributeValue(0).contains("extralarge")) {
            } else {
                Log.d(TAG, "not preview " + parser.getName());
            }
        }
        return clip;
    }

    // Processes names in the xml.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    // Processes extra image link in the xml.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "image");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "image");
        return link;
    }


    // Extracts text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}