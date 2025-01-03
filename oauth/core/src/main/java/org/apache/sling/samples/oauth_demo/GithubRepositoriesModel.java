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
package org.apache.sling.samples.oauth_demo;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.auth.oauth_client.ClientConnection;
import org.apache.sling.auth.oauth_client.OAuthTokenAccess;
import org.apache.sling.auth.oauth_client.OAuthTokenResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.samples.oauth_demo.impl.GithubHttpClient;

@Model(adaptables = SlingHttpServletRequest.class)
public class GithubRepositoriesModel {
    
    @SlingObject
    private SlingHttpServletRequest request;
    
    // get a reference to an OSGi service
    @OSGiService(filter = "(name=github)")
    private ClientConnection connection;
    
    @OSGiService
    private OAuthTokenAccess tokenAccess;

    private OAuthTokenResponse tokenResponse;
    
    @PostConstruct
    public void initToken() {
        tokenResponse = tokenAccess.getAccessToken(connection, request, request.getRequestURI());
    }
    
    public boolean needsLogin() {
        return !tokenResponse.hasValidToken();
    }
    
    public String getSearch() {
        return request.getParameter("search");
    }

    private boolean hasSearch() {
        String searchParam = getSearch();
        
        return searchParam != null && searchParam.trim().length() > 0;
    }
    
    public GithubRepositoriesSearchResponse getSearchResponse() throws IOException, InterruptedException {
        if ( hasSearch() && tokenResponse.hasValidToken() ) {
            return new GithubHttpClient().searchRepositories(request.getParameter("search"), tokenResponse.getTokenValue());
        }
        
        return null;
    }
    
    public URI getGithubLoginUrl() {
        if (needsLogin())
            return tokenResponse.getRedirectUri();
        return null;
    }

}
