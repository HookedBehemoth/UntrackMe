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

import static app.fedilab.nitterizeme.helpers.Utils.KILL_ACTIVITY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.databinding.ActivityMainBinding;
import app.fedilab.nitterizeme.databinding.ContentMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFS = "app_prefs";
    public static final String SET_NITTER_HOST = "set_nitter_host";
    public static final String SET_INVIDIOUS_HOST = "set_invidious_host";
    public static final String SET_TEDDIT_HOST = "set_teddit_host";
    public static final String SET_OSM_HOST = "set_osm_host";
    public static final String SET_BIBLIOGRAM_HOST = "set_bibliogram_host";
    public static final String SET_SCRIBERIP_HOST = "set_scriberip_host";
    public static final String SET_WIKILESS_HOST = "set_wikiless_host";
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static String TAG = "UntrackMe";
    public static String DEFAULT_NITTER_HOST = "nitter.net";
    public static String DEFAULT_INVIDIOUS_HOST = "invidious.snopyta.org";
    public static String SET_INVIDIOUS_ENABLED = "set_invidious_enabled";
    public static String SET_TEDDIT_ENABLED = "set_teddit_enabled";
    public static String SET_NITTER_ENABLED = "set_nitter_enabled";
    public static String SET_SCRIBERIP_ENABLED = "set_scriberip_enabled";
    public static String SET_WIKILESS_ENABLED = "set_wikiless_enabled";
    public static String SET_OSM_ENABLED = "set_osm_enabled";
    public static String DEFAULT_OSM_HOST = "www.openstreetmap.org";
    public static String SET_BIBLIOGRAM_ENABLED = "set_bibliogram_enabled";
    public static String DEFAULT_BIBLIOGRAM_HOST = "bibliogram.art";
    public static String DEFAULT_SCRIBERIP_HOST = "scribe.rip";
    public static String DEFAULT_WIKILESS_HOST = "wikiless.org";
    public static String DEFAULT_TEDDIT_HOST = "teddit.net";
    public static String SET_GEO_URIS = "set_geo_uris";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.compareTo(KILL_ACTIVITY) == 0) {
                finish();
            }
        }
    };
    private String nitterHost;
    private String invidiousHost;
    private String bibliogramHost;
    private String tedditHost;
    private String scriberipHost;
    private String wikilessHost;
    private String osmHost;
    private ContentMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = activityMainBinding.getRoot();
        setContentView(viewRoot);
        binding = activityMainBinding.contentMain;
        setSupportActionBar(activityMainBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedpreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);


        boolean nitter_enabled = sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
        boolean invidious_enabled = sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
        boolean osm_enabled = sharedpreferences.getBoolean(SET_OSM_ENABLED, true);
        boolean bibliogram_enabled = sharedpreferences.getBoolean(SET_BIBLIOGRAM_ENABLED, true);
        boolean teddit_enabled = sharedpreferences.getBoolean(SET_TEDDIT_ENABLED, true);
        boolean scriberip_enabled = sharedpreferences.getBoolean(SET_SCRIBERIP_ENABLED, true);
        boolean wikiless_enabled = sharedpreferences.getBoolean(SET_WIKILESS_ENABLED, true);
        boolean geouri_enabled = sharedpreferences.getBoolean(SET_GEO_URIS, false);


        binding.enableNitter.setChecked(nitter_enabled);
        binding.enableInvidious.setChecked(invidious_enabled);
        binding.enableBibliogram.setChecked(bibliogram_enabled);
        binding.enableTeddit.setChecked(teddit_enabled);
        binding.enableScriberip.setChecked(scriberip_enabled);
        binding.enableWikiless.setChecked(wikiless_enabled);
        binding.enableOsm.setChecked(osm_enabled);


        nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
        invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
        bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
        tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
        scriberipHost = sharedpreferences.getString(SET_SCRIBERIP_HOST, null);
        wikilessHost = sharedpreferences.getString(SET_WIKILESS_HOST, null);
        osmHost = sharedpreferences.getString(SET_OSM_HOST, null);

        binding.groupCurrentInvidious.setVisibility(invidious_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentNitter.setVisibility(nitter_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentBibliogram.setVisibility(bibliogram_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentTeddit.setVisibility(bibliogram_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentScriberip.setVisibility(scriberip_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentWikiless.setVisibility(wikiless_enabled ? View.VISIBLE : View.GONE);
        binding.groupCurrentOsm.setVisibility((osm_enabled && geouri_enabled) ? View.VISIBLE : View.GONE);
        binding.enableGeoUris.setVisibility(osm_enabled ? View.VISIBLE : View.GONE);

        binding.enableInvidious.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_INVIDIOUS_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentInvidious.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomInvidious.setVisibility(View.GONE);
            binding.buttonExpandInstanceInvidious.setRotation(0);
        });
        binding.enableNitter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_NITTER_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentNitter.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomNitter.setVisibility(View.GONE);
            binding.buttonExpandInstanceNitter.setRotation(0);
        });
        binding.enableBibliogram.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_BIBLIOGRAM_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentBibliogram.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomBibliogram.setVisibility(View.GONE);
            binding.buttonExpandInstanceBibliogram.setRotation(0);
        });
        binding.enableTeddit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_TEDDIT_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentTeddit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomTeddit.setVisibility(View.GONE);
            binding.buttonExpandInstanceTeddit.setRotation(0);
        });
        binding.enableScriberip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_SCRIBERIP_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentScriberip.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomScriberip.setVisibility(View.GONE);
            binding.buttonExpandInstanceScriberip.setRotation(0);
        });
        binding.enableWikiless.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_WIKILESS_ENABLED, isChecked);
            editor.apply();
            binding.groupCurrentWikiless.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.groupCustomWikiless.setVisibility(View.GONE);
            binding.buttonExpandInstanceWikiless.setRotation(0);
        });
        binding.enableOsm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_OSM_ENABLED, isChecked);
            editor.apply();
            binding.groupCustomOsm.setVisibility(View.GONE);
            binding.enableGeoUris.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            binding.buttonExpandInstanceOsm.setRotation(0);
            boolean geo = sharedpreferences.getBoolean(SET_GEO_URIS, false);
            if (isChecked) {
                if (geo) {
                    binding.groupCurrentOsm.setVisibility(View.GONE);
                    binding.groupCustomOsm.setVisibility(View.GONE);
                } else {
                    binding.groupCurrentOsm.setVisibility(View.VISIBLE);
                }
            } else {
                binding.groupCurrentOsm.setVisibility(View.GONE);
            }
        });


        binding.buttonExpandInstanceNitter.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomNitter.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceNitter.setRotation(0f);
                binding.groupCustomNitter.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceNitter.setRotation(180f);
                binding.groupCustomNitter.setVisibility(View.VISIBLE);
            }

            if (nitterHost != null) {
                binding.nitterInstance.setText(nitterHost);
            } else {
                binding.nitterInstance.setText("");
            }
        });
        binding.buttonExpandInstanceInvidious.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomInvidious.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceInvidious.setRotation(0f);
                binding.groupCustomInvidious.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceInvidious.setRotation(180f);
                binding.groupCustomInvidious.setVisibility(View.VISIBLE);
            }

            if (invidiousHost != null) {
                binding.invidiousInstance.setText(invidiousHost);
            } else {
                binding.invidiousInstance.setText("");
            }
        });
        binding.buttonExpandInstanceBibliogram.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomBibliogram.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceBibliogram.setRotation(0f);
                binding.groupCustomBibliogram.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceBibliogram.setRotation(180f);
                binding.groupCustomBibliogram.setVisibility(View.VISIBLE);
            }

            if (bibliogramHost != null) {
                binding.bibliogramInstance.setText(bibliogramHost);
            } else {
                binding.bibliogramInstance.setText("");
            }
        });
        binding.buttonExpandInstanceTeddit.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomTeddit.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceTeddit.setRotation(0f);
                binding.groupCustomTeddit.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceTeddit.setRotation(180f);
                binding.groupCustomTeddit.setVisibility(View.VISIBLE);
            }

            if (tedditHost != null) {
                binding.tedditInstance.setText(tedditHost);
            } else {
                binding.tedditInstance.setText("");
            }
        });
        binding.buttonExpandInstanceScriberip.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomScriberip.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceScriberip.setRotation(0f);
                binding.groupCustomScriberip.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceScriberip.setRotation(180f);
                binding.groupCustomScriberip.setVisibility(View.VISIBLE);
            }

            if (scriberipHost != null) {
                binding.scriberipInstance.setText(scriberipHost);
            } else {
                binding.scriberipInstance.setText("");
            }
        });
        binding.buttonExpandInstanceWikiless.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomWikiless.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceWikiless.setRotation(0f);
                binding.groupCustomWikiless.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceWikiless.setRotation(180f);
                binding.groupCustomWikiless.setVisibility(View.VISIBLE);
            }
            if (wikilessHost != null) {
                binding.wikilessInstance.setText(wikilessHost);
            } else {
                binding.wikilessInstance.setText("");
            }
        });
        binding.buttonExpandInstanceOsm.setOnClickListener(v -> {
            boolean custom_instance_visibility = binding.groupCustomOsm.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                binding.buttonExpandInstanceOsm.setRotation(0f);
                binding.groupCustomOsm.setVisibility(View.GONE);
            } else {
                binding.buttonExpandInstanceOsm.setRotation(180f);
                binding.groupCustomOsm.setVisibility(View.VISIBLE);
            }

            if (osmHost != null) {
                binding.osmInstance.setText(osmHost);
            } else {
                binding.osmInstance.setText("");
            }
        });


        if (nitterHost != null) {
            binding.nitterInstance.setText(nitterHost);
            binding.currentInstanceNitter.setText(nitterHost);
        } else {
            binding.currentInstanceNitter.setText(DEFAULT_NITTER_HOST);
        }
        if (invidiousHost != null) {
            binding.invidiousInstance.setText(invidiousHost);
            binding.currentInstanceInvidious.setText(invidiousHost);
        } else {
            binding.currentInstanceInvidious.setText(DEFAULT_INVIDIOUS_HOST);
        }
        if (bibliogramHost != null) {
            binding.bibliogramInstance.setText(bibliogramHost);
            binding.currentInstanceBibliogram.setText(bibliogramHost);
        } else {
            binding.currentInstanceBibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
        }
        if (tedditHost != null) {
            binding.tedditInstance.setText(tedditHost);
            binding.currentInstanceTeddit.setText(tedditHost);
        } else {
            binding.currentInstanceTeddit.setText(DEFAULT_TEDDIT_HOST);
        }
        if (scriberipHost != null) {
            binding.scriberipInstance.setText(scriberipHost);
            binding.currentInstanceScriberip.setText(scriberipHost);
        } else {
            binding.currentInstanceScriberip.setText(DEFAULT_SCRIBERIP_HOST);
        }
        if (wikilessHost != null) {
            binding.wikilessInstance.setText(wikilessHost);
            binding.currentInstanceWikiless.setText(wikilessHost);
        } else {
            binding.currentInstanceWikiless.setText(DEFAULT_WIKILESS_HOST);
        }
        if (osmHost != null) {
            binding.osmInstance.setText(osmHost);
            binding.currentInstanceOsm.setText(osmHost);
        } else {
            binding.currentInstanceOsm.setText(DEFAULT_OSM_HOST);
        }
        binding.enableGeoUris.setChecked(geouri_enabled);
        if (geouri_enabled) {
            binding.groupCurrentOsm.setVisibility(View.GONE);
            binding.groupCustomOsm.setVisibility(View.GONE);
        } else if (osm_enabled) {
            binding.groupCurrentOsm.setVisibility(View.VISIBLE);
        } else {
            binding.groupCustomOsm.setVisibility(View.GONE);
        }

        binding.buttonSaveInstanceNitter.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.nitterInstance.getText() != null && binding.nitterInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.nitterInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_NITTER_HOST, custom_instance);
                binding.currentInstanceNitter.setText(custom_instance);
            } else {
                editor.putString(SET_NITTER_HOST, null);
                binding.currentInstanceNitter.setText(DEFAULT_NITTER_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceInvidious.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.invidiousInstance.getText() != null && binding.invidiousInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.invidiousInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_INVIDIOUS_HOST, custom_instance);
                binding.currentInstanceInvidious.setText(custom_instance);
            } else {
                editor.putString(SET_INVIDIOUS_HOST, null);
                binding.currentInstanceInvidious.setText(DEFAULT_INVIDIOUS_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceBibliogram.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.bibliogramInstance.getText() != null && binding.bibliogramInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.bibliogramInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_BIBLIOGRAM_HOST, custom_instance);
                binding.currentInstanceBibliogram.setText(custom_instance);
            } else {
                editor.putString(SET_BIBLIOGRAM_HOST, null);
                binding.currentInstanceBibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceTeddit.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.tedditInstance.getText() != null && binding.tedditInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.tedditInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_TEDDIT_HOST, custom_instance);
                binding.currentInstanceTeddit.setText(custom_instance);
            } else {
                editor.putString(SET_TEDDIT_HOST, null);
                binding.currentInstanceTeddit.setText(DEFAULT_TEDDIT_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceScriberip.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.scriberipInstance.getText() != null && binding.scriberipInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.scriberipInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_SCRIBERIP_HOST, custom_instance);
                binding.currentInstanceScriberip.setText(custom_instance);
            } else {
                editor.putString(SET_SCRIBERIP_HOST, null);
                binding.currentInstanceScriberip.setText(DEFAULT_SCRIBERIP_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceWikiless.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.wikilessInstance.getText() != null && binding.wikilessInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.wikilessInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_WIKILESS_HOST, custom_instance);
                binding.currentInstanceWikiless.setText(custom_instance);
            } else {
                editor.putString(SET_WIKILESS_HOST, null);
                binding.currentInstanceWikiless.setText(DEFAULT_WIKILESS_HOST);
            }
            editor.apply();
        });
        binding.buttonSaveInstanceOsm.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (binding.osmInstance.getText() != null && binding.osmInstance.getText().toString().trim().length() > 0) {
                String custom_instance = binding.osmInstance.getText().toString().toLowerCase().trim();
                editor.putString(SET_OSM_HOST, custom_instance);
            } else {
                editor.putString(SET_OSM_HOST, null);
                binding.currentInstanceOsm.setText(DEFAULT_OSM_HOST);
            }
            editor.apply();
        });

        binding.configure.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplicationInfo().packageName, null);
            intent.setData(uri);
            startActivity(intent);
        });

        binding.buttonExpand.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckAppActivity.class);
            startActivity(intent);
        });

        binding.instances.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InstanceActivity.class);
            startActivity(intent);
        });


        binding.enableGeoUris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_GEO_URIS, isChecked);
            editor.apply();
            if (isChecked) {
                binding.buttonExpandInstanceOsm.setRotation(0f);
                binding.groupCurrentOsm.setVisibility(View.GONE);
                binding.groupCustomOsm.setVisibility(View.GONE);
                binding.osmIndications.setText(R.string.redirect_gm_to_geo_uri);
            } else {
                binding.groupCurrentOsm.setVisibility(View.VISIBLE);
                binding.osmIndications.setText(R.string.redirect_gm_to_osm);
            }
        });

        sharedpreferences.registerOnSharedPreferenceChangeListener(
                (sharedPreferences, key) -> {
                    switch (key) {
                        case SET_NITTER_HOST:
                            nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
                            binding.groupCustomNitter.setVisibility(View.GONE);
                            if (nitterHost != null && nitterHost.trim().length() > 0)
                                binding.currentInstanceNitter.setText(nitterHost);
                            else
                                binding.currentInstanceNitter.setText(DEFAULT_NITTER_HOST);
                            binding.buttonExpandInstanceNitter.setRotation(0f);
                            break;
                        case SET_INVIDIOUS_HOST:
                            invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
                            binding.groupCustomInvidious.setVisibility(View.GONE);
                            if (invidiousHost != null && invidiousHost.trim().length() > 0)
                                binding.currentInstanceInvidious.setText(invidiousHost);
                            else
                                binding.currentInstanceInvidious.setText(DEFAULT_INVIDIOUS_HOST);
                            binding.buttonExpandInstanceInvidious.setRotation(0f);
                            break;
                        case SET_BIBLIOGRAM_HOST:
                            bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
                            binding.groupCustomBibliogram.setVisibility(View.GONE);
                            if (bibliogramHost != null && bibliogramHost.trim().length() > 0)
                                binding.currentInstanceBibliogram.setText(bibliogramHost);
                            else
                                binding.currentInstanceBibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
                            binding.buttonExpandInstanceBibliogram.setRotation(0f);
                            break;
                        case SET_TEDDIT_HOST:
                            tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
                            binding.groupCustomTeddit.setVisibility(View.GONE);
                            if (tedditHost != null && tedditHost.trim().length() > 0)
                                binding.currentInstanceTeddit.setText(tedditHost);
                            else
                                binding.currentInstanceTeddit.setText(DEFAULT_TEDDIT_HOST);
                            binding.buttonExpandInstanceTeddit.setRotation(0f);
                            break;
                        case SET_SCRIBERIP_HOST:
                            scriberipHost = sharedpreferences.getString(SET_SCRIBERIP_HOST, null);
                            binding.groupCustomScriberip.setVisibility(View.GONE);
                            if (scriberipHost != null && scriberipHost.trim().length() > 0)
                                binding.currentInstanceScriberip.setText(scriberipHost);
                            else
                                binding.currentInstanceScriberip.setText(DEFAULT_SCRIBERIP_HOST);
                            binding.buttonExpandInstanceScriberip.setRotation(0f);
                            break;
                        case SET_WIKILESS_HOST:
                            wikilessHost = sharedpreferences.getString(SET_WIKILESS_HOST, null);
                            binding.groupCustomWikiless.setVisibility(View.GONE);
                            if (wikilessHost != null && wikilessHost.trim().length() > 0)
                                binding.currentInstanceWikiless.setText(wikilessHost);
                            else
                                binding.currentInstanceWikiless.setText(DEFAULT_WIKILESS_HOST);
                            binding.buttonExpandInstanceWikiless.setRotation(0f);
                            break;
                        case SET_OSM_HOST:
                            osmHost = sharedpreferences.getString(SET_OSM_HOST, null);
                            binding.groupCustomOsm.setVisibility(View.GONE);
                            if (osmHost != null && osmHost.trim().length() > 0)
                                binding.currentInstanceOsm.setText(osmHost);
                            else
                                binding.currentInstanceOsm.setText(DEFAULT_OSM_HOST);
                            binding.buttonExpandInstanceOsm.setRotation(0f);
                            break;
                    }

                    if (key.equals(SET_NITTER_HOST) || key.equals(SET_INVIDIOUS_HOST) || key.equals(SET_BIBLIOGRAM_HOST) || key.equals(SET_TEDDIT_HOST) || key.equals(SET_OSM_HOST)) {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, R.string.instances_saved, Snackbar.LENGTH_LONG).show();
                    }

                }
        );

        //Invidious custom settings
        binding.invidiousSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InvidiousSettingsActivity.class);
            startActivity(intent);
        });

        registerReceiver(broadcastReceiver, new IntentFilter(KILL_ACTIVITY));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!BuildConfig.fullLinks) {
            menu.findItem(R.id.action_settings).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, DefaultAppActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
        String invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
        String bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
        String tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
        String scriberipHost = sharedpreferences.getString(SET_SCRIBERIP_HOST, null);
        String wikilessHost = sharedpreferences.getString(SET_WIKILESS_HOST, null);
        if (nitterHost != null) {
            binding.nitterInstance.setText(nitterHost);
            binding.currentInstanceNitter.setText(nitterHost);
        }
        if (invidiousHost != null) {
            binding.invidiousInstance.setText(invidiousHost);
            binding.currentInstanceInvidious.setText(invidiousHost);
        }
        if (bibliogramHost != null) {
            binding.bibliogramInstance.setText(bibliogramHost);
            binding.currentInstanceBibliogram.setText(bibliogramHost);
        }
        if (tedditHost != null) {
            binding.tedditInstance.setText(tedditHost);
            binding.currentInstanceTeddit.setText(tedditHost);
        }
        if (scriberipHost != null) {
            binding.scriberipInstance.setText(scriberipHost);
            binding.currentInstanceScriberip.setText(scriberipHost);
        }
        if (wikilessHost != null) {
            binding.wikilessInstance.setText(wikilessHost);
            binding.currentInstanceWikiless.setText(wikilessHost);
        }
        if (BuildConfig.fullLinks) {
            List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_VIEW, Uri.parse("https://fedilab.app")), PackageManager.MATCH_DEFAULT_ONLY);
            String thisPackageName = getApplicationContext().getPackageName();
            if (resolveInfos.size() == 1 && resolveInfos.get(0).activityInfo.packageName.compareTo(thisPackageName) == 0) {
                binding.displayIndications.setVisibility(View.VISIBLE);
            } else {
                binding.displayIndications.setVisibility(View.GONE);
            }
        } else {
            binding.displayIndications.setVisibility(View.GONE);
        }
    }
}
