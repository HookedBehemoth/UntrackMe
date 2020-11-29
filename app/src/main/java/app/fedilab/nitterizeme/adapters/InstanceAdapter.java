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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.entities.Instance;
import app.fedilab.nitterizeme.helpers.Utils;

import static app.fedilab.nitterizeme.activities.MainActivity.APP_PREFS;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_BIBLIOGRAM_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_INVIDIOUS_HOST;
import static app.fedilab.nitterizeme.activities.MainActivity.SET_NITTER_HOST;

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
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ViewHolder(layoutInflater.inflate(R.layout.drawer_instance, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = (ViewHolder) viewHolder;
        Instance instance = instances.get(i);
        Context context = viewHolder.itemView.getContext();
        SharedPreferences sharedpreferences = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        //Reset checked instances by type when tipping


        holder.checkbox_instance.setText(instance.getDomain());
        if (instance.getLatency() == -1) {
            holder.latency.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);
        } else if (instance.getLatency() == 0) {
            holder.latency.setVisibility(View.GONE);
            holder.progress.setVisibility(View.VISIBLE);
        } else if (instance.getLatency() == -2) {
            holder.latency.setText(R.string.error);
            holder.latency.setVisibility(View.GONE);
            holder.progress.setVisibility(View.VISIBLE);
        } else {
            holder.latency.setText(String.format(Locale.getDefault(), "%d ms", instance.getLatency()));
            holder.latency.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.GONE);
        }

        holder.locale.setText(instance.getLocale());
        if (instance.isCloudflare()) {
            holder.useCloudflare.setVisibility(View.VISIBLE);
            holder.useCloudflare.setOnClickListener(v -> Toast.makeText(context, R.string.cloudflare, Toast.LENGTH_SHORT).show());
        } else {
            holder.useCloudflare.setVisibility(View.GONE);
        }

        holder.checkbox_instance.setChecked(instance.isChecked());

        holder.checkbox_instance.setOnClickListener(v -> {

            boolean isChecked = !instance.isChecked();
            for (Instance _ins : instances) {
                _ins.setChecked(false);
            }
            instance.setChecked(true);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            switch (instance.getType()) {
                case INVIDIOUS:
                    if (isChecked) {
                        editor.putString(SET_INVIDIOUS_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_INVIDIOUS_HOST, null);
                    }
                    editor.apply();
                    break;
                case NITTER:
                    if (isChecked) {
                        editor.putString(SET_NITTER_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_NITTER_HOST, null);
                    }
                    editor.apply();
                    break;
                case BIBLIOGRAM:
                    if (isChecked) {
                        editor.putString(SET_BIBLIOGRAM_HOST, instance.getDomain().trim());
                    } else {
                        editor.putString(SET_BIBLIOGRAM_HOST, null);
                    }
                    editor.apply();
                    break;
            }
            instanceAdapter.notifyItemRangeChanged(0, instances.size());
        });


    }

    public void evalLatency() {
        for (Instance instance : instances) {
            instance.setLatency(0);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    long ping = Utils.ping(instance.getDomain());
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = () -> {
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
        RadioButton checkbox_instance;
        TextView latency, locale;
        ProgressBar progress;
        ImageView useCloudflare;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox_instance = itemView.findViewById(R.id.checkbox_instance);
            latency = itemView.findViewById(R.id.latency);
            progress = itemView.findViewById(R.id.progress);
            locale = itemView.findViewById(R.id.locale);
            useCloudflare = itemView.findViewById(R.id.use_cloudflare);
        }
    }


}