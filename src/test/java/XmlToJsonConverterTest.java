import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import software.engineering.task.model.PrettyJsonMaker;
import software.engineering.task.model.XmlToJsonConverter;

import java.nio.file.Files;
import java.nio.file.Paths;


public class XmlToJsonConverterTest {
    private static final String XML_FILE = "src/test/resources/data/input.txt";
    private static final String JSON_FILE = "src/test/resources/data/output.txt";
    private static String xml, json;

    @Before
    public void setUp() throws Exception {
        xml = new String(Files.readAllBytes(Paths.get(XML_FILE)));
        json = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
    }

    @Test
    public void converterTest() {
        Assert.assertEquals(json, PrettyJsonMaker.convert(XmlToJsonConverter.convert(xml)));
    }
}