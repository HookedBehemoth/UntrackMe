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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import app.fedilab.nitterizeme.BuildConfig;
import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.databinding.ActivityAboutBinding;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            binding.aboutVersion.setText(getResources().getString(R.string.about_vesrion, version));
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        setTitle(R.string.about_the_app);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //Developer click for Mastodon account

        SpannableString content = new SpannableString(binding.developerMastodon.getText().toString());
        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(AboutActivity.this, R.color.colorAccent)), 0, content.length(), 0);
        binding.developerMastodon.setText(content);
        binding.developerMastodon.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://toot.fedilab.app/@apps"));
            startActivity(browserIntent);
        });

        //App Name:

        if (BuildConfig.fullLinks) {
            binding.appName.setText(R.string.app_name);
        } else {
            binding.appName.setText(R.string.app_name_lite);
        }

        //Developer Github
        content = new SpannableString(binding.github.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.github.setText(content);
        binding.github.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/stom79"));
            startActivity(browserIntent);
        });

        //Developer Framagit
        content = new SpannableString(binding.framagit.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.framagit.setText(content);
        binding.framagit.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://framagit.org/tom79"));
            startActivity(browserIntent);
        });

        //Developer Codeberg
        content = new SpannableString(binding.codeberg.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.codeberg.setText(content);
        binding.codeberg.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://codeberg.org/tom79"));
            startActivity(browserIntent);
        });

        //Developer donation
        binding.donatePaypal.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/Mastalab"));
            startActivity(browserIntent);
        });
        binding.donateLiberapay.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liberapay.com/tom79"));
            startActivity(browserIntent);
        });

        binding.howTo.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fedilab.app/wiki/untrackme/"));
            startActivity(browserIntent);
        });

        content = new SpannableString(binding.license.getText().toString());
        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(AboutActivity.this, R.color.colorAccent)), 0, content.length(), 0);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.license.setText(content);
        binding.license.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gnu.org/licenses/quick-guide-gplv3.fr.html"));
            startActivity(browserIntent);
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
