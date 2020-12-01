package app.fedilab.nitterizeme.entities;

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
    private instanceType type;
    private boolean cloudflare = false;
    private String locale;

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

    public instanceType getType() {
        return type;
    }

    public void setType(instanceType type) {
        this.type = type;
    }

    public boolean isCloudflare() {
        return cloudflare;
    }

    public void setCloudflare(boolean cloudflare) {
        this.cloudflare = cloudflare;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public enum instanceType {
        INVIDIOUS,
        NITTER,
        BIBLIOGRAM,
        TEDDIT
    }
}
