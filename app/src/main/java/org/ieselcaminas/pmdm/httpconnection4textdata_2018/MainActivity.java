package org.ieselcaminas.pmdm.httpconnection4textdata_2018;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    private class DownloadTextTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... urls) {
            InputStream in;
            BufferedReader inReader;
            String s = null;
            StringBuilder stBuilder = new StringBuilder();
            try {
                in = openHttpConnection(urls[0]);
                inReader = new BufferedReader(new InputStreamReader(in));
                while ((s = inReader.readLine()) != null) {
                    stBuilder.append(s+"\n");
                }
                s = stBuilder.toString();
                in.close();
            } catch (IOException e1) {
                Log.d("NetworkingActivity", e1.getLocalizedMessage());
            }
            return s;
        }

        protected void onPostExecute(String result) {
            textView.setText(result);
        }

        private InputStream openHttpConnection(String urlString) throws IOException {
            InputStream in = null;
            int response;
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            if (!(conn instanceof HttpURLConnection))
                throw new IOException("Not an HTTP connection");
            try{
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                response = httpConn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                }
            }
            catch (Exception ex) {
                Log.d("Networking", ex.getLocalizedMessage());
                throw new IOException("Error connecting");
            }
            return in;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data
                    DownloadTextTask downloadTextTask = new DownloadTextTask();
                    downloadTextTask.execute("https://google.com");
                } else {
                    // display error
                    Toast.makeText(getApplicationContext(), "No internet connection available.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}