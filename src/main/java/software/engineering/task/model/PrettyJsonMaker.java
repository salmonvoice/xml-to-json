package software.engineering.task.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * JSON converter to formatted JSON
 */
public class PrettyJsonMaker {
    /**
     * Convert not formatted JSON to a pretty one
     *
     * @param uglyJson - JSON to convert
     * @return pretty result
     */
    public static String convert(String uglyJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJson);
        return gson.toJson(je);
    }
}
