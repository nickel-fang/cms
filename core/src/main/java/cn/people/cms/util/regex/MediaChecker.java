package cn.people.cms.util.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: 张新征
 * Date: 2017/3/3 12:20
 * Description:音视频(媒体)类型检查工具类
 */
public class MediaChecker {
    private static final Set<String> mediaType = new HashSet<>();
    static {
        String s = "mp4,mpg4,avi,3gp,rmvb,rm,wmv,mkv,mpeg,mp4,vob,swf,flv,f4v,mov,mp3,wma,flac,aac,mmf,amr,m4a,m4r,wav";
        mediaType.addAll(Arrays.asList(s.split(",")));
    }
    public static boolean isMedia(String media) {
        return mediaType.contains(media.toLowerCase()) ? true : false;
    }
}
