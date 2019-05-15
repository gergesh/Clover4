package org.floens.chan.core.site.sites.chanil;

import org.floens.chan.core.model.Post;
import org.floens.chan.core.site.common.CommonSite;
import org.floens.chan.core.site.common.vichan.VichanEndpoints;

import java.util.Map;

import okhttp3.HttpUrl;

public class ChanILEndpoints extends VichanEndpoints {
    ChanILEndpoints(CommonSite commonSite, String rootUrl, String sysUrl) {
        super(commonSite, rootUrl, sysUrl);
    }

    @Override
    public HttpUrl thumbnailUrl(Post.Builder post, boolean spoiler, Map<String, String> arg) {
        return root.builder().s(post.board.code).s("thumb").s(arg.get("tim") + "." + arg.get("ext")).url();
    }
}
