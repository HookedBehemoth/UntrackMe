package app.fedilab.nitterizeme.viewmodels;
/* Copyright 2020 Thomas Schneider
 *
 * This file is a part of UntrackMe
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * UntrackMe is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with UntrackMe; if not,
 * see <http://www.gnu.org/licenses>. */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import app.fedilab.nitterizeme.entities.Instance;

import static app.fedilab.nitterizeme.activities.MainActivity.APP_PREFS;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_BIBLIOGRAM_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_INVIDIOUS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_NITTER_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_SCRIBERIP_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_TEDDIT_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_WIKILESS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_BIBLIOGRAM_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_INVIDIOUS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_NITTER_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_SCRIBERIP_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_TEDDIT_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_WIKILESS_HOST;

public class SearchInstanceVM extends AndroidViewModel {
    private MutableLiveData<List<Instance>> instancesMLD;

    public SearchInstanceVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Instance>> getInstances() {
        if (instancesMLD == null) {
            instancesMLD = new MutableLiveData<>();
            loadInstances();
        }

        return instancesMLD;
    }

    private void loadInstances() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                List<Instance> instances = getInstancesFromFedilabApp();
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = () -> instancesMLD.setValue(instances);
                mainHandler.post(myRunnable);
            }
        };
        thread.start();
    }

    private List<Instance> getInstancesFromFedilabApp() {
        HttpsURLConnection httpsURLConnection;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            String instances_url = "https://fedilab.app/untrackme_instances/payload_3.json";
            URL url = new URL(instances_url);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(10 * 1000);
            httpsURLConnection.setRequestProperty("http.keepAlive", "false");
            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty("Accept", "application/json");
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setDefaultUseCaches(true);
            httpsURLConnection.setUseCaches(true);
            String response = null;
            if (httpsURLConnection.getResponseCode() >= 200 && httpsURLConnection.getResponseCode() < 400) {
                java.util.Scanner s = new java.util.Scanner(httpsURLConnection.getInputStream()).useDelimiter("\\A");
                response = s.hasNext() ? s.next() : "";
            }
            httpsURLConnection.getInputStream().close();
            SharedPreferences sharedpreferences = getApplication().getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
            String defaultYoutube = sharedpreferences.getString(SET_INVIDIOUS_HOST, DEFAULT_INVIDIOUS_HOST);
            String defaultTwitter = sharedpreferences.getString(SET_NITTER_HOST, DEFAULT_NITTER_HOST);
            String defaultReddit = sharedpreferences.getString(SET_TEDDIT_HOST, DEFAULT_TEDDIT_HOST);
            String defaultInstagram = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, DEFAULT_BIBLIOGRAM_HOST);
            String defaultMedium = sharedpreferences.getString(SET_SCRIBERIP_HOST, DEFAULT_SCRIBERIP_HOST);
            String defaultWikipedia = sharedpreferences.getString(SET_WIKILESS_HOST, DEFAULT_WIKILESS_HOST);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayYoutube = jsonObject.getJSONArray("Youtube");
                    JSONArray jsonArrayTwitter = jsonObject.getJSONArray("Twitter");
                    JSONArray jsonArrayInstagram = jsonObject.getJSONArray("Instagram");
                    JSONArray jsonArrayReddit = jsonObject.getJSONArray("Reddit");
                    JSONArray jsonArrayMedium = jsonObject.getJSONArray("Medium");
                    JSONArray jsonArrayWikipedia = jsonObject.getJSONArray("Wikipedia");
                    for (int i = 0; i < jsonArrayYoutube.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayYoutube.getJSONObject(i).getString("domain");
                        String type = jsonArrayYoutube.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayYoutube.getJSONObject(i).getBoolean("cloudflare");
                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayYoutube.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayYoutube.getJSONObject(i).getJSONArray("country");

                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);
                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.YOUTUBE);
                        if (defaultYoutube != null && domain.compareTo(defaultYoutube) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayTwitter.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayTwitter.getJSONObject(i).getString("domain");
                        String type = jsonArrayTwitter.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayTwitter.getJSONObject(i).getBoolean("cloudflare");

                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayTwitter.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayTwitter.getJSONObject(i).getJSONArray("country");
                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);

                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.TWITTER);
                        if (defaultTwitter != null && domain.compareTo(defaultTwitter) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayInstagram.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayInstagram.getJSONObject(i).getString("domain");
                        String type = jsonArrayInstagram.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayInstagram.getJSONObject(i).getBoolean("cloudflare");

                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayInstagram.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayInstagram.getJSONObject(i).getJSONArray("country");
                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);

                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.INSTAGRAM);
                        if (defaultInstagram != null && domain.compareTo(defaultInstagram) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayReddit.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayReddit.getJSONObject(i).getString("domain");
                        String type = jsonArrayReddit.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayReddit.getJSONObject(i).getBoolean("cloudflare");

                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayReddit.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayReddit.getJSONObject(i).getJSONArray("country");
                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);

                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.REDDIT);
                        if (defaultReddit != null && domain.compareTo(defaultReddit) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayMedium.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayMedium.getJSONObject(i).getString("domain");
                        String type = jsonArrayMedium.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayMedium.getJSONObject(i).getBoolean("cloudflare");

                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayMedium.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayMedium.getJSONObject(i).getJSONArray("country");
                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);

                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.MEDIUM);
                        if (defaultMedium != null && domain.compareTo(defaultMedium) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayWikipedia.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayWikipedia.getJSONObject(i).getString("domain");
                        String type = jsonArrayWikipedia.getJSONObject(i).getString("type");
                        boolean cloudFlare = jsonArrayWikipedia.getJSONObject(i).getBoolean("cloudflare");

                        List<String> localesList = new ArrayList<>();
                        if (jsonArrayWikipedia.getJSONObject(i).has("country")) {
                            JSONArray locales = jsonArrayWikipedia.getJSONObject(i).getJSONArray("country");
                            for (int j = 0; j < locales.length(); j++) {
                                localesList.add(locales.getString(j));
                            }
                        } else {
                            localesList.add("--");
                        }
                        instance.setLocales(localesList);

                        instance.setDomain(domain);
                        instance.setType(type);
                        instance.setCloudflare(cloudFlare);
                        instance.setInstanceType(Instance.instanceType.WIKIPEDIA);
                        if (defaultWikipedia != null && domain.compareTo(defaultWikipedia) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instances;
    }


}
