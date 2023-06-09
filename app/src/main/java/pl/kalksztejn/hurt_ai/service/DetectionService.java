package pl.kalksztejn.hurt_ai.service;

import static pl.kalksztejn.hurt_ai.utils.ImageUtils.drawableToBase64;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class DetectionService {

    private static final String API_ENDPOINT = "https://detect.roboflow.com/wound-classification/4";
    private static final String API_KEY = "c3OvgABi5HOyvtBej2tA";


    public void sendImage(Drawable drawable, ApiResponseCallback callback) {
        String base64Image = drawableToBase64(drawable);
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String uploadURL = API_ENDPOINT + "?api_key=" + API_KEY + "&name=" + fileName;

        NetworkTask networkTask = new NetworkTask(callback);
        networkTask.execute(uploadURL, base64Image);
    }

    public interface ApiResponseCallback {
        void onSuccess(String response);

        void onFailure(String errorMessage);
    }

    private static class NetworkTask extends AsyncTask<String, Void, String> {
        private final ApiResponseCallback callback;

        public NetworkTask(ApiResponseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String uploadURL = params[0];
            String base64Image = params[1];

            HttpURLConnection connection = null;
            try {
                URL url = new URL(uploadURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(base64Image.length()));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(base64Image);
                wr.close();

                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                callback.onSuccess(response);
            } else {
                callback.onFailure("Request failed");
            }
        }
    }
}