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
import org.apache.sling.extensions.oidc_rp.OidcClient;
import org.apache.sling.extensions.oidc_rp.OidcConnection;
import org.apache.sling.extensions.oidc_rp.OidcToken;
import org.apache.sling.extensions.oidc_rp.OidcTokenStore;
import org.apache.sling.extensions.oidc_rp.support.OAuthEnabledSlingServlet;
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
    private final OidcConnection connection;
    private OidcTokenStore tokenStore;
    private OidcClient oidcClient;
    
    @Activate
    public YoutubeSearchServlet(@Reference OidcConnection connection, @Reference OidcTokenStore tokenStore, @Reference OidcClient oidcClient) {
        this.connection = connection;
        this.tokenStore = tokenStore;
        this.oidcClient = oidcClient;
    }

    @Override
    protected @NotNull String getRedirectPath(@NotNull SlingHttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    protected @NotNull OidcConnection getConnection() {
        return connection;
    }
    
    @Override
    protected @NotNull OidcClient getOidcClient() {
        return oidcClient;
    }
    
    @Override
    protected @NotNull OidcTokenStore getTokenStore() {
        return tokenStore;
    }

    @Override
    protected void doGetWithToken(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response,
            OidcToken token) throws IOException, ServletException {

        try {
            YoutubeHttpClient client = new YoutubeHttpClient();
            YoutubeSearchResponse youtubeResponse = client.searchVideos("adaptTo", token.getValue());
            
            RequestDispatcherOptions opts = new RequestDispatcherOptions();
            opts.setReplaceSelectors("show");
            
            request.setAttribute("ytResponse", youtubeResponse);
            response.setContentType("text/html");
            request.getRequestDispatcher(request.getResource(), opts).include(request, response);
            
            // TODO - needs a thin use object shim to expose the request attribute or a generic BVP
            
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setContentType("text/html");
//            response.getWriter().write(String.format("<p>Got %s results out of total %s</p>", youtubeResponse.items().size(), youtubeResponse.pageInfo().totalResults()));
//            response.getWriter().write("<ul>");
//            for (Item item : youtubeResponse.items()) {
//                response.getWriter().write(String.format("<li><a href=\"https://www.youtube.com/watch?v=%s\">%s</a></li>",
//                        item.id().videoId(), item.snippet().title()));
//            }
//            response.getWriter().write("</ul>");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Please retry later");
        }
    }
}
