package org.apache.sling.samples.oauth_demo;

import java.util.List;

public record Error (
    int code,
    String message,
    List<ErrorDetail> errors
) {}