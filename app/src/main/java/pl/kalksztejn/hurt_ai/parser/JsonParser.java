package pl.kalksztejn.hurt_ai.parser;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class JsonParser {
    @SuppressLint("DefaultLocale")
    public static String parseJson(String jsonString) {
        StringBuilder result = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray predictionsArray = jsonObject.getJSONArray("predictions");
            if (predictionsArray.length() == 0) {
                return "NO DETECTED";
            }
            for (int i = 0; i < predictionsArray.length(); i++) {
                JSONObject predictionObject = predictionsArray.getJSONObject(i);

                // Pobierz parametry z obiektu prediction
                String type = predictionObject.getString("class");
                double height = predictionObject.getDouble("height");
                double width = predictionObject.getDouble("width");
                double probability = predictionObject.getDouble("confidence");

                // Dodaj parametry do wynikowego stringa
                result.append("Type: ").append(type).append("\n");
                result.append("Height: ").append(height).append("px\n");
                result.append("Width: ").append(width).append("px\n");
                result.append("Probability: ").append(String.format(Locale.US,"%.2f", probability*100)).append("%\n");

                // Możesz dodać inne parametry do wypisania z obiektu prediction
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
