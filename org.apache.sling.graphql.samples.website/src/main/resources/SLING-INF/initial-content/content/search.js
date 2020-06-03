/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Licensed to the Apache Software Foundation (ASF) under one
~ or more contributor license agreements.  See the NOTICE file
~ distributed with this work for additional information
~ regarding copyright ownership.  The ASF licenses this file
~ to you under the Apache License, Version 2.0 (the
~ "License"); you may not use this file except in compliance
~ with the License.  You may obtain a copy of the License at
~
~   http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing,
~ software distributed under the License is distributed on an
~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~ KIND, either express or implied.  See the License for the
~ specific language governing permissions and limitations
~ under the License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

var template;

function render(resultElement, data) {
    console.log(`Rendering ${data.result.article.length} articles`);
    $(resultElement).html(template({ data : data }));
}

function queryAndRender(queryText) {
    var query = `{
        article(withText: "${queryText}") {
            path
            title
            seeAlso {
            path
            title
            tags
            }
        }
        }`;

    console.log(`Querying:\n${query}`);

    fetch('/graphql.json', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        },
        body: JSON.stringify({query: `${query} `})
    })
    .then(r => r.json())
    .then(json => render($("#results"), { result: json.data, query: queryText}))
    ;
}

$(document).ready(function() {
    template = Handlebars.compile($("#template").html());
    $("#search").submit(function(e) {
        var searchText = $("#searchText").val();
        queryAndRender(searchText);
        return false;
    });
});

