package com.example.annapurna;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParserUtil {

    public static final String SOURCE_LOCAL = "LOCAL";
    public static final String SOURCE_YOUTUBE = "YOUTUBE";
    public static final String SOURCE_INSTAGRAM = "INSTAGRAM";

    // Extract raw HTTP/HTTPS URL from text shared by external apps
    public static String extractUrl(String text) {
        if (text == null) return "";
        Pattern pattern = Pattern.compile("(https?://\\S+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return text.trim();
    }

    // Determine platform source type based on URL pattern
    public static String detectSource(String url) {
        if (url == null || url.trim().isEmpty()) {
            return SOURCE_LOCAL;
        }
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be")) {
            return SOURCE_YOUTUBE;
        } else if (lowerUrl.contains("instagram.com")) {
            return SOURCE_INSTAGRAM;
        }
        return SOURCE_LOCAL;
    }

    // Extract YouTube video ID from standard URLs, short links, or Shorts
    public static String extractYouTubeId(String url) {
        if (url == null) return null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|/shorts/|\\&v=)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    // Build HQ YouTube thumbnail image URL
    public static String getYouTubeThumbnailUrl(String videoId) {
        if (videoId == null || videoId.isEmpty()) return null;
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    // Format HTML for embedding YouTube in WebView
    public static String getYouTubeEmbedHtml(String videoId) {
        return "<html><body style='margin:0;padding:0;background-color:black;'>" +
                "<iframe width='100%' height='100%' src='https://www.youtube.com/embed/" + videoId +
                "?autoplay=1&modestbranding=1&rel=0' frameborder='0' allowfullscreen></iframe>" +
                "</body></html>";
    }

    // Format HTML for embedding Instagram Reels in WebView
    public static String getInstagramEmbedHtml(String url) {
        String cleanUrl = url.endsWith("/") ? url : url + "/";
        return "<html><body style='margin:0;padding:0;background-color:black;display:flex;justify-content:center;align-items:center;'>" +
                "<iframe src='" + cleanUrl + "embed' width='100%' height='100%' frameborder='0' scrolling='no'></iframe>" +
                "</body></html>";
    }
}