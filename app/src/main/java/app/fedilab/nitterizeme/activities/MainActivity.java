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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.R;

import static app.fedilab.nitterizeme.helpers.Utils.KILL_ACTIVITY;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFS = "app_prefs";
    public static final String SET_NITTER_HOST = "set_nitter_host";
    public static final String SET_INVIDIOUS_HOST = "set_invidious_host";
    public static final String SET_TEDDIT_HOST = "set_teddit_host";
    public static final String SET_OSM_HOST = "set_osm_host";
    public static final String SET_BIBLIOGRAM_HOST = "set_bibliogram_host";
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static String TAG = "UntrackMe";
    public static String DEFAULT_NITTER_HOST = "nitter.net";
    public static String DEFAULT_INVIDIOUS_HOST = "invidious.snopyta.org";
    public static String SET_INVIDIOUS_ENABLED = "set_invidious_enabled";
    public static String SET_TEDDIT_ENABLED = "set_teddit_enabled";
    public static String SET_NITTER_ENABLED = "set_nitter_enabled";
    public static String SET_OSM_ENABLED = "set_osm_enabled";
    public static String DEFAULT_OSM_HOST = "www.openstreetmap.org";
    public static String SET_BIBLIOGRAM_ENABLED = "set_bibliogram_enabled";
    public static String DEFAULT_BIBLIOGRAM_HOST = "bibliogram.art";
    public static String DEFAULT_TEDDIT_HOST = "teddit.net";
    public static String SET_GEO_URIS = "set_geo_uris";
    public static String SET_EMBEDDED_PLAYER = "set_embedded_player";
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
    private String osmHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedpreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);

        TextView current_instance_nitter = findViewById(R.id.current_instance_nitter);
        TextView current_instance_invidious = findViewById(R.id.current_instance_invidious);
        TextView current_instance_bibliogram = findViewById(R.id.current_instance_bibliogram);
        TextView current_instance_teddit = findViewById(R.id.current_instance_teddit);
        TextView current_instance_osm = findViewById(R.id.current_instance_osm);


        TextInputEditText nitter_instance = findViewById(R.id.nitter_instance);
        TextInputEditText invidious_instance = findViewById(R.id.invidious_instance);
        TextInputEditText bibliogram_instance = findViewById(R.id.bibliogram_instance);
        TextInputEditText teddit_instance = findViewById(R.id.teddit_instance);
        TextInputEditText osm_instance = findViewById(R.id.osm_instance);


        Group invidious_current_group = findViewById(R.id.group_current_invidious);
        Group nitter_current_group = findViewById(R.id.group_current_nitter);
        Group bibliogram_current_group = findViewById(R.id.group_current_bibliogram);
        Group teddit_current_group = findViewById(R.id.group_current_teddit);
        Group osm_current_group = findViewById(R.id.group_current_osm);


        Group invidious_custom_group = findViewById(R.id.group_custom_invidious);
        Group nitter_custom_group = findViewById(R.id.group_custom_nitter);
        Group bibliogram_custom_group = findViewById(R.id.group_custom_bibliogram);
        Group teddit_custom_group = findViewById(R.id.group_custom_teddit);
        Group osm_custom_group = findViewById(R.id.group_custom_osm);


        SwitchCompat enable_nitter = findViewById(R.id.enable_nitter);
        SwitchCompat enable_invidious = findViewById(R.id.enable_invidious);
        SwitchCompat enable_bibliogram = findViewById(R.id.enable_bibliogram);
        SwitchCompat enable_teddit = findViewById(R.id.enable_teddit);
        SwitchCompat enable_osm = findViewById(R.id.enable_osm);


        ImageButton expand_instance_nitter = findViewById(R.id.button_expand_instance_nitter);
        ImageButton expand_instance_invidious = findViewById(R.id.button_expand_instance_invidious);
        ImageButton expand_instance_bibliogram = findViewById(R.id.button_expand_instance_bibliogram);
        ImageButton expand_instance_teddit = findViewById(R.id.button_expand_instance_teddit);
        ImageButton expand_instance_osm = findViewById(R.id.button_expand_instance_osm);


        boolean nitter_enabled = sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
        boolean invidious_enabled = sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
        boolean osm_enabled = sharedpreferences.getBoolean(SET_OSM_ENABLED, true);
        boolean bibliogram_enabled = sharedpreferences.getBoolean(SET_BIBLIOGRAM_ENABLED, true);
        boolean teddit_enabled = sharedpreferences.getBoolean(SET_TEDDIT_ENABLED, true);
        boolean geouri_enabled = sharedpreferences.getBoolean(SET_GEO_URIS, false);
        boolean embedded_player = sharedpreferences.getBoolean(SET_EMBEDDED_PLAYER, false);

        enable_nitter.setChecked(nitter_enabled);
        enable_invidious.setChecked(invidious_enabled);
        enable_bibliogram.setChecked(bibliogram_enabled);
        enable_teddit.setChecked(teddit_enabled);
        enable_osm.setChecked(osm_enabled);
        ImageButton save_instance_nitter = findViewById(R.id.button_save_instance_nitter);
        ImageButton save_instance_invidious = findViewById(R.id.button_save_instance_invidious);
        ImageButton save_instance_bibliogram = findViewById(R.id.button_save_instance_bibliogram);
        ImageButton save_instance_teddit = findViewById(R.id.button_save_instance_teddit);
        ImageButton save_instance_osm = findViewById(R.id.button_save_instance_osm);

        CheckBox enable_geo_uris = findViewById(R.id.enable_geo_uris);
        CheckBox enable_embed_player = findViewById(R.id.enable_embed_player);


        nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
        invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
        bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
        tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
        osmHost = sharedpreferences.getString(SET_OSM_HOST, null);

        invidious_current_group.setVisibility(invidious_enabled ? View.VISIBLE : View.GONE);
        nitter_current_group.setVisibility(nitter_enabled ? View.VISIBLE : View.GONE);
        bibliogram_current_group.setVisibility(bibliogram_enabled ? View.VISIBLE : View.GONE);
        teddit_current_group.setVisibility(bibliogram_enabled ? View.VISIBLE : View.GONE);
        osm_current_group.setVisibility((osm_enabled && geouri_enabled) ? View.VISIBLE : View.GONE);
        enable_geo_uris.setVisibility(osm_enabled ? View.VISIBLE : View.GONE);
        enable_embed_player.setVisibility(invidious_enabled ? View.VISIBLE : View.GONE);

        enable_invidious.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_INVIDIOUS_ENABLED, isChecked);
            editor.apply();
            invidious_current_group.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            invidious_custom_group.setVisibility(View.GONE);
            enable_embed_player.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            expand_instance_invidious.setRotation(0);
        });
        enable_nitter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_NITTER_ENABLED, isChecked);
            editor.apply();
            nitter_current_group.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            nitter_custom_group.setVisibility(View.GONE);
            expand_instance_nitter.setRotation(0);
        });
        enable_bibliogram.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_BIBLIOGRAM_ENABLED, isChecked);
            editor.apply();
            bibliogram_current_group.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            bibliogram_custom_group.setVisibility(View.GONE);
            expand_instance_bibliogram.setRotation(0);
        });
        enable_teddit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_TEDDIT_ENABLED, isChecked);
            editor.apply();
            teddit_current_group.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            teddit_custom_group.setVisibility(View.GONE);
            expand_instance_teddit.setRotation(0);
        });
        enable_osm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_OSM_ENABLED, isChecked);
            editor.apply();
            osm_custom_group.setVisibility(View.GONE);
            enable_geo_uris.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            expand_instance_osm.setRotation(0);
            boolean geo = sharedpreferences.getBoolean(SET_GEO_URIS, false);
            if (isChecked) {
                if (geo) {
                    osm_current_group.setVisibility(View.GONE);
                    osm_custom_group.setVisibility(View.GONE);
                } else {
                    osm_current_group.setVisibility(View.VISIBLE);
                }
            } else {
                osm_current_group.setVisibility(View.GONE);
            }
        });


        expand_instance_nitter.setOnClickListener(v -> {
            boolean custom_instance_visibility = nitter_custom_group.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                expand_instance_nitter.setRotation(0f);
                nitter_custom_group.setVisibility(View.GONE);
            } else {
                expand_instance_nitter.setRotation(180f);
                nitter_custom_group.setVisibility(View.VISIBLE);
            }

            if (nitterHost != null) {
                nitter_instance.setText(nitterHost);
            } else {
                nitter_instance.setText("");
            }
        });
        expand_instance_invidious.setOnClickListener(v -> {
            boolean custom_instance_visibility = invidious_custom_group.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                expand_instance_invidious.setRotation(0f);
                invidious_custom_group.setVisibility(View.GONE);
            } else {
                expand_instance_invidious.setRotation(180f);
                invidious_custom_group.setVisibility(View.VISIBLE);
            }

            if (invidiousHost != null) {
                invidious_instance.setText(invidiousHost);
            } else {
                invidious_instance.setText("");
            }
        });
        expand_instance_bibliogram.setOnClickListener(v -> {
            boolean custom_instance_visibility = bibliogram_custom_group.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                expand_instance_bibliogram.setRotation(0f);
                bibliogram_custom_group.setVisibility(View.GONE);
            } else {
                expand_instance_bibliogram.setRotation(180f);
                bibliogram_custom_group.setVisibility(View.VISIBLE);
            }

            if (bibliogramHost != null) {
                bibliogram_instance.setText(bibliogramHost);
            } else {
                bibliogram_instance.setText("");
            }
        });
        expand_instance_teddit.setOnClickListener(v -> {
            boolean custom_instance_visibility = teddit_custom_group.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                expand_instance_teddit.setRotation(0f);
                teddit_custom_group.setVisibility(View.GONE);
            } else {
                expand_instance_teddit.setRotation(180f);
                teddit_custom_group.setVisibility(View.VISIBLE);
            }

            if (tedditHost != null) {
                teddit_instance.setText(tedditHost);
            } else {
                teddit_instance.setText("");
            }
        });
        expand_instance_osm.setOnClickListener(v -> {
            boolean custom_instance_visibility = osm_custom_group.getVisibility() == View.VISIBLE;
            if (custom_instance_visibility) {
                expand_instance_osm.setRotation(0f);
                osm_custom_group.setVisibility(View.GONE);
            } else {
                expand_instance_osm.setRotation(180f);
                osm_custom_group.setVisibility(View.VISIBLE);
            }

            if (osmHost != null) {
                osm_instance.setText(osmHost);
            } else {
                osm_instance.setText("");
            }
        });


        if (nitterHost != null) {
            nitter_instance.setText(nitterHost);
            current_instance_nitter.setText(nitterHost);
        } else {
            current_instance_nitter.setText(DEFAULT_NITTER_HOST);
        }
        if (invidiousHost != null) {
            invidious_instance.setText(invidiousHost);
            current_instance_invidious.setText(invidiousHost);
        } else {
            current_instance_invidious.setText(DEFAULT_INVIDIOUS_HOST);
        }
        if (bibliogramHost != null) {
            bibliogram_instance.setText(bibliogramHost);
            current_instance_bibliogram.setText(bibliogramHost);
        } else {
            current_instance_bibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
        }
        if (tedditHost != null) {
            teddit_instance.setText(tedditHost);
            current_instance_teddit.setText(tedditHost);
        } else {
            current_instance_teddit.setText(DEFAULT_TEDDIT_HOST);
        }
        if (osmHost != null) {
            osm_instance.setText(osmHost);
            current_instance_osm.setText(osmHost);
        } else {
            current_instance_osm.setText(DEFAULT_OSM_HOST);
        }
        enable_geo_uris.setChecked(geouri_enabled);
        if (geouri_enabled) {
            osm_current_group.setVisibility(View.GONE);
            osm_custom_group.setVisibility(View.GONE);
        } else if (osm_enabled) {
            osm_current_group.setVisibility(View.VISIBLE);
        } else {
            osm_custom_group.setVisibility(View.GONE);
        }

        enable_embed_player.setChecked(embedded_player);
        save_instance_nitter.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (nitter_instance.getText() != null && nitter_instance.getText().toString().trim().length() > 0) {
                String custom_instance = nitter_instance.getText().toString().toLowerCase().trim();
                editor.putString(SET_NITTER_HOST, custom_instance);
                current_instance_nitter.setText(custom_instance);
            } else {
                editor.putString(SET_NITTER_HOST, null);
                current_instance_nitter.setText(DEFAULT_NITTER_HOST);
            }
            editor.apply();
        });
        save_instance_invidious.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (invidious_instance.getText() != null && invidious_instance.getText().toString().trim().length() > 0) {
                String custom_instance = invidious_instance.getText().toString().toLowerCase().trim();
                editor.putString(SET_INVIDIOUS_HOST, custom_instance);
                current_instance_invidious.setText(custom_instance);
            } else {
                editor.putString(SET_INVIDIOUS_HOST, null);
                current_instance_invidious.setText(DEFAULT_INVIDIOUS_HOST);
            }
            editor.apply();
        });
        save_instance_bibliogram.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (bibliogram_instance.getText() != null && bibliogram_instance.getText().toString().trim().length() > 0) {
                String custom_instance = bibliogram_instance.getText().toString().toLowerCase().trim();
                editor.putString(SET_BIBLIOGRAM_HOST, custom_instance);
                current_instance_bibliogram.setText(custom_instance);
            } else {
                editor.putString(SET_BIBLIOGRAM_HOST, null);
                current_instance_bibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
            }
            editor.apply();
        });
        save_instance_teddit.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (teddit_instance.getText() != null && teddit_instance.getText().toString().trim().length() > 0) {
                String custom_instance = teddit_instance.getText().toString().toLowerCase().trim();
                editor.putString(SET_TEDDIT_HOST, custom_instance);
                current_instance_teddit.setText(custom_instance);
            } else {
                editor.putString(SET_TEDDIT_HOST, null);
                current_instance_teddit.setText(DEFAULT_TEDDIT_HOST);
            }
            editor.apply();
        });
        save_instance_osm.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (osm_instance.getText() != null && osm_instance.getText().toString().trim().length() > 0) {
                String custom_instance = osm_instance.getText().toString().toLowerCase().trim();
                editor.putString(SET_OSM_HOST, custom_instance);
            } else {
                editor.putString(SET_OSM_HOST, null);
                current_instance_osm.setText(DEFAULT_OSM_HOST);
            }
            editor.apply();
        });

        Button configure = findViewById(R.id.configure);
        configure.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplicationInfo().packageName, null);
            intent.setData(uri);
            startActivity(intent);
        });

        ImageButton buttonExpand = findViewById(R.id.button_expand);
        buttonExpand.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckAppActivity.class);
            startActivity(intent);
        });

        ImageButton buttonPing = findViewById(R.id.instances);
        buttonPing.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InstanceActivity.class);
            startActivity(intent);
        });


        enable_geo_uris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_GEO_URIS, isChecked);
            editor.apply();
            TextView osm_indications = findViewById(R.id.osm_indications);
            if (isChecked) {
                expand_instance_osm.setRotation(0f);
                osm_current_group.setVisibility(View.GONE);
                osm_custom_group.setVisibility(View.GONE);
                osm_indications.setText(R.string.redirect_gm_to_geo_uri);
            } else {
                osm_current_group.setVisibility(View.VISIBLE);
                osm_indications.setText(R.string.redirect_gm_to_osm);
            }
        });
        enable_embed_player.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(SET_EMBEDDED_PLAYER, isChecked);
            editor.apply();
        });

        sharedpreferences.registerOnSharedPreferenceChangeListener(
                (sharedPreferences, key) -> {
                    switch (key) {
                        case SET_NITTER_HOST:
                            nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
                            nitter_custom_group.setVisibility(View.GONE);
                            if (nitterHost != null && nitterHost.trim().length() > 0)
                                current_instance_nitter.setText(nitterHost);
                            else
                                current_instance_nitter.setText(DEFAULT_NITTER_HOST);
                            expand_instance_nitter.setRotation(0f);
                            break;
                        case SET_INVIDIOUS_HOST:
                            invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
                            invidious_custom_group.setVisibility(View.GONE);
                            if (invidiousHost != null && invidiousHost.trim().length() > 0)
                                current_instance_invidious.setText(invidiousHost);
                            else
                                current_instance_invidious.setText(DEFAULT_INVIDIOUS_HOST);
                            expand_instance_invidious.setRotation(0f);
                            break;
                        case SET_BIBLIOGRAM_HOST:
                            bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
                            bibliogram_custom_group.setVisibility(View.GONE);
                            if (bibliogramHost != null && bibliogramHost.trim().length() > 0)
                                current_instance_bibliogram.setText(bibliogramHost);
                            else
                                current_instance_bibliogram.setText(DEFAULT_BIBLIOGRAM_HOST);
                            expand_instance_bibliogram.setRotation(0f);
                            break;
                        case SET_TEDDIT_HOST:
                            tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
                            teddit_custom_group.setVisibility(View.GONE);
                            if (tedditHost != null && tedditHost.trim().length() > 0)
                                current_instance_teddit.setText(tedditHost);
                            else
                                current_instance_teddit.setText(DEFAULT_TEDDIT_HOST);
                            expand_instance_teddit.setRotation(0f);
                            break;
                        case SET_OSM_HOST:
                            osmHost = sharedpreferences.getString(SET_OSM_HOST, null);
                            osm_custom_group.setVisibility(View.GONE);
                            if (osmHost != null && osmHost.trim().length() > 0)
                                current_instance_osm.setText(osmHost);
                            else
                                current_instance_osm.setText(DEFAULT_OSM_HOST);
                            expand_instance_osm.setRotation(0f);
                            break;
                    }

                    if (key.equals(SET_NITTER_HOST) || key.equals(SET_INVIDIOUS_HOST) || key.equals(SET_BIBLIOGRAM_HOST) || key.equals(SET_TEDDIT_HOST) || key.equals(SET_OSM_HOST)) {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, R.string.instances_saved, Snackbar.LENGTH_LONG).show();
                    }

                }
        );

        //Invidious custom settings
        ImageButton invidious_settings = findViewById(R.id.invidious_settings);
        invidious_settings.setOnClickListener(v -> {
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
        TextInputEditText nitter_instance = findViewById(R.id.nitter_instance);
        TextInputEditText invidious_instance = findViewById(R.id.invidious_instance);
        TextInputEditText bibliogram_instance = findViewById(R.id.bibliogram_instance);
        TextInputEditText teddit_instance = findViewById(R.id.teddit_instance);

        TextView current_instance_nitter = findViewById(R.id.current_instance_nitter);
        TextView current_instance_invidious = findViewById(R.id.current_instance_invidious);
        TextView current_instance_bibliogram = findViewById(R.id.current_instance_bibliogram);
        TextView current_instance_teddit = findViewById(R.id.current_instance_teddit);

        SharedPreferences sharedpreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String nitterHost = sharedpreferences.getString(SET_NITTER_HOST, null);
        String invidiousHost = sharedpreferences.getString(SET_INVIDIOUS_HOST, null);
        String bibliogramHost = sharedpreferences.getString(SET_BIBLIOGRAM_HOST, null);
        String tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, null);
        if (nitterHost != null) {
            nitter_instance.setText(nitterHost);
            current_instance_nitter.setText(nitterHost);
        }
        if (invidiousHost != null) {
            invidious_instance.setText(invidiousHost);
            current_instance_invidious.setText(invidiousHost);
        }
        if (bibliogramHost != null) {
            bibliogram_instance.setText(bibliogramHost);
            current_instance_bibliogram.setText(bibliogramHost);
        }
        if (tedditHost != null) {
            teddit_instance.setText(tedditHost);
            current_instance_teddit.setText(tedditHost);
        }
        ConstraintLayout display_indications = findViewById(R.id.display_indications);
        if (BuildConfig.fullLinks) {
            List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_VIEW, Uri.parse("https://fedilab.app")), PackageManager.MATCH_DEFAULT_ONLY);
            String thisPackageName = getApplicationContext().getPackageName();
            if (resolveInfos.size() == 1 && resolveInfos.get(0).activityInfo.packageName.compareTo(thisPackageName) == 0) {
                display_indications.setVisibility(View.VISIBLE);
            } else {
                display_indications.setVisibility(View.GONE);
            }
        } else {
            display_indications.setVisibility(View.GONE);
        }
    }
}
