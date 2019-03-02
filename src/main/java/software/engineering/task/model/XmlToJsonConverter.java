package software.engineering.task.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Convert XML to JSON
 */
public class XmlToJsonConverter {
    /**
     * Convert XML to not formatted JSON
     *
     * @param xml - XML to convert
     * @return - converted JSON
     * @throws JSONException if XML is not valid
     */
    public static String convert(String xml) throws JSONException {
        JSONObject json = XML.toJSONObject(xml);
        return json.toString();
    }
}
