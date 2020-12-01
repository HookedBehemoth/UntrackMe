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
import static app.fedilab.nitterizeme.activities.MainActivity.DEFAULT_TEDDIT_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_BIBLIOGRAM_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_INVIDIOUS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_NITTER_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_TEDDIT_HOST;

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
                List<Instance> bibliogramInstances = getInstancesFromBibliogramArt();
                instances.addAll(bibliogramInstances);
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
            String instances_url = "https://fedilab.app/untrackme_instances/payload_2.json";
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
            String defaultInvidious = sharedpreferences.getString(SET_INVIDIOUS_HOST, DEFAULT_INVIDIOUS_HOST);
            String defaultNitter = sharedpreferences.getString(SET_NITTER_HOST, DEFAULT_NITTER_HOST);
            String defaultTeddit = sharedpreferences.getString(SET_TEDDIT_HOST, DEFAULT_TEDDIT_HOST);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayInvidious = jsonObject.getJSONArray("invidious");
                    JSONArray jsonArrayNitter = jsonObject.getJSONArray("nitter");
                    JSONArray jsonArrayTeddit = jsonObject.getJSONArray("teddit");
                    for (int i = 0; i < jsonArrayInvidious.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayInvidious.getJSONObject(i).getString("domain");
                        boolean cloudFlare = jsonArrayInvidious.getJSONObject(i).getBoolean("cloudflare");
                        String locale = jsonArrayInvidious.getJSONObject(i).getString("locale");
                        instance.setDomain(domain);
                        instance.setCloudflare(cloudFlare);
                        instance.setLocale(locale);
                        instance.setType(Instance.instanceType.INVIDIOUS);
                        if (defaultInvidious != null && domain.compareTo(defaultInvidious) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayNitter.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayNitter.getJSONObject(i).getString("domain");
                        boolean cloudFlare = jsonArrayNitter.getJSONObject(i).getBoolean("cloudflare");
                        String locale = jsonArrayNitter.getJSONObject(i).getString("locale");
                        instance.setDomain(domain);
                        instance.setCloudflare(cloudFlare);
                        instance.setLocale(locale);
                        instance.setType(Instance.instanceType.NITTER);
                        if (defaultNitter != null && domain.compareTo(defaultNitter) == 0) {
                            instance.setChecked(true);
                        }
                        instances.add(instance);
                    }
                    for (int i = 0; i < jsonArrayTeddit.length(); i++) {
                        Instance instance = new Instance();
                        String domain = jsonArrayTeddit.getJSONObject(i).getString("domain");
                        boolean cloudFlare = jsonArrayTeddit.getJSONObject(i).getBoolean("cloudflare");
                        String locale = jsonArrayTeddit.getJSONObject(i).getString("locale");
                        instance.setDomain(domain);
                        instance.setCloudflare(cloudFlare);
                        instance.setLocale(locale);
                        instance.setType(Instance.instanceType.TEDDIT);
                        if (defaultTeddit != null && domain.compareTo(defaultTeddit) == 0) {
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


    private List<Instance> getInstancesFromBibliogramArt() {
        HttpsURLConnection httpsURLConnection;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            String instances_url = "https://bibliogram.art/api/instances";
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
            String defaultBibliogram = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, DEFAULT_BIBLIOGRAM_HOST);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayBibliogram = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArrayBibliogram.length(); i++) {
                        Instance instance = new Instance();
                        String url_bibliogram = jsonArrayBibliogram.getJSONObject(i).getString("address");
                        URL urlBibliogram = new URL(url_bibliogram);
                        String domain = urlBibliogram.getHost();
                        boolean cloudFlare = jsonArrayBibliogram.getJSONObject(i).getBoolean("using_cloudflare");
                        String locale = jsonArrayBibliogram.getJSONObject(i).getString("country");
                        instance.setDomain(domain);
                        instance.setCloudflare(cloudFlare);
                        instance.setLocale(locale);
                        instance.setType(Instance.instanceType.BIBLIOGRAM);
                        if (defaultBibliogram != null && domain.compareTo(defaultBibliogram) == 0) {
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
