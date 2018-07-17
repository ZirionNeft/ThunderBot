package thunder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class Misc {
    static String rounding (double d, int precise) {
        String b = "#.";
        for (int i = 0; i < precise; i++) {
            b += "#";
        }
        DecimalFormat df = new DecimalFormat(b);
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(d);
    }

    static String JSONObjectSearch (JSONObject object, String target) {
        for(Object fieldName : object.keySet()) {
            if (object.get(fieldName) instanceof JSONArray) {
                JSONArray array = (JSONArray) object.get(fieldName);
                // soon......
            }
        }
        return null;
    }

    static JSONObject HTTPQuery (String url) throws IOException,ParseException {
        JSONParser parser = new JSONParser();
        StringBuilder buffer = new StringBuilder();
        URL query = new URL(url);
        URLConnection connection = query.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            String input;
            while ((input = br.readLine()) != null) {
                buffer.append(input);
            }
        } finally {
            br.close();
        }
        return (JSONObject) parser.parse(buffer.toString());
    }

    static String getWeatherIcon (String icon) {
        return "http://openweathermap.org/img/w/" + icon +".png";
    }

    static String calculateTemperature(double T, String type) {
        if (type.equals("C")) {
            return Misc.rounding(T - 273.15, 1);
        } else if (type.equals("F")) {
            return Misc.rounding(T * 1.8 - 459.67, 1);
        }
        return null;
    }
}
