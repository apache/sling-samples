/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.samples.oauth_demo.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.extensions.oauth_client.ClientConnection;
import org.apache.sling.extensions.oauth_client.OAuthToken;
import org.apache.sling.extensions.oauth_client.OAuthTokenRefresher;
import org.apache.sling.extensions.oauth_client.OAuthTokenStore;
import org.apache.sling.extensions.oauth_client.support.OAuthEnabledSlingServlet;
import org.apache.sling.samples.oauth_demo.YoutubeSearchResponse;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "oauth-demo/components/youtube-videos", methods = "GET", extensions = "html")
public class YoutubeSearchServlet extends OAuthEnabledSlingServlet {

    private static final long serialVersionUID = 1L;
    
    @Activate
    public YoutubeSearchServlet(@Reference(target = "(name=google)") ClientConnection connection, @Reference OAuthTokenStore tokenStore, @Reference OAuthTokenRefresher oidcClient) {
        super(connection, tokenStore, oidcClient);
    }

    @Override
    protected @NotNull String getRedirectPath(@NotNull SlingHttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    protected void doGetWithToken(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response,
            OAuthToken token) throws IOException, ServletException {

        try {
            String search = request.getParameter("search");
            if (search != null && !search.isBlank()) {
                YoutubeHttpClient client = new YoutubeHttpClient();
                YoutubeSearchResponse youtubeResponse = client.searchVideos(search, token.getValue());
                request.setAttribute("ytResponse", youtubeResponse);
            }
            
            RequestDispatcherOptions opts = new RequestDispatcherOptions();
            opts.setReplaceSelectors("show");
            
            response.setContentType("text/html");
            request.getRequestDispatcher(request.getResource(), opts).include(request, response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Please retry later");
        } catch (YoutubeApiException e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    protected boolean isInvalidAccessTokenException(Exception e) {
        return e.getCause() instanceof InvalidAccessTokenException;
    }
}
