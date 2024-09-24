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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.extensions.oauth_client.ClientConnection;
import org.apache.sling.extensions.oauth_client.OAuthToken;
import org.apache.sling.extensions.oauth_client.OAuthTokenStore;
import org.apache.sling.extensions.oauth_client.OAuthUris;
import org.apache.sling.extensions.oauth_client.TokenState;
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
    private OAuthTokenStore tokenStore;
    
    public boolean needsLogin() {
        return getAccessToken().getState() != TokenState.VALID;
    }

    public boolean hasSearch() {
        String searchParam = request.getParameter("search");
        
        return searchParam != null && searchParam.trim().length() > 0;
    }
    
    private OAuthToken getAccessToken() {
        return tokenStore.getAccessToken(connection, request.getResourceResolver());
    }
    
    public GithubRepositoriesSearchResponse getSearchResponse() throws IOException, InterruptedException {
        if ( hasSearch() ) {
            return new GithubHttpClient().searchRepositories(request.getParameter("search"), getAccessToken().getValue());
        }
        
        return null;
    }
    
    public URI getGithubLoginUrl() {
        return OAuthUris.getOidcEntryPointUri(connection, request, request.getRequestURI());
    }

}
