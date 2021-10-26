package app.fedilab.nitterizeme.entities;

import java.util.List;

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
public class Instance {


    private String domain;
    private long latency = -1;
    private boolean checked = false;
    private instanceType instanceType;
    private boolean cloudflare = false;
    private List<String> locales;
    private String type;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isCloudflare() {
        return cloudflare;
    }

    public void setCloudflare(boolean cloudflare) {
        this.cloudflare = cloudflare;
    }

    public List<String> getLocales() {
        return locales;
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public Instance.instanceType getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(Instance.instanceType instanceType) {
        this.instanceType = instanceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public enum instanceType {
        YOUTUBE,
        TWITTER,
        INSTAGRAM,
        REDDIT,
        MEDIUM,
        WIKIPEDIA
    }
}
