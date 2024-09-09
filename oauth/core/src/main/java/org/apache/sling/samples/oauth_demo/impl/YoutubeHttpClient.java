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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.sling.samples.oauth_demo.YoutubeSearchResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class YoutubeHttpClient {
    
    public static final String API_BASE = "https://youtube.googleapis.com/youtube/v3";
    
    public YoutubeSearchResponse searchVideos(String query, String accessToken) throws IOException, InterruptedException {
        
        try ( HttpClient client = HttpClient.newHttpClient() ) {
            HttpRequest searchRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/search?part=snippet&maxResults=20&q=" + query + "&type=video"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .build();
            
            HttpResponse<String> response = client.send(searchRequest, BodyHandlers.ofString());
    
            String body = response.body();
            
            System.out.println(body);
            
            ObjectMapper mapper = new ObjectMapper();
            return mapper.reader().readValue(body, YoutubeSearchResponse.class);
        }
    }        
}