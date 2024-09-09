package org.apache.sling.samples.oauth_demo;

public record Snippet (
    String publishedAt,
    String channelId,
    String title,
    String description,
    Thumbnails thumbnails,
    String channelTitle,
    String liveBroadcastContent,
    String publishTime
) {}