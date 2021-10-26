package app.fedilab.nitterizeme.adapters;
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.databinding.DrawerInstanceBinding;
import app.fedilab.nitterizeme.entities.Instance;
import app.fedilab.nitterizeme.helpers.Utils;

import static app.fedilab.nitterizeme.activities.MainActivity.APP_PREFS;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_BIBLIOGRAM_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_INVIDIOUS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_NITTER_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_SCRIBERIP_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_TEDDIT_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_WIKILESS_HOST;


public class InstanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Instance> instances;
    private final InstanceAdapter instanceAdapter;

    public InstanceAdapter(List<Instance> instances) {
        this.instances = instances;
        this.instanceAdapter = this;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        DrawerInstanceBinding itemBinding = DrawerInstanceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = (ViewHolder) viewHolder;
        Instance instance = instances.get(i);
        Context context = viewHolder.itemView.getContext();
        SharedPreferences sharedpreferences = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        //Reset checked instances by type when tipping

        holder.binding.checkboxInstance.setText(instance.getDomain());
        if (instance.getLatency() == -1) {
            holder.binding.latency.setVisibility(View.GONE);
            holder.binding.progress.setVisibility(View.GONE);
        } else if (instance.getLatency() == 0) {
            holder.binding.latency.setVisibility(View.GONE);
            holder.binding.progress.setVisibility(View.VISIBLE);
        } else if (instance.getLatency() == -2) {
            holder.binding.latency.setText(R.string.error);
            holder.binding.latency.setVisibility(View.GONE);
            holder.binding.progress.setVisibility(View.VISIBLE);
        } else {
            holder.binding.latency.setText(String.format(Locale.getDefault(), "%d ms", instance.getLatency()));
            holder.binding.latency.setVisibility(View.VISIBLE);
            holder.binding.progress.setVisibility(View.GONE);
        }
        List<String> locales = instance.getLocales();
        StringBuilder locale = new StringBuilder();
        for (String _l : locales) {
            locale.append(_l).append(" ");
        }
        holder.binding.locale.setText(locale.toString());
        if (instance.isCloudflare()) {
            holder.binding.useCloudflare.setVisibility(View.VISIBLE);
            holder.binding.useCloudflare.setOnClickListener(v -> Toast.makeText(context, R.string.cloudflare, Toast.LENGTH_SHORT).show());
        } else {
            holder.binding.useCloudflare.setVisibility(View.GONE);
        }

        holder.binding.checkboxInstance.setChecked(instance.isChecked());

        holder.binding.checkboxInstance.setOnClickListener(v -> {

            boolean isChecked = !instance.isChecked();
            for (Instance _ins : instances) {
                _ins.setChecked(false);
            }
            instance.setChecked(true);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            switch (instance.getInstanceType()) {
                case YOUTUBE:
                    if (isChecked) {
                        editor.putString(SET_INVIDIOUS_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_INVIDIOUS_HOST, null);
                    }
                    editor.apply();
                    break;
                case TWITTER:
                    if (isChecked) {
                        editor.putString(SET_NITTER_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_NITTER_HOST, null);
                    }
                    editor.apply();
                    break;
                case INSTAGRAM:
                    if (isChecked) {
                        editor.putString(SET_BIBLIOGRAM_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_BIBLIOGRAM_HOST, null);
                    }
                    editor.apply();
                    break;
                case REDDIT:
                    if (isChecked) {
                        editor.putString(SET_TEDDIT_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_TEDDIT_HOST, null);
                    }
                    editor.apply();
                    break;
                case MEDIUM:
                    if (isChecked) {
                        editor.putString(SET_SCRIBERIP_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_SCRIBERIP_HOST, null);
                    }
                    editor.apply();
                    break;
                case WIKIPEDIA:
                    if (isChecked) {
                        editor.putString(SET_WIKILESS_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_WIKILESS_HOST, null);
                    }
                    editor.apply();
                    break;
            }

            instanceAdapter.notifyItemRangeChanged(0, instances.size());
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    public void evalLatency() {
        for (Instance instance : instances) {
            instance.setLatency(0);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    long ping = Utils.ping(instance.getDomain());
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    @SuppressLint("NotifyDataSetChanged") Runnable myRunnable = () -> {
                        instance.setLatency(ping);
                        notifyDataSetChanged();
                    };
                    mainHandler.post(myRunnable);
                }
            };
            thread.start();
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return instances.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        DrawerInstanceBinding binding;

        ViewHolder(@NonNull DrawerInstanceBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


}