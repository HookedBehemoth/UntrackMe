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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.helpers.Utils;

import static app.fedilab.nitterizeme.activities.CheckAppActivity.shortener_domains;
import static app.fedilab.nitterizeme.helpers.Utils.KILL_ACTIVITY;
import static app.fedilab.nitterizeme.helpers.Utils.forwardToBrowser;
import static app.fedilab.nitterizeme.helpers.Utils.isRouted;
import static app.fedilab.nitterizeme.helpers.Utils.manageShortened;
import static app.fedilab.nitterizeme.helpers.Utils.manageShortenedShare;
import static app.fedilab.nitterizeme.helpers.Utils.remove_tracking_param;
import static app.fedilab.nitterizeme.helpers.Utils.routerEnabledForHost;
import static app.fedilab.nitterizeme.helpers.Utils.transformUrl;


public class TransformActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("nitterizeme") != null) {
            finish();
            return;
        }
        Intent stopMainActivity = new Intent(KILL_ACTIVITY);
        sendBroadcast(stopMainActivity);
        assert intent != null;
        //Dealing with URLs
        if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_VIEW)) {
            String url = Objects.requireNonNull(intent.getData()).toString();
            url = remove_tracking_param(url);
            URL url_;
            String host = null;
            try {
                url_ = new URL(url);
                host = url_.getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //Shortened URLs
            if (Arrays.asList(shortener_domains).contains(host)) {
                manageShortened(TransformActivity.this, url);
            } else if (isRouted(url)) {
                boolean routeEnabled = routerEnabledForHost(TransformActivity.this, url);
                if (routeEnabled) {
                    Intent delegate = new Intent(Intent.ACTION_VIEW);
                    String transformedURL = transformUrl(TransformActivity.this, url);
                    if (transformedURL != null) {
                        delegate.setData(Uri.parse(transformedURL));
                        delegate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (BuildConfig.fullLinks) {
                            forwardToBrowser(TransformActivity.this, delegate);
                        } else {
                            if (delegate.resolveActivity(getPackageManager()) != null) {
                                startActivity(delegate);
                                finish();
                            }
                        }
                    } else {
                        forwardToBrowser(TransformActivity.this, intent);
                    }
                } else {
                    forwardToBrowser(TransformActivity.this, intent);
                }
            } else {
                String newUrl = remove_tracking_param(url);
                try {
                    url_ = new URL(newUrl);
                    host = url_.getHost();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //Shortened URLs
                if (Arrays.asList(shortener_domains).contains(host)) {
                    manageShortened(TransformActivity.this, newUrl);
                } else {
                    intent.setData(Uri.parse(newUrl));
                    forwardToBrowser(TransformActivity.this, intent);
                }
            }

        }
        //It's a sharing intent
        else if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_SEND)) {
            share(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Transform URL inside the shared content without modifying the whole content
     *
     * @param extraText String the new extra text
     */
    private void share(String extraText) {

        String url = null;
        if (extraText != null) {
            Matcher matcher;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                matcher = Patterns.WEB_URL.matcher(extraText);
            else
                matcher = Utils.urlPattern.matcher(extraText);
            while (matcher.find()) {
                int matchStart = matcher.start(1);
                int matchEnd = matcher.end();
                if (matchStart < matchEnd && extraText.length() >= matchEnd) {
                    url = extraText.substring(matchStart, matchEnd);
                }
            }
        }
        String newUrl;
        if (url == null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, extraText);
            sendIntent.setType("text/plain");
            forwardToBrowser(TransformActivity.this, sendIntent);
            return;
        }
        url = Utils.remove_tracking_param(url);
        URL url_;
        String host = null;
        try {
            url_ = new URL(url);
            host = url_.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (Arrays.asList(shortener_domains).contains(host)) {
            Uri url_r = Uri.parse(url);
            String scheme = url_r.getScheme();
            if (scheme == null) {
                scheme = "https://";
            } else {
                scheme += "://";
            }
            manageShortenedShare(TransformActivity.this, url, extraText, scheme);
            return;
        }
        newUrl = transformUrl(TransformActivity.this, url);
        if (newUrl != null) {
            extraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(newUrl));
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        sendIntent.setType("text/plain");
        if (BuildConfig.fullLinks) {
            forwardToBrowser(TransformActivity.this, sendIntent);
        } else {
            startActivity(sendIntent);
            finish();
        }
    }

}
