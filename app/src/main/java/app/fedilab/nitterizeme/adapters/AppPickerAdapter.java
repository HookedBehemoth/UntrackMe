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

import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import app.fedilab.nitterizeme.R;
import app.fedilab.nitterizeme.entities.AppPicker;

public class AppPickerAdapter extends BaseAdapter {

    private final List<AppPicker> appPickers;

    public AppPickerAdapter(List<AppPicker> appPickers) {
        this.appPickers = appPickers;
    }


    @Override
    public int getCount() {
        return appPickers.size();
    }

    @Override
    public AppPicker getItem(int position) {
        return appPickers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        AppPicker appPicker = appPickers.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.drawer_app_picker, parent, false);
            holder = new ViewHolder();
            holder.app_icon = convertView.findViewById(R.id.app_icon);
            holder.app_name = convertView.findViewById(R.id.app_name);
            holder.app_container = convertView.findViewById(R.id.app_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.app_icon.setImageDrawable(appPicker.getIcon());
        } catch (Resources.NotFoundException e) {
            holder.app_icon.setImageResource(R.drawable.ic_android);
        }
        holder.app_name.setText(appPicker.getName());


        if (appPicker.isSelected()) {
            holder.app_container.setBackgroundResource(R.drawable.rounded_selector);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.app_container.setBackground(null);
            }
        }

        return convertView;

    }

    private static class ViewHolder {
        ImageView app_icon;
        TextView app_name;
        ConstraintLayout app_container;
    }

}