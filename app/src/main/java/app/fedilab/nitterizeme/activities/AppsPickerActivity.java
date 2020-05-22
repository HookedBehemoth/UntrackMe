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

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.adapters.AppPickerAdapter;
import app.fedilab.nitterizeme.entities.AppPicker;
import app.fedilab.nitterizeme.helpers.Utils;
import app.fedilab.nitterizeme.sqlite.DefaultAppDAO;
import app.fedilab.nitterizeme.sqlite.Sqlite;

import static app.fedilab.nitterizeme.helpers.Utils.INTENT_ACTION;
import static app.fedilab.nitterizeme.helpers.Utils.KILL_ACTIVITY;
import static app.fedilab.nitterizeme.helpers.Utils.URL_APP_PICKER;


public class AppsPickerActivity extends Activity {


    private String url;
    private String action;
    private String appToUse;
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_app);
        if (getIntent() == null) {
            finish();
        }
        Bundle b = getIntent().getExtras();
        if (b == null) {
            finish();
        }
        if (b != null) {
            url = b.getString(URL_APP_PICKER, null);
            action = b.getString(INTENT_ACTION, null);
        }

        if (url == null || action == null) {
            finish();
        }
        //At this point we are sure that url is not null
        Intent stopMainActivity = new Intent(KILL_ACTIVITY);
        sendBroadcast(stopMainActivity);

        Intent delegate = new Intent(action);
        delegate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (action.compareTo(Intent.ACTION_VIEW) == 0) {
            delegate.setData(Uri.parse(url));
        } else {
            delegate.putExtra(Intent.EXTRA_TEXT, url);
            delegate.setType("text/plain");
        }
        List<ResolveInfo> activities;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activities = getPackageManager().queryIntentActivities(
                    delegate, PackageManager.MATCH_ALL);
        } else {
            activities = getPackageManager().queryIntentActivities(
                    delegate, 0);
        }


        SQLiteDatabase db = Sqlite.getInstance(getApplicationContext(), Sqlite.DB_NAME, null, Sqlite.DB_VERSION).open();
        RelativeLayout blank = findViewById(R.id.blank);
        blank.setOnClickListener(v -> finish());
        String thisPackageName = getApplicationContext().getPackageName();
        ArrayList<String> packages = new ArrayList<>();
        List<AppPicker> appPickers = new ArrayList<>();
        int i = 0;
        for (ResolveInfo currentInfo : activities) {
            String packageName = currentInfo.activityInfo.packageName;
            if (!thisPackageName.equals(packageName) && !packages.contains(packageName)) {
                AppPicker appPicker = new AppPicker();
                appPicker.setIcon(currentInfo.activityInfo.loadIcon(getPackageManager()));
                appPicker.setName(String.valueOf(currentInfo.loadLabel(getPackageManager())));
                appPicker.setPackageName(packageName);
                if (i == 0) {
                    appPicker.setSelected(true);
                    appToUse = packageName;
                    appName = String.valueOf(currentInfo.loadLabel(getPackageManager()));
                }
                appPickers.add(appPicker);
                packages.add(packageName);
                i++;
            }

        }
        String defaultApp = new DefaultAppDAO(AppsPickerActivity.this, db).getDefault(packages);

        TextView urlText = findViewById(R.id.url);
        urlText.setText(url);

        if (defaultApp != null) {
            Intent intent = new Intent(action, Uri.parse(url));
            intent.setPackage(defaultApp);
            startActivity(intent);
            finish();
        } else {
            ConstraintLayout app_container = findViewById(R.id.app_container);
            app_container.setVisibility(View.VISIBLE);
            GridView gridView = findViewById(R.id.app_list);
            AppPickerAdapter appPickerAdapter = new AppPickerAdapter(appPickers);
            gridView.setAdapter(appPickerAdapter);
            gridView.setNumColumns(3);
            gridView.setOnItemClickListener((parent, view1, position, id) -> {
                if (!appPickers.get(position).isSelected()) {
                    for (AppPicker ap : appPickers) {
                        ap.setSelected(false);
                    }
                    appPickers.get(position).setSelected(true);
                    appToUse = appPickers.get(position).getPackageName();
                    appName = appPickers.get(position).getName();
                    appPickerAdapter.notifyDataSetChanged();
                } else {
                    if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                        Intent intent = new Intent(action, Uri.parse(url));
                        intent.setPackage(appToUse);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(action);
                        intent.putExtra(Intent.EXTRA_TEXT, url);
                        intent.setType("text/plain");
                        intent.setPackage(appToUse);
                        startActivity(intent);
                    }
                    finish();
                }

            });


            Button always = findViewById(R.id.always);
            Button once = findViewById(R.id.once);

            always.setOnClickListener(v -> {

                boolean isPresent = new DefaultAppDAO(AppsPickerActivity.this, db).isPresent(appToUse);
                long val = -1;
                if (isPresent) {
                    ArrayList<String> oldConcurrent = new DefaultAppDAO(AppsPickerActivity.this, db).getConcurrent(appToUse);
                    ArrayList<String> newConcurrent = Utils.union(oldConcurrent, packages);
                    new DefaultAppDAO(AppsPickerActivity.this, db).update(appToUse, newConcurrent);
                } else {
                    val = new DefaultAppDAO(AppsPickerActivity.this, db).insert(appToUse, packages);
                }
                if (val > 0) {
                    Toast.makeText(AppsPickerActivity.this, getString(R.string.default_app_indication, appName), Toast.LENGTH_LONG).show();
                }
                if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                    Intent intent = new Intent(action, Uri.parse(url));
                    intent.setPackage(appToUse);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(action);
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    intent.setType("text/plain");
                    intent.setPackage(appToUse);
                    startActivity(intent);
                }
                finish();
            });

            once.setOnClickListener(v -> {
                if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                    Intent intent = new Intent(action, Uri.parse(url));
                    intent.setPackage(appToUse);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(action);
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    intent.setType("text/plain");
                    intent.setPackage(appToUse);
                    startActivity(intent);
                }
                finish();
            });
        }

        ImageView copyLink = findViewById(R.id.copy_link);
        copyLink.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("", url);
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(this, getString(R.string.copy_done), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
