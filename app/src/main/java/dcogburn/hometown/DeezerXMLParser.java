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
            Log.d(TAG, text);
            Log.d(TAG, "clip: "+ clip);

        }
        return clip;
    }

}