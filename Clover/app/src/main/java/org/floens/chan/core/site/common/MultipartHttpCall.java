/*
 * Clover - 4chan browser https://github.com/Floens/Clover/
 * Copyright (C) 2014  Floens
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.floens.chan.core.site.common;

import android.support.annotation.Nullable;

import org.floens.chan.core.site.Site;
import org.floens.chan.core.site.http.HttpCall;
import org.floens.chan.core.site.http.ProgressRequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class MultipartHttpCall extends HttpCall {
    private final MultipartBody.Builder formBuilder;

    private HttpUrl url;
    private Map<String, String> headers;

    public MultipartHttpCall(Site site) {
        super(site);

        headers = new HashMap<>();
        formBuilder = new MultipartBody.Builder();
        formBuilder.setType(MultipartBody.FORM);
    }

    public MultipartHttpCall url(HttpUrl url) {
        this.url = url;
        return this;
    }

    public MultipartHttpCall parameter(String name, String value) {
        formBuilder.addFormDataPart(name, value);
        return this;
    }

    public MultipartHttpCall fileParameter(String name, String filename, File file) {
        formBuilder.addFormDataPart(name, filename, RequestBody.create(
                MediaType.parse("application/octet-stream"), file
        ));
        return this;
    }

    public MultipartHttpCall header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public void setup(
            Request.Builder requestBuilder,
            @Nullable ProgressRequestBody.ProgressRequestListener progressListener
    ) {
        requestBuilder.url(url);
        String r = url.scheme() + "://" + url.host();
        if (url.port() != 80 && url.port() != 443) {
            r += ":" + url.port();
        }

        for (String key : headers.keySet()) {
            requestBuilder.addHeader(key, headers.get(key));
        }

        if (!headers.containsKey("Referer")) {
            requestBuilder.addHeader("Referer", r);
        }
        requestBuilder.post(formBuilder.build());
    }
}
