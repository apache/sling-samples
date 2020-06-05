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
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.graphql.api.SlingDataFetcher;
import org.apache.sling.graphql.api.SlingDataFetcherEnvironment;
import org.apache.sling.graphql.samples.website.models.SlingWrappers;
import org.osgi.service.component.annotations.Component;

/** Find articles that belong to a given section (business, news etc) */
@Component(service = SlingDataFetcher.class, property = {"name=website/articlesBySection"})
public class ArticlesBySectionFetcher implements SlingDataFetcher<Object> {

    @Override
    public Object get(SlingDataFetcherEnvironment env) throws Exception {
        // TODO should paginate instead
        final int maxArticles = 42;

        final Resource section = env.getCurrentResource();
        final List<Map<String, Object>> result = new ArrayList<>();
        final Iterable<Resource> it = () -> section.getResourceResolver().getChildren(section).iterator();
        StreamSupport.stream(it.spliterator(), false).limit(maxArticles)
                .forEach(child -> result.add(SlingWrappers.resourceWrapper(child)));
        return result;
    }
}