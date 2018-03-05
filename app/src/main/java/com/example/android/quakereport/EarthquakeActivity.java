/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<String>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private String HTTPurl =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static String HTTPurlin =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&minlatitude=6.75&maxlatitude=37.1&minlongitude=68.1&maxlongitude=97.4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            android.app.LoaderManager loaderManager = getLoaderManager();
            Log.i("Out","yess");
            loaderManager.initLoader(0,null,this);
        }
        else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.emp);
            progressBar.setVisibility(View.GONE);
            TextView tv = (TextView) findViewById(R.id.noI);
            tv.setText("No Internet Connection ");
        }

    }

    private void createList(final String JSONstr){
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        try{
            JSONObject root = new JSONObject(JSONstr);
            JSONArray features = root.getJSONArray("features");

            if(features.length() == 0)
            {
                TextView textView = (TextView) findViewById(R.id.noE);
                textView.setText("No Earthquakes found for current Preferences.");
                textView.setVisibility(View.VISIBLE);
            }

            for(int i = 0; i < features.length(); i++)
            {
                JSONObject eqs = features.getJSONObject(i);
                JSONObject properties = eqs.getJSONObject("properties");
                String mag = properties.optString("mag");
                String loc = properties.optString("place");
                String time = properties.optString("time");
                earthquakes.add(new Earthquake(mag,loc,time));
            }
        }catch (JSONException e){
            Log.e("Exception Message huh",e.getMessage());
        }


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    JSONObject root = new JSONObject(JSONstr);
                    JSONArray features = root.getJSONArray("features");
                    JSONObject eqs = features.getJSONObject(position);
                    JSONObject properties = eqs.getJSONObject("properties");
                    String url = properties.optString("url");

                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }catch (JSONException e){
                    Log.e("Exception Message",e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.content.Loader<String> onCreateLoader(int id, Bundle args) {
        Log.i("Out","yes");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String limit = sharedPrefs.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));
        String startTime = sharedPrefs.getString(
                getString(R.string.settings_start_time_key),
                getString(R.string.settings_start_time_default));

        Uri baseUri = Uri.parse(HTTPurl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("starttime", startTime);

        return new EarthquakeLoader(EarthquakeActivity.this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.content.Loader<String> loader, String data) {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.emp);
        progressBar.setVisibility(View.GONE);
        if(data != null) {
            Log.i("Out","yesh");
            createList(data);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<String> loader) {
        createList("");
    }


    public static class EarthquakeLoader extends AsyncTaskLoader<String> {
        private String URL;

        public EarthquakeLoader(Context context, String url){
            super(context);
            this.URL = url;
        }

        @Override
        protected void onStartLoading(){
            forceLoad();
        }

        @Override
        public String loadInBackground() {

            Log.i("URL",URL);

            if(URL == null)
                return null;
            String JSONoutput = null;
            URL url = createUrl(URL);
            try {
                JSONoutput = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Out","yes");
            return JSONoutput;
        }
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
