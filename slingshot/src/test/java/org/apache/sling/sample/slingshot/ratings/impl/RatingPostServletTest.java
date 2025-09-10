/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.sample.slingshot.ratings.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.sample.slingshot.ratings.RatingsService;
import org.apache.sling.sample.slingshot.ratings.RatingsUtil;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatingPostServletTest {

    @Rule
    public final SlingContext context = new SlingContext();

    @Test
    @SuppressWarnings("deprecation") // Using deprecated Sling API for backward compatibility testing
    public void successfulSave() throws Exception {

        // Mock the RatingsService
        RatingsService ratingsService = mock(RatingsService.class);
        when(ratingsService.getRating(any(Resource.class))).thenReturn(4.5);

        // Mock the ResourceResolverFactory
        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);

        // Create test resource
        Resource testResource = context.create().resource("/content/slingshot/users/test/entries/entry1");
        when(resourceResolver.getResource("/content/slingshot/users/test/entries/entry1")).thenReturn(testResource);

        // Register services
        context.registerService(RatingsService.class, ratingsService);
        context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

        // Create and register the servlet
        RatingPostServlet servlet = context.registerInjectActivateService(new RatingPostServlet());

        // Verify the servlet can be instantiated and registered
        assertNotNull(servlet);

        // Create mock request and response
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        // Set up request parameters
        when(request.getParameter(RatingsUtil.PROPERTY_RATING)).thenReturn("5.0");
        when(request.getRemoteUser()).thenReturn("testuser");
        when(request.getResource()).thenReturn(testResource);

        // Set up response writer
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Execute the servlet POST method
        servlet.doPost(request, response);

        // Verify interactions
        verify(ratingsService).setRating(any(Resource.class), anyString(), anyDouble());
        verify(ratingsService).getRating(any(Resource.class));
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("utf-8");
        verify(response).setStatus(200);

        // Verify the JSON response contains the rating
        writer.flush();
        String responseContent = stringWriter.toString();
        assertNotNull(responseContent);
        // Basic check that response contains rating JSON structure
        assert (responseContent.contains("rating"));
        assert (responseContent.contains("4.5"));
    }
}
