package cn.people.cms.util.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: 张新征
 * Date: 2017/3/3 12:20
 * Description:音视频(媒体)类型检查工具类
 */
public class ImageChecker {
    private static final Set<String> imageType = new HashSet<>();
    static {
        String s = "jpg,png,gif,jpeg,bmp";
        imageType.addAll(Arrays.asList(s.split(",")));
    }
    public static boolean isImage(String image) {
        return imageType.contains(image.toLowerCase()) ? true : false;
    }
}
