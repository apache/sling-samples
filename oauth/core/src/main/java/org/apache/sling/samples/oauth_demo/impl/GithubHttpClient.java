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
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.apache.sling.samples.oauth_demo.GithubRepositoriesSearchResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GithubHttpClient {
    public static final String API_BASE = "https://api.github.com";
    
    public GithubRepositoriesSearchResponse searchRepositories(String query, String accessToken) throws IOException, InterruptedException {
        
        try ( HttpClient client = HttpClient.newHttpClient() ) {
            query = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            HttpRequest searchRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/search/repositories?q=" + query))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .build();
            
            HttpResponse<String> response = client.send(searchRequest, BodyHandlers.ofString());
    
            String body = response.body();
            
            ObjectMapper mapper = new ObjectMapper();
            // client mapping is still incomplete
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.reader().readValue(body, GithubRepositoriesSearchResponse.class);
        }
    }   
}