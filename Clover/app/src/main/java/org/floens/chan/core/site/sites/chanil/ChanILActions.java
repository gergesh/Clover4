package org.floens.chan.core.site.sites.chanil;

import org.floens.chan.core.site.common.CommonSite;
import org.floens.chan.core.site.common.MultipartHttpCall;
import org.floens.chan.core.site.common.vichan.VichanActions;
import org.floens.chan.core.site.http.Reply;
import org.floens.chan.core.site.http.ReplyResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class ChanILActions extends VichanActions {
    ChanILActions(CommonSite commonSite) {
        super(commonSite);
    }

    @Override
    public void setupPost(Reply reply, MultipartHttpCall call) {
        super.setupPost(reply, call);
        call.parameter("post", "תגובה חדשה");

        call.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        call.header("Accept", "*/*");
        call.header("Accept-Language", "en-US,en;q=0.5");
        call.header("Accept-Encoding", "gzip, deflate");
        if (reply.loadable.isThreadMode()) {
            call.header("Referer", "https://chan.org.il/" + reply.loadable.board.code + "/res/" + reply.loadable.no + ".html");
            call.parameter("thread", String.valueOf(reply.loadable.no));
        }
    }

    @Override
    public void handlePost(ReplyResponse replyResponse, Response response, String result) {
        Matcher err = Pattern.compile("<h2 style=\"margin:20px 0\">(.+)</h2>").matcher(result);
        if (err.find()) {
            replyResponse.errorMessage = err.group(0);
        } else {
            replyResponse.posted = true;
        }
    }
}
