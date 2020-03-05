package cn.people.cms.util.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: 张新征
 * Date: 2017/3/3 12:20
 * Description:音频类型检查工具类
 */
public class AudioChecker {
    private static final Set<String> mediaType = new HashSet<>();
    static {
        String s = "mp3,wma,flac,aac,mmf,amr,m4a,m4r,wav";
        mediaType.addAll(Arrays.asList(s.split(",")));
    }
    public static boolean isAudio(String audio) {
        return mediaType.contains(audio.toLowerCase()) ? true : false;
    }
}
