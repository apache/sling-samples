<%-- 
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
--%>

# This is the common parts of our "samples" schemas
# It is included (using JSP includes for now) in
# the resourceType-specific schemas

# Website navigation  information
type Navigation {
  # The root path of our website
  root: String
  # List of content sections
  sections: [Section]
  # Previous article, if applicable
  previous: String
  # Next article, if application
  next: String
}

# A content section with its name and path
type Section { 
  name: String
  path: String
  
  ## fetch:samples/articlesBySection
  #
  # List of articles in this section
  articles: [Article]
}

# Articles are the core content of our website
# Fields names are self-describing
type Article {
  path: String
  title: String
  tags: [String]
  text: String

  ## fetch:samples/seeAlso
  #
  # List of "See Also" articles
  seeAlso: [Article]
}

# A query for articles which have specific tags
type TagQuery {
  # List of tags specified in the query
  query: [String]
  # List of articles found
  articles : [Article]
}