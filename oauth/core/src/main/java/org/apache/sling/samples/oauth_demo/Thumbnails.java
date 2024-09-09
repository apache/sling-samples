package org.apache.sling.samples.oauth_demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Thumbnails (
    @JsonProperty("default") Thumbnail defaultThumbnail,
    Thumbnail medium,
    Thumbnail high
) {}