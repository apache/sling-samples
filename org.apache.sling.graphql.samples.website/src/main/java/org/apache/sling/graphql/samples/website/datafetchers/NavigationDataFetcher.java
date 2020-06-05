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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.graphql.api.SlingDataFetcher;
import org.apache.sling.graphql.api.SlingDataFetcherEnvironment;
import org.apache.sling.graphql.samples.website.models.SlingWrappers;
import org.osgi.service.component.annotations.Component;

/**
 * Retrieve navigation information for our website: sections, content root etc.
 */
@Component(service = SlingDataFetcher.class, property = {"name=website/navigation"})
public class NavigationDataFetcher implements SlingDataFetcher<Object> {

    private Object[] getSections(Resource r) {
        final List<Map<String, Object>> result = new ArrayList<>();
        final Resource root = r.getResourceResolver().getResource(Constants.ARTICLES_ROOT);
        final Iterable<Resource> it = () -> root.getResourceResolver().getChildren(root).iterator();
        StreamSupport.stream(it.spliterator(), false)
                .forEach(child -> result.add(SlingWrappers.resourceWrapper(child)));
        return result.toArray();

    }

    String getNextOrPreviousPath(Resource r, String propertyName, String currentValue, boolean isNext) {
        String result = null;
        final String jcrQuery = String.format("/jcr:root%s//*[%s %s '%s'] order by %s %s", Constants.ARTICLES_ROOT,
                propertyName, isNext ? ">" : "<", currentValue, propertyName, isNext ? "ascending" : "descending");
        final Iterator<Resource> it = r.getResourceResolver().findResources(jcrQuery, "xpath");
        if (it.hasNext()) {
            result = it.next().getPath();
        }
        return result;
    }

    /**
     * If r is an article, add previous/next navigation based on article filenames
     */
    private void maybeAddPrevNext(Map<String, Object> result, Resource r) {
        final String propName = "filename";
        if (Constants.ARTICLE_RESOURCE_SUPERTYPE.equals(r.getResourceSuperType())) {
            final String filename = r.adaptTo(ValueMap.class).get(propName, String.class);
            if (filename != null) {
                result.put("previous", getNextOrPreviousPath(r, propName, filename, false));
                result.put("next", getNextOrPreviousPath(r, propName, filename, true));
            }
        }
    }

    @Override
    public Object get(SlingDataFetcherEnvironment env) throws Exception {
        final Map<String, Object> result = new HashMap<>();
        result.put("root", Constants.ARTICLES_ROOT);
        result.put("sections", getSections(env.getCurrentResource()));
        result.put("search", Constants.SEARCH_PAGE_PATH);
        maybeAddPrevNext(result, FetcherUtil.getSourceResource(env));
        return result;
    }
}