package cn.people.cms.util.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: 张新征
 * Date: 2017/3/3 12:17
 * Description:手机号检查工具类
 */
public class PhoneChecker {
    private static final Set<String> mobilehead = new HashSet<>();
    static {
        String s = "134,135,136,137,138,139,150,151,152,157,158,159,182,183,187,188,147,130,131,132,155,156,185,186,145,133,153,170,171,172,173,174,175,176,177,178,179,180,181,189";
        mobilehead.addAll(Arrays.asList(s.split(",")));
    }
    public static boolean isMobile(String mobile) {
        if(mobile.startsWith("86")) {
            mobile = mobile.substring(2);
        }
        if(mobile.startsWith("+86")) {
            mobile = mobile.substring(3);
        }
        if(mobile.startsWith("0086")) {
            mobile = mobile.substring(4);
        }
        if(mobile.length() != 11) {
            return false;
        } else {
            String start = mobile.substring(0, 3);
            return !mobilehead.contains(start)?false:mobile.matches("[0-9]+");
        }
    }
}