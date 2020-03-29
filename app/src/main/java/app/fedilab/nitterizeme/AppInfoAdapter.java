package app.fedilab.nitterizeme;
/* Copyright 2020 Thomas Schneider
 *
 * This file is a part of NitterizeMe
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * NitterizeMe is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with NitterizeMe; if not,
 * see <http://www.gnu.org/licenses>. */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter {

    private List<AppInfo> appInfos;

    AppInfoAdapter(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ViewHolder(layoutInflater.inflate(R.layout.drawer_app_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = (ViewHolder) viewHolder;
        AppInfo appInfo = appInfos.get(viewHolder.getAdapterPosition());
        Context context = holder.itemView.getContext();
        holder.domain.setText(appInfo.getDomain());
        if (appInfo.getApplicationInfo() != null) {
            Drawable icon = appInfo.getApplicationInfo().loadIcon(context.getPackageManager());
            try {
                holder.app_icon.setImageDrawable(icon);
            } catch (Resources.NotFoundException e) {
                holder.app_icon.setImageResource(R.drawable.ic_android);
            }
            String app_label = context.getPackageManager().getApplicationLabel(appInfo.getApplicationInfo()).toString();
            if (appInfo.getApplicationInfo().packageName.compareTo(BuildConfig.APPLICATION_ID) == 0) {
                holder.application_label.setText(app_label);
                holder.package_name.setVisibility(View.GONE);
                holder.valid.setImageResource(R.drawable.ic_check);
                holder.valid.setContentDescription(context.getString(R.string.valid));
            } else {
                String package_name = appInfo.getApplicationInfo().packageName;
                holder.application_label.setText(app_label);
                holder.package_name.setVisibility(View.VISIBLE);
                holder.package_name.setText(String.format("(%s)", package_name));
                holder.valid.setImageResource(R.drawable.ic_error);
                holder.valid.setContentDescription(context.getString(R.string.error));
            }
            holder.main_container.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", appInfo.getApplicationInfo().packageName, null);
                intent.setData(uri);
                context.startActivity(intent);
            });
        } else {
            holder.application_label.setText(R.string.no_apps);
            holder.app_icon.setImageResource(R.drawable.ic_android);
            holder.valid.setContentDescription(context.getString(R.string.warning));
            holder.valid.setImageResource(R.drawable.ic_warning);
            holder.main_container.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nitterizeme", "test");
                String url = "https://" + appInfo.getDomain();
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView app_icon, valid;
        TextView application_label, package_name, domain;
        ConstraintLayout main_container;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            app_icon = itemView.findViewById(R.id.app_icon);
            valid = itemView.findViewById(R.id.valid);
            application_label = itemView.findViewById(R.id.application_label);
            package_name = itemView.findViewById(R.id.package_name);
            domain = itemView.findViewById(R.id.domain);
            main_container = itemView.findViewById(R.id.main_container);
        }
    }


}