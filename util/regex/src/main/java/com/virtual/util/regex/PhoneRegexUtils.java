package com.virtual.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * find()与 matches()的区别
 * find()：是否存在与该模式匹配的下一个子序列。简单来说就是在字符某部分匹配上模式就会返回true，
 * 同时匹配位置会记录到当前位置，再次调用时从该处匹配下一个。
 * matches()：整个字符串是否匹配上模式，匹配上则返回true，否则false。
 */
public class PhoneRegexUtils {

    /**
     * 匹配字符串中是否包含手机号格式校验正则
     */
    private static final String PHONE_REGEX_C = "1(3\\d|4[57]|5[0-35-9]|7[0135678]|8\\d)\\d{8}";
    /**
     * 匹配字符串是否为手机号格式校验正则
     */
    private static final String PHONE_REGEX = "^1(3\\d|4[57]|5[0-35-9]|7[0135678]|8\\d)\\d{8}$";

    private static Pattern PHONE_COMPILE_C;

    /**
     * 手机号格式校验
     *
     * @param phoneStr phone
     * @return boolean
     */
    public static boolean checkPhone(String phoneStr) {
        if (StringUtils.isEmpty(phoneStr)) {
            return false;
        }
        return phoneStr.matches(PHONE_REGEX);
    }

    /**
     * 字符串中是否包含手机号
     *
     * @param phoneStr phone
     * @return boolean
     */
    public static boolean containsPhone(String phoneStr) {
        if (StringUtils.isEmpty(phoneStr)) {
            return false;
        }
        if (PHONE_COMPILE_C == null) {
            PHONE_COMPILE_C = Pattern.compile(PHONE_REGEX_C);
        }
        Matcher matcher = PHONE_COMPILE_C.matcher(phoneStr);
        return matcher.find();
    }

    public static String takePhone(String targetStr) {
        return takePhone(targetStr, ",");
    }

    public static String takePhone(String targetStr, String regex) {
        if (PHONE_COMPILE_C == null) {
            PHONE_COMPILE_C = Pattern.compile(PHONE_REGEX_C);
        }
        Matcher matcher = PHONE_COMPILE_C.matcher(targetStr);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(regex).append(matcher.group());
        }
        return result.toString().replaceFirst(regex, "");
    }
}
