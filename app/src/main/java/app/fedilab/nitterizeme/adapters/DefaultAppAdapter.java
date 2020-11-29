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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.entities.DefaultApp;
import app.fedilab.nitterizeme.sqlite.DefaultAppDAO;
import app.fedilab.nitterizeme.sqlite.Sqlite;

public class DefaultAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DefaultApp> defaultApps;

    public DefaultAppAdapter(List<DefaultApp> packageNames) {
        this.defaultApps = packageNames;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ViewHolder(layoutInflater.inflate(R.layout.drawer_default_app, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        DefaultApp defaultApp = defaultApps.get(viewHolder.getAdapterPosition());
        ViewHolder holder = (ViewHolder) viewHolder;
        Context context = holder.itemView.getContext();
        if (defaultApp.getApplicationInfo() != null) {
            Drawable icon = defaultApp.getApplicationInfo().loadIcon(context.getPackageManager());
            try {
                holder.app_icon.setImageDrawable(icon);
            } catch (Resources.NotFoundException e) {
                holder.app_icon.setImageResource(R.drawable.ic_android);
            }
            String app_name = context.getPackageManager().getApplicationLabel(defaultApp.getApplicationInfo()).toString();
            String app_package = defaultApp.getApplicationInfo().packageName;
            holder.app_name.setText(app_name);
            holder.app_package.setText(app_package);
            holder.delete.setOnClickListener(v -> {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context, R.style.AppThemeDialogDelete);
                confirmDialog.setMessage(context.getString(R.string.delete_app_from_default, app_name));
                confirmDialog.setIcon(R.mipmap.ic_launcher);
                confirmDialog.setPositiveButton(R.string.delete, (dialog, id) -> {
                    SQLiteDatabase db = Sqlite.getInstance(context.getApplicationContext(), Sqlite.DB_NAME, null, Sqlite.DB_VERSION).open();
                    new DefaultAppDAO(context, db).removeApp(app_package);
                    defaultApps.remove(defaultApp);
                    notifyItemRemoved(i);
                    if (defaultApps.size() == 0) {
                        TextView no_apps = ((Activity) context).findViewById(R.id.no_apps);
                        if (no_apps != null) {
                            no_apps.setVisibility(View.VISIBLE);
                        }
                    }
                    dialog.dismiss();
                });
                confirmDialog.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                AlertDialog alertDialog = confirmDialog.create();
                alertDialog.show();

            });
        } else {
            holder.app_icon.setImageResource(R.drawable.ic_android);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return defaultApps.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView app_icon, delete;
        TextView app_name, app_package;
        ConstraintLayout main_container;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            app_icon = itemView.findViewById(R.id.app_icon);
            delete = itemView.findViewById(R.id.delete);
            app_name = itemView.findViewById(R.id.app_name);
            app_package = itemView.findViewById(R.id.app_package);
            main_container = itemView.findViewById(R.id.main_container);
        }
    }

}