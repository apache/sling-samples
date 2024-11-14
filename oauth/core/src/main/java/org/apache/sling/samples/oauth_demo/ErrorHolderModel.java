package org.apache.sling.samples.oauth_demo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class)
public class ErrorHolderModel {

    @RequestAttribute(name = "javax.servlet.error.message")
    private String message;

    @RequestAttribute(name = "javax.servlet.error.exception")
    private Exception exception;
    
    @SlingObject
    private SlingHttpServletResponse response;
    
    @PostConstruct
    public void init() {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
    
    public String getMessage() {
        return message ;
    }

    public Exception getException() {
        return exception;
    }
}
