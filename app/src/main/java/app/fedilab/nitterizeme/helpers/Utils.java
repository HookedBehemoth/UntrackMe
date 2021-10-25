package app.fedilab.nitterizeme.helpers;
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
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.activities.AppsPickerActivity;
import app.fedilab.nitterizeme.activities.MainActivity;
import app.fedilab.nitterizeme.activities.WebviewPlayerActivity;

import static android.content.Context.DOWNLOAD_SERVICE;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.bibliogram_instances;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.instagram_domains;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.invidious_instances;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.medium_domains;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.nitter_instances;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.outlook_safe_domain;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.reddit_domains;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.shortener_domains;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.twitter_domains;
import static app.fedilab.nitterizeme.activities.CheckAppActivity.youtube_domains;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_BIBLIOGRAM_ENABLED;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_EMBEDDED_PLAYER;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_INVIDIOUS_ENABLED;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_NITTER_ENABLED;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_SCRIBERIP_ENABLED;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_TEDDIT_ENABLED;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_TEDDIT_HOST;

public class Utils {

    public static final String KILL_ACTIVITY = "kill_activity";
    public static final String URL_APP_PICKER = "url_app_picker";
    public static final String INTENT_ACTION = "intent_action";
    public static final String LAST_USED_APP_PACKAGE = "last_used_app_package";
    public static final Pattern youtubePattern = Pattern.compile("(www\\.|m\\.)?(youtube\\.com|youtu\\.be|youtube-nocookie\\.com)/(((?!([\"'<])).)*)");
    public static final Pattern redditPattern = Pattern.compile("(www\\.|m\\.)?(reddit\\.com|preview\\.redd\\.it|i\\.redd\\.it)/(((?!([\"'<])).)*)");
    public static final Pattern nitterPattern = Pattern.compile("(mobile\\.|www\\.)?twitter.com([\\w-/]+)");
    public static final Pattern bibliogramPostPattern = Pattern.compile("(m\\.|www\\.)?instagram.com(/p/[\\w-/]+)");
    public static final Pattern scriberipPattern = Pattern.compile("(www\\.)?medium.com/([\\w-/@]+)");

    public static final Pattern bibliogramAccountPattern = Pattern.compile("(m\\.|www\\.)?instagram.com(((?!/p/).)+)");
    public static final Pattern maps = Pattern.compile("/maps/place/([^@]+@)?([\\d.,z]+).*");
    public static final Pattern ampExtract = Pattern.compile("amp/s/(.*)");
    public static final String RECEIVE_STREAMING_URL = "receive_streaming_url";
    public static final Pattern outlookRedirect = Pattern.compile("(.*)safelinks\\.protection\\.outlook\\.com/?[?]?((?!url).)*url=([^&]+)");
    private static final Pattern extractPlace = Pattern.compile("/maps/place/(((?!/data).)*)");
    private static final Pattern googleRedirect = Pattern.compile("https?://(www\\.)?google(\\.\\w{2,})?(\\.\\w{2,})/url\\?(q=|q%3D)(.*)");
    private static final String[] G_TRACKING = {
            "sourceid",
            "aqs",
            "client",
            "source",
            "ust",
            "usg"
    };

    private static final String[] UTM_PARAMS = {
            "utm_\\w+",
            "ga_source",
            "ga_medium",
            "ga_term",
            "ga_content",
            "ga_campaign",
            "ga_place",
            "yclid",
            "_openstat",
            "fb_action_ids",
            "fb_action_types",
            "fb_source",
            "fb_ref",
            "fbclid",
            "action_object_map",
            "action_type_map",
            "action_ref_map",
            "gs_l",
            "mkt_tok",
            "hmb_campaign",
            "hmb_medium",
            "hmb_source",
            "[\\?|&]ref[\\_]?",
            "amp[_#\\w]+",
            "click"
    };


