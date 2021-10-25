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


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.adapters.AppInfoAdapter;
import app.fedilab.nitterizeme.databinding.ActivityCheckAppBinding;
import app.fedilab.nitterizeme.entities.AppInfo;


public class CheckAppActivity extends AppCompatActivity {


    //Supported domains
    public static String[] twitter_domains = {
            "twitter.com",
            "mobile.twitter.com",
            "www.twitter.com",
            "pbs.twimg.com",
            "pic.twitter.com"
    };
    public static String[] instagram_domains = {
            "instagram.com",
            "www.instagram.com",
            "m.instagram.com",
    };
    public static String[] youtube_domains = {
            "www.youtube.com",
            "youtube.com",
            "m.youtube.com",
            "youtu.be",
            "youtube-nocookie.com"
    };

    public static String[] reddit_domains = {
            "www.reddit.com",
            "reddit.com",
            "i.reddit.com",
            "i.redd.it",
            "preview.redd.it"
    };
    public static String[] medium_domains = {
            "medium.com",
            "www.medium.com"
    };
    public static String[] shortener_domains = {
            "t.co",
            "nyti.ms",
            "bit.ly",
            "is.gd",
            "aspedrom.com",
            "bit.do",
            "fb.me",
            "rb.gy",
            "cutt.ly",
            "amp.gs",
            "tinyurl.com",
            "goo.gl",
            "nzzl.us",
            "ift.tt",
            "ow.ly",
            "bl.ink",
            "buff.ly",
            "maps.app.goo.gl"
    };
    //Supported instances to redirect one instance to another faster for the user
    public static String[] invidious_instances = {
            "invidio.us",
            "invidious.snopyta.org",
            "invidiou.sh",
            "invidious.toot.koeln",
            "invidious.ggc-project.de",
            "invidious.13ad.de",
            "yewtu.be"
    };
    public static String[] nitter_instances = {
            "nitter.net",
            "nitter.snopyta.org",
            "nitter.42l.fr",
            "nitter.13ad.de",
            "tw.openalgeria.org",
            "nitter.pussthecat.org",
            "nitter.mastodont.cat",
            "nitter.dark.fail",
            "nitter.tedomum.net"
    };
    public static String[] bibliogram_instances = {
            "bibliogram.art",
            "bibliogram.snopyta.org",
            "bibliogram.dsrev.ru",
            "bibliogram.pussthecat.org"
    };

    public static String outlook_safe_domain = "safelinks.protection.outlook.com";

    private String[] domains;
    private ActivityCheckAppBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckAppBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);


        setTitle(R.string.check_apps);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (BuildConfig.fullLinks) {
            domains = new String[twitter_domains.length + youtube_domains.length + reddit_domains.length + shortener_domains.length + instagram_domains.length + invidious_instances.length + nitter_instances.length + bibliogram_instances.length];
        } else {
            domains = new String[twitter_domains.length + youtube_domains.length + reddit_domains.length + shortener_domains.length + instagram_domains.length];
        }
        int i = 0;
        for (String host : twitter_domains) {
            domains[i] = host;
            i++;
        }
        for (String host : youtube_domains) {
            domains[i] = host;
            i++;
        }
        for (String host : reddit_domains) {
            domains[i] = host;
            i++;
        }
        for (String host : instagram_domains) {
            domains[i] = host;
            i++;
        }
        for (String host : shortener_domains) {
            domains[i] = host;
            i++;
        }
        if (BuildConfig.fullLinks) {
            for (String host : invidious_instances) {
                domains[i] = host;
                i++;
            }
            for (String host : nitter_instances) {
                domains[i] = host;
                i++;
            }
            for (String host : bibliogram_instances) {
                domains[i] = host;
                i++;
            }
        }

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(CheckAppActivity.this);
        binding.listApps.setLayoutManager(mLayoutManager);
        binding.listApps.setNestedScrollingEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        int position = ((LinearLayoutManager) Objects.requireNonNull(binding.listApps.getLayoutManager()))
                .findFirstVisibleItemPosition();
        ArrayList<AppInfo> appInfos = getAppInfo();
        AppInfoAdapter appInfoAdapter = new AppInfoAdapter(appInfos);
        binding.listApps.setAdapter(appInfoAdapter);
        binding.listApps.scrollToPosition(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Allow to get info about application that opens the link by default
     *
     * @param url String url for test
     * @return ApplicationInfo info about the application
     */
    ApplicationInfo getDefaultApp(String url) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        browserIntent.setData(Uri.parse(url));
        final ResolveInfo defaultResolution = getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (defaultResolution != null) {
            final ActivityInfo activity = defaultResolution.activityInfo;
            if (!activity.name.equals("com.android.internal.app.ResolverActivity") && !activity.packageName.equals("com.huawei.android.internal.app")) {
                return activity.applicationInfo;
            }
        }
        return null;
    }


    private ArrayList<AppInfo> getAppInfo() {
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        for (String domain : domains) {
            if (Arrays.asList(twitter_domains).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle("Twitter");
                appInfos.add(appInfo);
            } else if (Arrays.asList(youtube_domains).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle("YouTube");
                appInfos.add(appInfo);
            } else if (Arrays.asList(instagram_domains).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle("Instagram");
                appInfos.add(appInfo);
            } else if (Arrays.asList(reddit_domains).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle("Reddit");
                appInfos.add(appInfo);
            } else if (Arrays.asList(shortener_domains).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle(getString(R.string.shortener_services));
                appInfos.add(appInfo);
            } else if (Arrays.asList(invidious_instances).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle(getString(R.string.invidious_instances));
                appInfos.add(appInfo);
            } else if (Arrays.asList(nitter_instances).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle(getString(R.string.nitter_instances));
                appInfos.add(appInfo);
            } else if (Arrays.asList(bibliogram_instances).contains(domain)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setTitle(getString(R.string.bibliogram_instances));
                appInfos.add(appInfo);
            }
            AppInfo appInfo = new AppInfo();
            appInfo.setDomain(domain);
            appInfo.setApplicationInfo(getDefaultApp("https://" + domain + "/"));
            appInfos.add(appInfo);
        }
        return appInfos;
    }

}
