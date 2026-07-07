package com.agora.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    private static final String URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    public static boolean containsUrl(String text) {
        Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        // 如果匹配到URL，则返回true
        return matcher.find();
    }

    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // 将匹配到的URL添加到列表中
            urls.add(text.substring(matcher.start(0), matcher.end(0)));
        }

        return urls;
    }
}
