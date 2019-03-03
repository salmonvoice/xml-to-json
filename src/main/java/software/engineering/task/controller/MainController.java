package software.engineering.task.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import software.engineering.task.data.ExchangeRates;
import software.engineering.task.data.ExchangeRatesRepository;
import software.engineering.task.model.PrettyJsonMaker;
import software.engineering.task.model.XmlToJsonConverter;
import software.engineering.task.pojo.BuySell;
import software.engineering.task.pojo.RateAndDate;
import software.engineering.task.pojo.Rates;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class waits and processes all incoming requests
 */
@RestController
public class MainController {


    /**
     * WARN - free api key works only for 1000 usages
     */
    private static final String API_KEY = "fdc02ed89821849c87fb80b6377f308a";
    private static final String BASE_CURRENCY = "EUR";

    @Autowired
    private RestTemplate restTemplate;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ExchangeRatesRepository exchangeRatesRepository;


    /**
     * Method processes incoming XML and convert it to JSON.
     *
     * @param xml - XML to convert
     * @return - converted JSON or "Not valid" message if XML is not valid
     */
    @PostMapping(path = "/", consumes = "application/xml")
    public ResponseEntity<String> xmlToJson(@RequestBody String xml) {
        System.out.println("request: " + xml);
        try {
            return ResponseEntity.ok(PrettyJsonMaker.convert(XmlToJsonConverter.convert(xml)));
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not valid XML\n");
        }
    }

    @GetMapping("bestDaysPdf")
    public void generatePdf(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, HttpServletResponse response) throws DocumentException, IOException {
        Map<String, BuySell> bestbuySell = getStringBuySellMap(startDate, endDate);

        Document document = new Document();
        response.addHeader("Content-Type", "application/pdf");
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        for (Map.Entry<String, BuySell> entry : bestbuySell.entrySet()) {
            table.addCell(entry.getKey());
            table.addCell(dateTimeFormatter.format(entry.getValue().getBuyDate()));
            table.addCell(dateTimeFormatter.format(entry.getValue().getSellDate()));
        }

        Paragraph paragraph2 = new Paragraph();
        paragraph2.add(table);
        document.add(paragraph2);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Currency", "Buy date", "Cell date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }



    @GetMapping(value = "bestDays", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, BuySell>> bestDaysToBuyAndSell(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        System.out.println("startDate = " + startDate);
        System.out.println("endDate = " + endDate);

        Map<String, BuySell> bestbuySell = getStringBuySellMap(startDate, endDate);

        return new ResponseEntity<>(bestbuySell, HttpStatus.OK);
    }

    private Map<String, BuySell> getStringBuySellMap(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        LocalDate date1 = dateTimeFormatter.parse(startDate, LocalDate::from);
        LocalDate date2 = dateTimeFormatter.parse(endDate, LocalDate::from);

        if (date1.isAfter(date2)) {
            LocalDate buf = date1;
            date1 = date2;
            date2 = buf;
        }

        LocalDate curDate = date1;
        Map<String, RateAndDate> ratesMap = new HashMap<>();
        while (!curDate.isAfter(date2)) {
            ExchangeRates rates = getRates(curDate);

            Map<String, Double> rates1 = rates.getRates();

            for (String currency : rates1.keySet()) {
                ratesMap.putIfAbsent(currency, new RateAndDate());
                RateAndDate rateAndDate = ratesMap.get(currency);
                Double rateVal = rates1.get(currency);
                if (rateAndDate.min == 0 || rateVal < rateAndDate.min) {
                    rateAndDate.min = rateVal;
                    rateAndDate.minDate = curDate;
                }
                if (rateAndDate.max == 0 || rateVal > rateAndDate.max) {
                    rateAndDate.max = rateVal;
                    rateAndDate.maxDate = curDate;
                }
            }
            curDate = curDate.plus(1, ChronoUnit.DAYS);
        }


        Map<String, BuySell> bestbuySell = new HashMap<>();


        for (Map.Entry<String, RateAndDate> entry : ratesMap.entrySet()) {
            bestbuySell.put(entry.getKey(), new BuySell(entry.getValue().minDate, entry.getValue().maxDate, entry.getKey()));
        }
        return bestbuySell;
    }

    private ExchangeRates getRates(LocalDate date){
        String dateTxt = dateTimeFormatter.format(date);

        Optional<ExchangeRates> byDate = exchangeRatesRepository.findById(dateTxt);
        if (byDate.isPresent()) {
            return byDate.get();
        }
//        String url = "https://api.exchangeratesapi.io/{date}?base={base}";  //not working inaccessible
        String url = "http://data.fixer.io/api/{date}?access_key={key}&base={base}";
        ResponseEntity<Rates> exchange = restTemplate.exchange(url, HttpMethod.GET, null, Rates.class,
                dateTxt, API_KEY, BASE_CURRENCY);
        Rates body = exchange.getBody();
        System.out.println("rates = " + body);

        ExchangeRates exchangeRates = new ExchangeRates();
        exchangeRates.setDate(dateTxt);
        exchangeRates.setBaseCurrency(BASE_CURRENCY);
        Map<String, Double> rr = new HashMap<>();
        rr.put("usd", body.rates.USD);
        rr.put("eur", body.rates.RUB);// using RUB cost instead of EUR because RUB is restricted as base currency of fixer.io
        exchangeRates.setRates(rr);
        exchangeRatesRepository.save(exchangeRates);
        return exchangeRates;
    }
}
