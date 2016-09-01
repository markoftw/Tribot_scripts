package scripts.redDrags;

import org.tribot.api.General;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class PriceChecker {

    public static String[] userAgents = new String[]{"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
        "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/533.1 (KHTML, like Gecko) Maxthon/3.0.8.2 Safari/533.1",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A"};

    public static int getOSbuddyPrice(int itemID) {
        return handleResult("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=", itemID, "overall");
    }

    public static int getGEPrice(int itemID) {
        return handleResult("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=", itemID, "price");
    }

    private static int handleResult(String url, int itemID, String key) {
        String queryResult = httpRequest(url + itemID);

        if (queryResult != null) {
            HashMap<String, String> results = JSONToMap(queryResult);

            if (results.containsKey(key)) {
                return Integer.parseInt(results.get(key));
            }
        }

        return -1;
    }

    private static String httpRequest(final String url) {
        try {
            String content = "";
            String line;

            URLConnection con = new URL(url).openConnection();

            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestProperty("User-Agent", userAgents[General.random(0, userAgents.length - 1)]);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((line = in.readLine()) != null) {
                content = content + line;
            }

            in.close();

            return (content.isEmpty()) ? null : content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HashMap<String, String> JSONToMap(String json) {
        HashMap<String, String> map = new HashMap<>();

        String[] parts = json.replace("{", "").replace("}", "").replaceAll("\"", "").split(",");

        for (String part : parts) {
            String[] pieces = part.split(":");

            if (map.containsKey(pieces[0])) {
                int c = 1;

                while (map.containsKey(pieces[0] + c)) {
                    c++;
                }

                pieces[0] = pieces[0] + c;
            }

            map.put(pieces[0], pieces[1]);
        }

        return map;
    }
}
