package app.fedilab.nitterizeme.activities;
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


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.adapters.InstanceAdapter;
import app.fedilab.nitterizeme.entities.Instance;
import app.fedilab.nitterizeme.viewmodels.SearchInstanceVM;


public class InstanceActivity extends AppCompatActivity {

    private static final String list_for_instances = "https://framagit.org/tom79/fedilab_app/-/blob/master/content/untrackme_instances/payload_2.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_instance);
        setTitle(R.string.select_instances);

        SearchInstanceVM viewModel = new ViewModelProvider(this).get(SearchInstanceVM.class);
        viewModel.getInstances().observe(this, result -> {
            LinearLayout instance_container = findViewById(R.id.instance_container);
            RelativeLayout loader = findViewById(R.id.loader);
            RecyclerView invidious_instances = findViewById(R.id.invidious_instances);
            RecyclerView nitter_instances = findViewById(R.id.nitter_instances);
            RecyclerView bibliogram_instances = findViewById(R.id.bibliogram_instances);
            Button latency_test = findViewById(R.id.latency_test);
            ImageButton instance_info = findViewById(R.id.instance_info);
            Button close = findViewById(R.id.close);
            if (result == null) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, R.string.error_message_internet, Snackbar.LENGTH_LONG).setAction(R.string.close, v -> finish()).show();
                return;
            }

            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
            String invidiousHost = sharedpreferences.getString(MainActivity.SET_INVIDIOUS_HOST, MainActivity.DEFAULT_INVIDIOUS_HOST);
            String nitterHost = sharedpreferences.getString(MainActivity.SET_NITTER_HOST, MainActivity.DEFAULT_NITTER_HOST);
            String bibliogramHost = sharedpreferences.getString(MainActivity.SET_BIBLIOGRAM_HOST, MainActivity.DEFAULT_BIBLIOGRAM_HOST);

            ArrayList<Instance> invidiousInstances = new ArrayList<>();
            ArrayList<Instance> nitterInstances = new ArrayList<>();
            ArrayList<Instance> bibliogramInstances = new ArrayList<>();
            boolean customInvidiousInstance = true;
            boolean customNitterInstance = true;
            boolean customBibliogramInstance = true;

            for (Instance instance : result) {
                if (instance.getType() == Instance.instanceType.INVIDIOUS) {
                    invidiousInstances.add(instance);
                    if (invidiousHost != null && invidiousHost.trim().toLowerCase().compareTo(instance.getDomain()) == 0) {
                        customInvidiousInstance = false;
                    }
                } else if (instance.getType() == Instance.instanceType.NITTER) {
                    nitterInstances.add(instance);
                    if (nitterHost != null && nitterHost.trim().toLowerCase().compareTo(instance.getDomain()) == 0) {
                        customNitterInstance = false;
                    }
                } else if (instance.getType() == Instance.instanceType.BIBLIOGRAM) {
                    bibliogramInstances.add(instance);
                    if (bibliogramHost != null && bibliogramHost.trim().toLowerCase().compareTo(instance.getDomain()) == 0) {
                        customBibliogramInstance = false;
                    }
                }
            }
            //Check if custom instances are also added
            if (customInvidiousInstance) {
                Instance instance = new Instance();
                instance.setChecked(true);
                instance.setDomain(invidiousHost);
                instance.setLocale("--");
                invidiousInstances.add(0, instance);
            }
            if (customNitterInstance) {
                Instance instance = new Instance();
                instance.setChecked(true);
                instance.setDomain(nitterHost);
                instance.setLocale("--");
                nitterInstances.add(0, instance);
            }
            if (customBibliogramInstance) {
                Instance instance = new Instance();
                instance.setChecked(true);
                instance.setDomain(bibliogramHost);
                instance.setLocale("--");
                bibliogramInstances.add(0, instance);
            }

            final LinearLayoutManager iLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter invidiousAdapter = new InstanceAdapter(invidiousInstances);
            invidious_instances.setAdapter(invidiousAdapter);
            invidious_instances.setLayoutManager(iLayoutManager);
            invidious_instances.setNestedScrollingEnabled(false);

            final LinearLayoutManager nLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter nitterAdapter = new InstanceAdapter(nitterInstances);
            nitter_instances.setAdapter(nitterAdapter);
            nitter_instances.setLayoutManager(nLayoutManager);
            nitter_instances.setNestedScrollingEnabled(false);

            final LinearLayoutManager bLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter bibliogramAdapter = new InstanceAdapter(bibliogramInstances);
            bibliogram_instances.setAdapter(bibliogramAdapter);
            bibliogram_instances.setLayoutManager(bLayoutManager);
            bibliogram_instances.setNestedScrollingEnabled(false);
            latency_test.setOnClickListener(
                    v -> {
                        invidiousAdapter.evalLatency();
                        nitterAdapter.evalLatency();
                        bibliogramAdapter.evalLatency();
                    }
            );

            instance_info.setOnClickListener(v -> {
                AlertDialog.Builder instanceInfo = new AlertDialog.Builder(this);
                instanceInfo.setTitle(R.string.about_instances_title);
                View view = getLayoutInflater().inflate(R.layout.popup_instance_info, new LinearLayout(getApplicationContext()), false);
                instanceInfo.setView(view);
                TextView infoInstancesTextview = view.findViewById(R.id.info_instances);
                infoInstancesTextview.setText(getString(R.string.about_instances, list_for_instances, list_for_instances));
                instanceInfo.setPositiveButton(R.string.close, (dialog, id) -> dialog.dismiss());
                AlertDialog alertDialog = instanceInfo.create();
                alertDialog.show();
            });

            close.setOnClickListener(v -> finish());


            instance_container.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
        });
    }

}
