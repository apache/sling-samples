package org.apache.sling.samples.oauth_demo;

import java.util.List;

public record YoutubeSearchResponse (
    String kind,
    String etag,
    String nextPageToken,
    String regionCode,
    PageInfo pageInfo,
    List<Item> items,
    Error error            
) {}