package org.apache.sling.samples.oauth_demo;

public record ErrorDetail (
    String message,
    String domain,
    String reason,
    String location,
    String locationType
) {}