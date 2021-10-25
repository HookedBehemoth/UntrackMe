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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.adapters.InstanceAdapter;
import app.fedilab.nitterizeme.databinding.ActivityPopupInstanceBinding;
import app.fedilab.nitterizeme.entities.Instance;
import app.fedilab.nitterizeme.viewmodels.SearchInstanceVM;


public class InstanceActivity extends AppCompatActivity {

    private static final String list_for_instances = "https://framagit.org/tom79/fedilab_app/-/blob/master/content/untrackme_instances/payload_2.json";
    private ActivityPopupInstanceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPopupInstanceBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        setTitle(R.string.select_instances);

        SearchInstanceVM viewModel = new ViewModelProvider(this).get(SearchInstanceVM.class);
        viewModel.getInstances().observe(this, result -> {
            if (result == null) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, R.string.error_message_internet, Snackbar.LENGTH_LONG).setAction(R.string.close, v -> finish()).show();
                return;
            }

            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
            String invidiousHost = sharedpreferences.getString(MainActivity.SET_INVIDIOUS_HOST, MainActivity.DEFAULT_INVIDIOUS_HOST);
            String nitterHost = sharedpreferences.getString(MainActivity.SET_NITTER_HOST, MainActivity.DEFAULT_NITTER_HOST);
            String bibliogramHost = sharedpreferences.getString(MainActivity.SET_BIBLIOGRAM_HOST, MainActivity.DEFAULT_BIBLIOGRAM_HOST);
            String tedditHost = sharedpreferences.getString(MainActivity.SET_TEDDIT_HOST, MainActivity.DEFAULT_TEDDIT_HOST);

            ArrayList<Instance> invidiousInstances = new ArrayList<>();
            ArrayList<Instance> nitterInstances = new ArrayList<>();
            ArrayList<Instance> bibliogramInstances = new ArrayList<>();
            ArrayList<Instance> tedditInstances = new ArrayList<>();
            boolean customInvidiousInstance = true;
            boolean customNitterInstance = true;
            boolean customBibliogramInstance = true;
            boolean customTedditInstance = true;
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
                } else if (instance.getType() == Instance.instanceType.TEDDIT) {
                    tedditInstances.add(instance);
                    if (tedditHost != null && tedditHost.trim().toLowerCase().compareTo(instance.getDomain()) == 0) {
                        customTedditInstance = false;
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
            if (customTedditInstance) {
                Instance instance = new Instance();
                instance.setChecked(true);
                instance.setDomain(tedditHost);
                instance.setLocale("--");
                tedditInstances.add(0, instance);
            }
            binding.instanceContainer.setVisibility(View.VISIBLE);
            binding.loader.setVisibility(View.GONE);


            final LinearLayoutManager iLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter invidiousAdapter = new InstanceAdapter(invidiousInstances);
            binding.invidiousInstances.setAdapter(invidiousAdapter);
            binding.invidiousInstances.setLayoutManager(iLayoutManager);
            binding.invidiousInstances.setNestedScrollingEnabled(false);

            final LinearLayoutManager nLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter nitterAdapter = new InstanceAdapter(nitterInstances);
            binding.nitterInstances.setAdapter(nitterAdapter);
            binding.nitterInstances.setLayoutManager(nLayoutManager);
            binding.nitterInstances.setNestedScrollingEnabled(false);

            final LinearLayoutManager bLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter bibliogramAdapter = new InstanceAdapter(bibliogramInstances);
            binding.bibliogramInstances.setAdapter(bibliogramAdapter);
            binding.bibliogramInstances.setLayoutManager(bLayoutManager);
            binding.bibliogramInstances.setNestedScrollingEnabled(false);


            final LinearLayoutManager tLayoutManager = new LinearLayoutManager(this);
            InstanceAdapter tedditAdapter = new InstanceAdapter(tedditInstances);
            binding.tedditInstances.setAdapter(tedditAdapter);
            binding.tedditInstances.setLayoutManager(tLayoutManager);
            binding.tedditInstances.setNestedScrollingEnabled(false);


            binding.latencyTest.setOnClickListener(v -> {
                        invidiousAdapter.evalLatency();
                        nitterAdapter.evalLatency();
                        bibliogramAdapter.evalLatency();
                tedditAdapter.evalLatency();
                    }
            );


            binding.instanceInfo.setOnClickListener(v -> {
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

            binding.close.setOnClickListener(v -> finish());


        });
    }

}
