package com.example.android.findthemeaning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.searchText)
    EditText searchArea;

    @BindView(R.id.searchButton)
    Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String res = dictionaryEntries();
                if(res=="false"){
                    Toast.makeText(getApplicationContext(), "Not an English word", Toast.LENGTH_SHORT)
                            .show();
                }
                else if(res=="No input"){
                    Toast.makeText(getApplicationContext(), "Please enter a word", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                    new CallbackTask().execute(res);
            }
        });
    }

    private String dictionaryEntries() {
        final String language = "en";
        final String word = searchArea.getText().toString();
        if(TextUtils.isEmpty(word))
            return "No input";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        for(int i=0;i<word_id.length();++i){
            if(word_id.charAt(i)<97 || word_id.charAt(i)>122) {
                return "false";
            }
        }
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }


    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "fb3fc25c";
            final String app_key = "92e3e400116f3a4f74c580496c3fe82a";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
            intent.putExtra("key",result);
            startActivity(intent);

            System.out.println(result);
        }
    }

}
