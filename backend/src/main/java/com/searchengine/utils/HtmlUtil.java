package com.searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlUtil {
    public static String removeHtmlTags(String html){
        Document doc = Jsoup.parse(html);
        return doc.text();
    }

    public static String handleString (String str) {
        str = str.replaceAll("\\r\\n|\\r|\\n", "");
        return "\"" + str + "\"";
    }
}
