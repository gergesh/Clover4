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
package org.floens.chan.core.site.sites.chanil;

import android.support.annotation.Nullable;

import org.floens.chan.core.model.Post;
import org.floens.chan.core.model.orm.Board;
import org.floens.chan.core.model.orm.Loadable;
import org.floens.chan.core.site.Site;
import org.floens.chan.core.site.SiteIcon;
import org.floens.chan.core.site.common.CommonSite;
import org.floens.chan.core.site.common.vichan.VichanApi;
import org.floens.chan.core.site.common.vichan.VichanCommentParser;

import okhttp3.HttpUrl;

public class ChanIL extends CommonSite {
    public static final CommonSiteUrlHandler URL_HANDLER = new CommonSiteUrlHandler() {
        @Override
        public Class<? extends Site> getSiteClass() {
            return ChanIL.class;
        }

        @Override
        public HttpUrl getUrl() {
            return HttpUrl.parse("https://chan.org.il/");
        }

        @Override
        public String[] getNames() {
            return new String[]{"chanIL"};
        }

        @Override
        public String desktopUrl(Loadable loadable, @Nullable Post post) {
            if (loadable.isCatalogMode()) {
                return getUrl().newBuilder().addPathSegment(loadable.boardCode).toString();
            } else if (loadable.isThreadMode()) {
                return getUrl().newBuilder()
                        .addPathSegment(loadable.boardCode).addPathSegment("res")
                        .addPathSegment(loadable.no + ".html")
                        .toString();
            } else {
                return getUrl().toString();
            }
        }
    };

    @Override
    public void setup() {
        setName("ChanIL");
        setIcon(SiteIcon.fromFavicon(HttpUrl.parse("https://chan.org.il/favicon.ico")));

        setBoards(
                Board.fromSiteNameCode(this, "רנדומלי", "b"),
                Board.fromSiteNameCode(this, "בינלאומי", "goys")
        );

        setResolvable(URL_HANDLER);

        setConfig(new CommonConfig() {
            @Override
            public boolean feature(Feature feature) {
                return feature == Feature.POSTING;
            }
        });

        setEndpoints(new ChanILEndpoints(this,
                "https://chan.org.il",
                "https://chan.org.il"));
        setActions(new ChanILActions(this));
        setApi(new VichanApi(this));
        setParser(new VichanCommentParser());
    }
}
