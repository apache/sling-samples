package org.apache.sling.samples.oauth_demo;

public record Item (
    String kind,
    String etag,
    Id id,
    Snippet snippet
) {}