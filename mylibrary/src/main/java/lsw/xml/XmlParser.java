package lsw.xml;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by lsw_wsl on 8/3/15.
 */
public abstract class XmlParser<T> {

    public XmlParser(InputStream inputStream) {
        parser(inputStream);
    }

    public abstract void startDocument(XmlPullParser parser);
    public abstract void startTag(XmlPullParser parser) throws Exception;
    public abstract void endTag(XmlPullParser parser);

    public void parser(InputStream inputStream) {
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        startDocument(parser);
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        startTag(parser);
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        endTag(parser);
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            Log.d("xml", ex.getMessage());
        }
    }

    public abstract T getT();
}
