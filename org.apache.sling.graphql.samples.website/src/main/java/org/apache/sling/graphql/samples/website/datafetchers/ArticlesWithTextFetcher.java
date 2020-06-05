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

package org.apache.sling.graphql.samples.website.datafetchers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.graphql.api.SlingDataFetcher;
import org.apache.sling.graphql.api.SlingDataFetcherEnvironment;
import org.apache.sling.graphql.samples.website.models.SlingWrappers;
import org.osgi.service.component.annotations.Component;

/** Find articles which contain specific text */
@Component(service = SlingDataFetcher.class, property = {"name=website/articlesWithText"})
public class ArticlesWithTextFetcher implements SlingDataFetcher<Object> {

    public static final String P_WITH_TEXT = "withText";

    @Override
    public Object get(SlingDataFetcherEnvironment env) throws Exception {
        final String expectedText = env.getArgument(P_WITH_TEXT);
        final String jcrQuery = String.format("/jcr:root%s//*[jcr:contains(@text, '%s') or jcr:contains(@title, '%s')]",
                Constants.ARTICLES_ROOT, expectedText, expectedText);

        final List<Map<String, Object>> result = new ArrayList<>();
        final Iterator<Resource> it = env.getCurrentResource().getResourceResolver().findResources(jcrQuery, "xpath");
        // TODO should use pagination
        int counter = 451;
        while (it.hasNext()) {
            if (--counter <= 0) {
                break;
            }
            result.add(SlingWrappers.resourceWrapper(it.next()));
        }
        return result;
    }
}