    private static final String urlRegex = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,10}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))";
    public static final Pattern urlPattern = Pattern.compile(
            urlRegex,
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Returns the unshortened URL
     *
     * @param urls ArrayList<String> URL to check
     */
    private static void checkUrl(Context context, ArrayList<String> urls) {
        URL url;
        String newURL = null;
        String comingURl;
        try {
            comingURl = urls.get(urls.size() - 1);

            url = new URL(comingURl);
            if (comingURl.startsWith("https")) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestProperty("http.keepAlive", "false");
                httpsURLConnection.setInstanceFollowRedirects(false);
                httpsURLConnection.setRequestMethod("HEAD");
                if (httpsURLConnection.getResponseCode() == 301 || httpsURLConnection.getResponseCode() == 302) {
                    Map<String, List<String>> map = httpsURLConnection.getHeaderFields();
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        if (entry.toString().toLowerCase().startsWith("location")) {
                            Matcher matcher = urlPattern.matcher(entry.toString());
                            if (matcher.find()) {
                                newURL = remove_tracking_param(context, matcher.group(1));
                                urls.add(transformUrl(context, newURL));
                            }
                        }
                    }
                }
                httpsURLConnection.getInputStream().close();
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("http.keepAlive", "false");
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setRequestMethod("HEAD");
                if (httpURLConnection.getResponseCode() == 301) {
                    Map<String, List<String>> map = httpURLConnection.getHeaderFields();
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        if (entry.toString().toLowerCase().startsWith("location")) {
                            Matcher matcher = urlPattern.matcher(entry.toString());
                            if (matcher.find()) {
                                newURL = remove_tracking_param(context, matcher.group(1));
                                urls.add(transformUrl(context, newURL));
                            }
                        }
                    }
                }
                httpURLConnection.getInputStream().close();
            }
            if (newURL != null && newURL.compareTo(comingURl) != 0) {
                URL redirectURL = new URL(newURL);
                String host = redirectURL.getHost();
                String protocol = redirectURL.getProtocol();
                if (protocol != null && host != null) {
                    if (Arrays.asList(shortener_domains).contains(host)) {
                        checkUrl(context, urls);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Transform the URL to a Nitter, Invidious or OSM ones
     *
     * @param url String original URL
     * @return String transformed URL
     */
    public static String transformUrl(Context context, String url) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
        String newUrl = null;
        URL url_;
        String host = null;
        url = Utils.remove_tracking_param(context, url);
        try {
            url_ = new URL(url);
            host = url_.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Uri url_r = Uri.parse(url);
        String scheme = url_r.getScheme();
        if (scheme == null) {
            scheme = "https://";
        } else {
            scheme += "://";
        }
        if (Arrays.asList(twitter_domains).contains(host)) {
            boolean nitter_enabled = sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
            if (nitter_enabled) {
                String nitterHost = sharedpreferences.getString(MainActivity.SET_NITTER_HOST, MainActivity.DEFAULT_NITTER_HOST);
                assert nitterHost != null;
                nitterHost = nitterHost.toLowerCase();
                if (nitterHost.startsWith("http")) {
                    scheme = "";
                }
                assert host != null;
                if (host.compareTo("pbs.twimg.com") == 0 || host.compareTo("pic.twitter.com") == 0) {
                    try {
                        newUrl = scheme + nitterHost + "/pic/" + URLEncoder.encode(url, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        newUrl = scheme + nitterHost + "/pic/" + url;
                    }
                } else if (url.contains("/search?")) {
                    newUrl = url.replace(host, nitterHost);
                } else {
                    Matcher matcher = nitterPattern.matcher(url);
                    while (matcher.find()) {
                        final String nitter_directory = matcher.group(2);
                        newUrl = scheme + nitterHost + nitter_directory;
                    }
                }
                if (newUrl != null && (newUrl.endsWith("tweets") || newUrl.endsWith("tweets/"))) {
                    newUrl = newUrl.replaceAll("/tweets/?", "");
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (Arrays.asList(instagram_domains).contains(host)) {
            boolean bibliogram_enabled = sharedpreferences.getBoolean(SET_BIBLIOGRAM_ENABLED, true);
            if (bibliogram_enabled) {
                String bibliogramHost = sharedpreferences.getString(MainActivity.SET_BIBLIOGRAM_HOST, MainActivity.DEFAULT_BIBLIOGRAM_HOST);
                assert bibliogramHost != null;
                bibliogramHost = bibliogramHost.toLowerCase();
                if (bibliogramHost.startsWith("http")) {
                    scheme = "";
                }
                Matcher matcher = bibliogramPostPattern.matcher(url);
                while (matcher.find()) {
                    final String bibliogram_directory = matcher.group(2);

                    newUrl = scheme + bibliogramHost + bibliogram_directory;
                }
                matcher = bibliogramAccountPattern.matcher(url);
                while (matcher.find()) {
                    final String bibliogram_directory = matcher.group(2);
                    if (bibliogram_directory != null && bibliogram_directory.compareTo("privacy") != 0 && !bibliogram_directory.startsWith("/tv/") && !bibliogram_directory.startsWith("/reel/") && !bibliogram_directory.startsWith("/igtv/")) {
                        newUrl = scheme + bibliogramHost + "/u" + bibliogram_directory;
                    } else {
                        newUrl = scheme + bibliogramHost + bibliogram_directory;
                    }
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (Arrays.asList(medium_domains).contains(host)) {
            boolean scriberip_enabled = sharedpreferences.getBoolean(MainActivity.SET_SCRIBERIP_ENABLED, true);
            if (scriberip_enabled) {
                String scriberipHost = sharedpreferences.getString(MainActivity.SET_SCRIBERIP_HOST, MainActivity.DEFAULT_SCRIBERIP_HOST);
                assert scriberipHost != null;
                scriberipHost = scriberipHost.toLowerCase();
                if (scriberipHost.startsWith("http")) {
                    scheme = "";
                }
                Matcher matcher = scriberipPattern.matcher(url);
                while (matcher.find()) {
                    final String scriberip_directory = matcher.group(2);
                    newUrl = scheme + scriberipHost + "/" + scriberip_directory;
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (url.contains("/maps/place")) {
            boolean osm_enabled = sharedpreferences.getBoolean(MainActivity.SET_OSM_ENABLED, true);
            if (osm_enabled) {
                Matcher matcher = maps.matcher(url);
                while (matcher.find()) {
                    final String localization = matcher.group(2);
                    assert localization != null;
                    String[] data = localization.split(",");
                    if (data.length >= 2) {
                        String zoom;
                        if (data.length > 2) {
                            String[] details = data[2].split("\\.");
                            if (details.length > 0) {
                                zoom = details[0];
                            } else {
                                zoom = data[2];
                            }
                        } else {
                            zoom = "16";
                        }
                        String osmHost = sharedpreferences.getString(MainActivity.SET_OSM_HOST, MainActivity.DEFAULT_OSM_HOST);
                        assert osmHost != null;
                        osmHost = osmHost.toLowerCase();
                        boolean geo_uri_enabled = sharedpreferences.getBoolean(MainActivity.SET_GEO_URIS, false);
                        if (!geo_uri_enabled) {
                            newUrl = scheme + osmHost + "/#map=" + zoom + "/" + data[0] + "/" + data[1];
                        } else {
                            newUrl = "geo:0,0?q=" + data[0] + "," + data[1] + ",z=" + zoom;
                        }
                    }
                }
                if (newUrl == null && url.contains("/data=")) {
                    matcher = extractPlace.matcher(url);
                    while (matcher.find()) {
                        final String search = matcher.group(1);
                        newUrl = "geo:0,0?q=" + search;
                    }
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (url.contains("/amp/s/")) {
            Matcher matcher = ampExtract.matcher(url);
            String transformedURL = url;
            while (matcher.find()) {
                transformedURL = "https://" + matcher.group(1);
            }
            return transformedURL;
        } else if (Arrays.asList(youtube_domains).contains(host)) { //Youtube URL
            boolean invidious_enabled = sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
            if (invidious_enabled) {
                String invidiousHost = sharedpreferences.getString(MainActivity.SET_INVIDIOUS_HOST, MainActivity.DEFAULT_INVIDIOUS_HOST);
                assert invidiousHost != null;
                invidiousHost = invidiousHost.toLowerCase();
                if (invidiousHost.startsWith("http")) {
                    scheme = "";
                }
                Matcher matcher = youtubePattern.matcher(url);
                while (matcher.find()) {
                    String youtubeId = matcher.group(3);

                    if (Objects.requireNonNull(matcher.group(2)).compareTo("youtu.be") == 0) {
                        if (youtubeId != null && youtubeId.contains("?t=")) {
                            youtubeId = youtubeId.replace("?t=", "&t=");
                        }
                        newUrl = scheme + invidiousHost + "/watch?v=" + youtubeId;
                    } else {
                        newUrl = scheme + invidiousHost + "/" + youtubeId;
                    }
                    newUrl = replaceInvidiousParams(context, newUrl);
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (Arrays.asList(reddit_domains).contains(host)) { //Reddit URL
            boolean teddit_enabled = sharedpreferences.getBoolean(SET_TEDDIT_ENABLED, true);
            if (teddit_enabled) {
                String tedditHost = sharedpreferences.getString(SET_TEDDIT_HOST, MainActivity.DEFAULT_TEDDIT_HOST);
                assert tedditHost != null;
                tedditHost = tedditHost.toLowerCase();
                if (tedditHost.startsWith("http")) {
                    scheme = "";
                }
                Matcher matcher = redditPattern.matcher(url);
                while (matcher.find()) {
                    String redditPath = matcher.group(3);
                    if (Objects.requireNonNull(matcher.group(2)).compareTo("preview.redd.it") == 0 ||
                            Objects.requireNonNull(matcher.group(2)).compareTo("i.redd.it") == 0
                    ) {
                        newUrl = scheme + tedditHost + "/pics/w:null_" + (redditPath != null ? redditPath.split("\\?|%26")[0] : "null");
                    } else {
                        newUrl = scheme + tedditHost + "/" + redditPath;
                    }
                }
                return newUrl;
            } else {
                return url;
            }
        } else if (Arrays.asList(invidious_instances).contains(host)) {
            boolean invidious_enabled = sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
            newUrl = url;
            if (invidious_enabled) {
                String invidiousHost = sharedpreferences.getString(MainActivity.SET_INVIDIOUS_HOST, MainActivity.DEFAULT_INVIDIOUS_HOST);
                assert invidiousHost != null;
                invidiousHost = invidiousHost.toLowerCase();
                if (host != null && host.compareTo(invidiousHost) != 0) {
                    if (!invidiousHost.startsWith("http")) {
                        newUrl = url.replace(host, invidiousHost);
                    } else {
                        newUrl = url.replace("https://" + host, invidiousHost).replace("http://" + host, invidiousHost);
                    }
                }
                newUrl = Utils.replaceInvidiousParams(context, newUrl);
            }
            return newUrl;
        }
        //Transform a Nitter URL from an instance to another one selected by the end user.
        else if (Arrays.asList(nitter_instances).contains(host)) {
            newUrl = url;
            boolean nitter_enabled = sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
            if (nitter_enabled) {
                String nitterHost = sharedpreferences.getString(MainActivity.SET_NITTER_HOST, MainActivity.DEFAULT_NITTER_HOST);
                assert nitterHost != null;
                nitterHost = nitterHost.toLowerCase();
                if (host != null && host.compareTo(nitterHost) != 0) {
                    if (!nitterHost.startsWith("http")) {
                        newUrl = url.replace(host, nitterHost);
                    } else {
                        newUrl = url.replace("https://" + host, nitterHost).replace("http://" + host, nitterHost);
                    }
                }
            }
            return newUrl;
        }
        //Transform a Bibliogram URL from an instance to another one selected by the end user.
        else if (Arrays.asList(bibliogram_instances).contains(host)) {
            newUrl = url;
            boolean bibliogram_enabled = sharedpreferences.getBoolean(SET_BIBLIOGRAM_ENABLED, true);
            if (bibliogram_enabled) {
                String bibliogramHost = sharedpreferences.getString(MainActivity.SET_BIBLIOGRAM_HOST, MainActivity.DEFAULT_BIBLIOGRAM_HOST);
                assert bibliogramHost != null;
                bibliogramHost = bibliogramHost.toLowerCase();
                if (host != null && host.compareTo(bibliogramHost) != 0) {
                    if (!bibliogramHost.startsWith("http")) {
                        newUrl = url.replace(host, bibliogramHost);
                    } else {
                        newUrl = url.replace("https://" + host, bibliogramHost).replace("http://" + host, bibliogramHost);
                    }
                }
            }
            return newUrl;
        } else if (host != null && host.contains(outlook_safe_domain)) {
            newUrl = url;
            Matcher matcher = outlookRedirect.matcher(url);
            if (matcher.find()) {
                String tmp_url = matcher.group(3);
                try {
                    newUrl = transformUrl(context, URLDecoder.decode(tmp_url, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
            return newUrl;
        }
        return url;
    }

    /**
     * Replace params with those defined in Invidious settings from the app
     *
     * @param context Context
     * @param url     String incoming URL
     * @return String transformed URL
     */
    public static String replaceInvidiousParams(Context context, String url) {
        String newUrl = url;
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Theme
        String theme = sharedpreferences.getString(context.getString(R.string.invidious_dark_mode), "0");
        assert theme != null;
        if (theme.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?dark_mode=(true|false)", "");
        } else if (theme.compareTo("0") != 0) { //Change value
            if (newUrl.contains("dark_mode=")) {
                newUrl = newUrl.replaceAll("dark_mode=(true|false)", theme);
            } else {
                newUrl += "&" + theme;
            }
        }

        //Thin mode
        String thin = sharedpreferences.getString(context.getString(R.string.invidious_thin_mode), "0");
        assert thin != null;
        if (thin.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?thin_mode=(true|false)", "");
        } else if (thin.compareTo("0") != 0) { //Change value
            if (newUrl.contains("thin_mode=")) {
                newUrl = newUrl.replaceAll("thin_mode=(true|false)", thin);
            } else {
                newUrl += "&" + thin;
            }
        }

        //Language
        String language = sharedpreferences.getString(context.getString(R.string.invidious_language_mode), "0");
        assert language != null;
        if (language.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?hl=\\w{2}(-\\w{2})?", "");
        } else if (language.compareTo("0") != 0) { //Change value
            if (newUrl.contains("hl=")) {
                newUrl = newUrl.replaceAll("hl=\\w{2}(-\\w{2})?", "hl=" + language);
            } else {
                newUrl += "&hl=" + language;
            }
        }

        //Annotations
        String annotations = sharedpreferences.getString(context.getString(R.string.invidious_annotations_mode), "0");
        assert annotations != null;
        if (annotations.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?iv_load_policy=\\d", "");
        } else if (annotations.compareTo("0") != 0) { //Change value
            if (newUrl.contains("iv_load_policy=")) {
                newUrl = newUrl.replaceAll("iv_load_policy=\\d", annotations);
            } else {
                newUrl += "&" + annotations;
            }
        }

        //Autoplay
        String autoplay = sharedpreferences.getString(context.getString(R.string.invidious_autoplay_mode), "0");
        assert autoplay != null;
        if (autoplay.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?autoplay=\\d", "");
        } else if (autoplay.compareTo("0") != 0) { //Change value
            if (newUrl.contains("autoplay=")) {
                newUrl = newUrl.replaceAll("autoplay=\\d", autoplay);
            } else {
                newUrl += "&" + autoplay;
            }
        }

        //Continue
        String continueMode = sharedpreferences.getString(context.getString(R.string.invidious_continue_mode), "0");
        assert continueMode != null;
        if (continueMode.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?continue=\\d", "");
        } else if (continueMode.compareTo("0") != 0) { //Change value
            if (newUrl.contains("continue=")) {
                newUrl = newUrl.replaceAll("continue=\\d", continueMode);
            } else {
                newUrl += "&" + continueMode;
            }
        }

        //Listen
        String listen = sharedpreferences.getString(context.getString(R.string.invidious_listen_mode), "0");
        assert listen != null;
        if (listen.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?listen=(true|false)", "");
        } else if (listen.compareTo("0") != 0) { //Change value
            if (newUrl.contains("listen=")) {
                newUrl = newUrl.replaceAll("listen=(true|false)", listen);
            } else {
                newUrl += "&" + listen;
            }
        }

        //Local
        String local = sharedpreferences.getString(context.getString(R.string.invidious_local_mode), "local=true");
        if (!url.contains("/channel/")) {
            assert local != null;
            if (local.compareTo("-1") == 0) { //Remove value
                newUrl = newUrl.replaceAll("&?local=(true|false)", "");
            } else if (local.compareTo("0") != 0) { //Change value
                if (newUrl.contains("local=")) {
                    newUrl = newUrl.replaceAll("local=(true|false)", local);
                } else {
                    newUrl += "&" + local;
                }
            }
        }

        //Subtitles
        String subtitles = sharedpreferences.getString(context.getString(R.string.invidious_subtitles_mode), "0");
        assert subtitles != null;
        if (subtitles.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?subtitles=\\w+", "");
        } else if (subtitles.compareTo("0") != 0) { //Change value
            if (newUrl.contains("subtitles=")) {
                newUrl = newUrl.replaceAll("subtitles=\\w+", "subtitles=" + subtitles);
            } else {
                newUrl += "&subtitles=" + subtitles;
            }
        }

        //Quality
        String quality = sharedpreferences.getString(context.getString(R.string.invidious_quality_mode), "0");
        assert quality != null;
        if (quality.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?quality=\\w+", "");
        } else if (quality.compareTo("0") != 0) { //Change value
            if (newUrl.contains("listen=")) {
                newUrl = newUrl.replaceAll("quality=\\w+", quality);
            } else {
                newUrl += "&" + quality;
            }
        }

        //Loop
        String loop = sharedpreferences.getString(context.getString(R.string.invidious_loop_mode), "0");
        assert loop != null;
        if (loop.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?loop=\\d", "");
        } else if (loop.compareTo("0") != 0) { //Change value
            if (newUrl.contains("loop=")) {
                newUrl = newUrl.replaceAll("loop=\\d", loop);
            } else {
                newUrl += "&" + loop;
            }
        }


        //Volume
        String volume = sharedpreferences.getString(context.getString(R.string.invidious_volume_mode), "0");
        assert volume != null;
        if (volume.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?volume=\\d{1,3}", "");
        } else if (volume.compareTo("0") != 0) { //Change value
            int volume_value = sharedpreferences.getInt(context.getString(R.string.invidious_volume_value), 60);
            if (newUrl.contains("volume=")) {
                newUrl = newUrl.replaceAll("volume=\\d{1,3}", "volume=" + volume_value);
            } else {
                newUrl += "&volume=" + volume_value;
            }
        }

        //Player style
        String player_style = sharedpreferences.getString(context.getString(R.string.invidious_player_style_mode), "0");
        assert player_style != null;
        if (player_style.compareTo("-1") == 0) { //Remove value
            newUrl = newUrl.replaceAll("&?player_style=\\w+", "");
        } else if (player_style.compareTo("0") != 0) { //Change value
            if (newUrl.contains("player_style=")) {
                newUrl = newUrl.replaceAll("player_style=\\w+", player_style);
            } else {
                newUrl += "&" + player_style;
            }
        }


        return newUrl;
    }

    /**
     * Get time for reaching a domain
     *
     * @param domain String domain name
     * @return long delay
     */
    public static long ping(String domain) {
        long timeDifference = -2;
        try {
            long beforeTime = System.currentTimeMillis();
            //noinspection ResultOfMethodCallIgnored
            InetAddress.getByName(domain).isReachable(10000);
            long afterTime = System.currentTimeMillis();
            timeDifference = afterTime - beforeTime;
        } catch (IOException ignored) {
        }
        return timeDifference;
    }


    /**
     * Remove unwanted redirects from Google - recursive removal
     *
     * @param context Context
     * @param url     String initial url
     * @return String url without Google redirects
     */
    private static String removeGoogleRedirects(Context context, String url) {
        Matcher matcher = googleRedirect.matcher(url);
        if (matcher.find()) {
            return remove_tracking_param(context, matcher.group(5));
        }
        return url;
    }

    /**
     * Clean URLs from utm parameters
     *
     * @param url String URL
     * @return cleaned URL String
     */
    public static String remove_tracking_param(Context context, String url) {

        if (url != null) {
            try {

                InputStream inputStream = context.getAssets().open("rules.json");
                BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((line = bR.readLine()) != null) {

                    responseStrBuilder.append(line);
                }
                inputStream.close();
                JSONObject result = new JSONObject(responseStrBuilder.toString());
                JSONObject providers = result.getJSONObject("providers");
                Iterator<String> iter = providers.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONObject domainValues = providers.getJSONObject(key);
                    String urlPattern = domainValues.getString("urlPattern");
                    JSONArray rules = domainValues.getJSONArray("rules");
                    JSONArray exceptions = domainValues.getJSONArray("exceptions");
                    boolean completeProvider = domainValues.getBoolean("completeProvider");
                    Pattern rulesRegex = Pattern.compile(
                            urlPattern,
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                    Matcher matcher = rulesRegex.matcher(url);
                    if (matcher.find()) {
                        //noinspection StatementWithEmptyBody
                        if (!completeProvider) {
                            for (int i = 0; i < rules.length(); i++) {
                                boolean exception = false;
                                for (int j = 0; j < exceptions.length(); j++) {
                                    Pattern exceptionRegex = Pattern.compile(
                                            exceptions.getString(j),
                                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                                    Matcher matcherException = exceptionRegex.matcher(url);
                                    if (matcherException.find()) {
                                        exception = true;
                                    }
                                }
                                if (!exception) {
                                    url = url.replaceAll(rules.getString(i), "");
                                }
                            }
                        } else {
                            //TODO see what can be done with that URL
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            for (String utm : UTM_PARAMS) {
                url = url.replaceAll("&amp;" + utm + "=[0-9a-zA-Z._-]*", "");
                url = url.replaceAll("&" + utm + "=[0-9a-zA-Z._-]*", "");
                url = url.replaceAll("\\?" + utm + "=[0-9a-zA-Z._-]*", "?");
                url = url.replaceAll("/" + utm + "=" + urlRegex, "/");
                url = url.replaceAll("#" + utm + "=" + urlRegex, "");
            }
            try {
                url = removeGoogleRedirects(context, url);
                URL redirectURL = new URL(url);
                String host = redirectURL.getHost();
                if (host != null) {
                    for (String utm : G_TRACKING) {
                        assert url != null;
                        url = url.replaceAll("&amp;" + utm + "=[0-9a-zA-Z._-]*", "");
                        url = url.replaceAll("&" + utm + "=[0-9a-zA-Z._-]*", "");
                        url = url.replaceAll("\\?" + utm + "=[0-9a-zA-Z._-]*", "?");
                        url = url.replaceAll("/" + utm + "=" + urlRegex, "/");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (url != null && url.endsWith("?")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }


    /**
     * Manage downloads with URLs
     *
     * @param context Context
     * @param url     String download url
     */
    public static void manageDownloadsNoPopup(final Context context, final String url) {

        final DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(url.trim()));
        } catch (Exception e) {
            return;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
            Date now = new Date();
            final String fileName = "UntrackMe_" + formatter.format(now) + ".mp4";
            request.allowScanningByMediaScanner();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            assert dm != null;
            dm.enqueue(request);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if an app is installed
     *
     * @return boolean
     */
    @SuppressWarnings({"SameParameterValue"})
    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Get PackageInfo for an app
     *
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception ignored) {
        }
        return packageInfo;
    }


    /**
     * Convert an ArrayList to a string using coma
     *
     * @param arrayList ArrayList<String>
     * @return String
     */
    public static String arrayToString(ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (String item : arrayList) {
            result.append(item).append(",");
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * Convert an ArrayList to a string using coma
     *
     * @param arrayList ArrayList<String>
     * @return String
     */
    public static String arrayToStringQuery(ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (String item : arrayList) {
            result.append("'").append(item).append("'").append(",");
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * Convert String items to Array
     *
     * @param items String
     * @return ArrayList<String>
     */
    public static ArrayList<String> stringToArray(String items) {
        if (items == null) {
            return null;
        }
        String[] result = items.split(",");
        return new ArrayList<>(Arrays.asList(result));
    }


    public static <T> ArrayList<T> union(ArrayList<T> list1, ArrayList<T> list2) {
        Set<T> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }


    /**
     * Manage URLs when visiting a shortened URL
     *
     * @param context Context
     * @param url     String the shortened URL
     */
    public static void manageShortened(Context context, String url) {
        final ArrayList<String> notShortnedURLDialog = new ArrayList<>();
        AlertDialog.Builder unshortenAlertBuilder = new AlertDialog.Builder(context, R.style.AppThemeDialog);
        unshortenAlertBuilder.setTitle(R.string.shortened_detected);
        unshortenAlertBuilder.setOnDismissListener(dialog -> ((Activity) context).finish());
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.popup_unshorten, new LinearLayout(context), false);
        unshortenAlertBuilder.setView(view);
        unshortenAlertBuilder.setIcon(R.mipmap.ic_launcher);
        unshortenAlertBuilder.setPositiveButton(R.string.open, (dialog, id) -> {
            if (notShortnedURLDialog.size() > 0) {
                Intent delegate = new Intent(Intent.ACTION_VIEW);
                delegate.setData(Uri.parse(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1)));
                delegate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (BuildConfig.fullLinks) {
                    forwardToBrowser(context, delegate);
                } else {
                    if (delegate.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(delegate);
                        ((Activity) context).finish();
                    }
                }
            }
            dialog.dismiss();
            ((Activity) context).finish();
        });
        unshortenAlertBuilder.setNegativeButton(R.string.dismiss, (dialog, id) -> {
            dialog.dismiss();
            ((Activity) context).finish();
        });
        AlertDialog alertDialog = unshortenAlertBuilder.create();
        alertDialog.show();
        Button positiveButton = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                notShortnedURLDialog.add(url);
                Utils.checkUrl(context, notShortnedURLDialog);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = () -> {
                    positiveButton.setEnabled(true);
                    StringBuilder message;
                    if (notShortnedURLDialog.size() <= 1) {
                        message = new StringBuilder(context.getString(R.string.the_app_failed_shortened));
                    } else {
                        message = new StringBuilder(context.getString(R.string.try_to_redirect, notShortnedURLDialog.get(0), notShortnedURLDialog.get(1)));
                        if (notShortnedURLDialog.size() > 2) {
                            for (int i = 2; i < notShortnedURLDialog.size(); i++) {
                                message.append("\n\n").append(context.getString(R.string.try_to_redirect_again, notShortnedURLDialog.get(i)));
                            }
                        }
                    }
                    TextView indications = view.findViewById(R.id.indications);
                    RelativeLayout progress = view.findViewById(R.id.progress);
                    indications.setText(message.toString());
                    indications.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                };
                mainHandler.post(myRunnable);
            }
        };
        thread.start();
    }

    /**
     * Manage URLs when trying to share a shortened URL
     *
     * @param context   Context
     * @param url       String coming URL
     * @param extraText String text when sharing content
     * @param scheme    String scheme of the URL
     */
    public static void manageShortenedShare(Context context, String url, String extraText, final String scheme) {
        ArrayList<String> notShortnedURLDialog = new ArrayList<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                notShortnedURLDialog.add(url);
                Utils.checkUrl(context, notShortnedURLDialog);

                URL url_;
                String host = null;
                try {
                    url_ = new URL(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1));
                    host = url_.getHost();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
                boolean nitter_enabled = sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
                boolean invidious_enabled = sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
                boolean osm_enabled = sharedpreferences.getBoolean(MainActivity.SET_OSM_ENABLED, true);
                boolean teddit_enabled = sharedpreferences.getBoolean(SET_TEDDIT_ENABLED, true);
                if (nitter_enabled && Arrays.asList(twitter_domains).contains(host)) {
                    Matcher matcher = nitterPattern.matcher(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1));
                    String newUrlFinal = notShortnedURLDialog.get(notShortnedURLDialog.size() - 1);
                    while (matcher.find()) {
                        final String nitter_directory = matcher.group(2);
                        String nitterHost = sharedpreferences.getString(MainActivity.SET_NITTER_HOST, MainActivity.DEFAULT_NITTER_HOST);
                        assert nitterHost != null;
                        nitterHost = nitterHost.toLowerCase();
                        newUrlFinal = scheme + nitterHost + nitter_directory;
                    }
                    String newExtraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(newUrlFinal));
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, newExtraText);
                    sendIntent.setType("text/plain");
                    forwardToBrowser(context, sendIntent);
                } else if (invidious_enabled && Arrays.asList(youtube_domains).contains(host)) {
                    Matcher matcher = youtubePattern.matcher(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1));
                    String newUrlFinal = notShortnedURLDialog.get(notShortnedURLDialog.size() - 1);
                    while (matcher.find()) {
                        final String youtubeId = matcher.group(3);
                        String invidiousHost = sharedpreferences.getString(MainActivity.SET_INVIDIOUS_HOST, MainActivity.DEFAULT_INVIDIOUS_HOST);
                        assert invidiousHost != null;
                        invidiousHost = invidiousHost.toLowerCase();
                        if (Objects.requireNonNull(matcher.group(2)).compareTo("youtu.be") == 0) {
                            newUrlFinal = scheme + invidiousHost + "/watch?v=" + youtubeId;
                        } else {
                            newUrlFinal = scheme + invidiousHost + "/" + youtubeId;
                        }
                        newUrlFinal = replaceInvidiousParams(context, newUrlFinal);
                    }
                    String newExtraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(newUrlFinal));
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, newExtraText);
                    sendIntent.setType("text/plain");
                    forwardToBrowser(context, sendIntent);
                } else if (teddit_enabled && Arrays.asList(reddit_domains).contains(host)) {
                    Matcher matcher = redditPattern.matcher(url);
                    String newUrlFinal = notShortnedURLDialog.get(notShortnedURLDialog.size() - 1);
                    while (matcher.find()) {
                        String redditPath = matcher.group(3);
                        String tedditHost = sharedpreferences.getString(MainActivity.SET_TEDDIT_HOST, MainActivity.DEFAULT_TEDDIT_HOST);
                        assert tedditHost != null;
                        tedditHost = tedditHost.toLowerCase();
                        if (Objects.requireNonNull(matcher.group(2)).compareTo("preview.redd.it") == 0 ||
                                Objects.requireNonNull(matcher.group(2)).compareTo("i.redd.it") == 0
                        ) {
                            newUrlFinal = scheme + tedditHost + "/pics/w:null_" + (redditPath != null ? redditPath.split("\\?|%26")[0] : "null");
                        } else {
                            newUrlFinal = scheme + tedditHost + "/" + redditPath;
                        }
                    }
                    String newExtraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(newUrlFinal));
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, newExtraText);
                    sendIntent.setType("text/plain");
                    forwardToBrowser(context, sendIntent);
                } else if (osm_enabled && notShortnedURLDialog.get(notShortnedURLDialog.size() - 1).contains("/maps/place/")) {
                    String newUrlFinal = notShortnedURLDialog.get(notShortnedURLDialog.size() - 1);
                    Matcher matcher = maps.matcher(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1));
                    while (matcher.find()) {
                        final String localization = matcher.group(2);
                        assert localization != null;
                        String[] data = localization.split(",");
                        if (data.length > 2) {
                            String zoom;
                            String[] details = data[2].split("\\.");
                            if (details.length > 0) {
                                zoom = details[0];
                            } else {
                                zoom = data[2];
                            }
                            String osmHost = sharedpreferences.getString(MainActivity.SET_OSM_HOST, MainActivity.DEFAULT_OSM_HOST);
                            assert osmHost != null;
                            osmHost = osmHost.toLowerCase();
                            newUrlFinal = scheme + osmHost + "/#map=" + zoom + "/" + data[0] + "/" + data[1];
                        }
                    }
                    String newExtraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(newUrlFinal));
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, newExtraText);
                    sendIntent.setType("text/plain");
                    forwardToBrowser(context, sendIntent);
                } else {
                    String newExtraText = extraText.replaceAll(Pattern.quote(url), Matcher.quoteReplacement(notShortnedURLDialog.get(notShortnedURLDialog.size() - 1)));
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, newExtraText);
                    sendIntent.setType("text/plain");
                    forwardToBrowser(context, sendIntent);
                }
            }
        };
        thread.start();
    }

    /**
     * Forward the intent to a browser
     *
     * @param i original intent
     */
    public static void forwardToBrowser(Context context, Intent i) {

        if (!BuildConfig.fullLinks) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String type = i.getType();
            intent.setDataAndType(i.getData(), type);
            List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
            ArrayList<Intent> targetIntents = new ArrayList<>();
            String thisPackageName = context.getApplicationContext().getPackageName();
            for (ResolveInfo currentInfo : activities) {
                String packageName = currentInfo.activityInfo.packageName;
                if (!thisPackageName.equals(packageName)) {
                    Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                    targetIntent.setDataAndType(intent.getData(), intent.getType());
                    targetIntent.setPackage(intent.getPackage());
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    targetIntent.setComponent(new ComponentName(packageName, currentInfo.activityInfo.name));
                    targetIntents.add(targetIntent);
                }
            }
            //NewPipe has to be manually added
            if (Utils.isAppInstalled(context, "org.schabi.newpipe") && Arrays.asList(invidious_instances).contains(Objects.requireNonNull(i.getData()).getHost())) {
                Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                targetIntent.setDataAndType(intent.getData(), intent.getType());
                targetIntent.setPackage(intent.getPackage());
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                targetIntent.setComponent(new ComponentName("org.schabi.newpipe", "org.schabi.newpipe.RouterActivity"));
                targetIntents.add(targetIntent);
            }

            SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
            boolean embedded_player = sharedpreferences.getBoolean(SET_EMBEDDED_PLAYER, false);

            if (Arrays.asList(invidious_instances).contains(Objects.requireNonNull(i.getData()).getHost()) && embedded_player) {
                if (!i.getData().toString().contains("videoplayback")) {
                    Intent intentPlayer = new Intent(context, WebviewPlayerActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intentPlayer.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    intentPlayer.putExtra("url", i.getData().toString());
                    context.startActivity(intentPlayer);
                } else {
                    Intent intentStreamingUrl = new Intent(Utils.RECEIVE_STREAMING_URL);
                    Bundle b = new Bundle();
                    b.putString("streaming_url", i.getData().toString());
                    intentStreamingUrl.putExtras(b);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentStreamingUrl);
                }
            } else if (targetIntents.size() > 0) {
                Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), context.getString(R.string.open_with));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
                context.startActivity(chooserIntent);
            }
            ((Activity) context).finish();

        } else {
            Intent app_picker = new Intent(context, AppsPickerActivity.class);
            Bundle b = new Bundle();
            if (Objects.requireNonNull(i.getAction()).compareTo(Intent.ACTION_VIEW) == 0) {
                b.putString(URL_APP_PICKER, i.getDataString());
            } else {
                b.putString(URL_APP_PICKER, i.getStringExtra(Intent.EXTRA_TEXT));
            }
            b.putString(INTENT_ACTION, i.getAction());
            app_picker.putExtras(b);
            context.startActivity(app_picker);
            ((Activity) context).finish();
        }
    }


    public static boolean isRouted(String url) {

        URL url_;
        String host = null;
        try {
            url_ = new URL(url);
            host = url_.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Arrays.asList(twitter_domains).contains(host) || Arrays.asList(nitter_instances).contains(host) || Arrays.asList(reddit_domains).contains(host)
                || Arrays.asList(instagram_domains).contains(host) || Arrays.asList(bibliogram_instances).contains(host)
                || url.contains("/maps/place") || url.contains("/amp/s/") || (host != null && host.contains(outlook_safe_domain))
                || Arrays.asList(youtube_domains).contains(host) || Arrays.asList(invidious_instances).contains(host) || Arrays.asList(medium_domains).contains(host);
    }

    public static boolean routerEnabledForHost(Context context, String url) {

        URL url_;
        String host = null;
        try {
            url_ = new URL(url);
            host = url_.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.APP_PREFS, Context.MODE_PRIVATE);
        if (Arrays.asList(twitter_domains).contains(host) || Arrays.asList(nitter_instances).contains(host)) {
            return sharedpreferences.getBoolean(SET_NITTER_ENABLED, true);
        } else if (Arrays.asList(instagram_domains).contains(host) || Arrays.asList(bibliogram_instances).contains(host)) {
            return sharedpreferences.getBoolean(SET_BIBLIOGRAM_ENABLED, true);
        } else if (url.contains("/maps/place")) {
            return sharedpreferences.getBoolean(MainActivity.SET_OSM_ENABLED, true);
        } else if (Arrays.asList(youtube_domains).contains(host) || Arrays.asList(invidious_instances).contains(host)) {
            return sharedpreferences.getBoolean(SET_INVIDIOUS_ENABLED, true);
        } else if (Arrays.asList(reddit_domains).contains(host)) {
            return sharedpreferences.getBoolean(SET_TEDDIT_ENABLED, true);
        } else if (Arrays.asList(medium_domains).contains(host)) {
            return sharedpreferences.getBoolean(SET_SCRIBERIP_ENABLED, true);
        } else
            return url.contains("/amp/s/") || (host != null && host.contains(outlook_safe_domain));
    }


}
