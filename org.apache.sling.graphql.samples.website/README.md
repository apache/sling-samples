Apache Sling GraphQL demo website
====

This is a work in progress demo of the [Sling GraphQL Core](https://github.com/apache/sling-org-apache-sling-graphql-core/).

It demonstrates both server-side GraphQL queries, used for content aggregation, and the 
more traditional client-side queries, using the same GraphQL schemas and data fetching
Java components for both variants. Handlebars templates are used for rendering content,
either on the server or client side depending on the website sections.

Besides the page rendering code there's not much: GraphQL schema and query definitions
and a few Java classes used for aggregating or enhancing content and for content queries.

For now there's no pagination of query results, just arbitrary limits on the number
of results returned.

## How to run this

Build and run with

```bash
mvn clean install
java -jar target/dependency/org.apache.sling.feature.launcher.jar -f target/slingfeature-tmp/feature-sling12.json
```

And open http://localhost:8080/ which should redirect to `/articles/music.html` and show a list
of articles from the _Music_ category from our demo website.

Or point a GraphQL client to http://localhost:8080/graphql.json to test client-side queries.

## GraphQL and Handlebars, on both the server and client sides

The articles and navigation pages are rendered using server-side Handlebars templates,
which retrieve the aggregated JSON content of the current page by making an internal request
to the current path with a `.json` extension.

That aggregated JSON content is generated using server-side GraphQL queries so that a single
request provides all the page content and navigation.

Those `.json` URLs are also accessible from the outside if client-side rendering is preferred.

The search page at `/content/search.html`, which needs a more dynamic behavior, uses client-side
GraphQL queries and client-side Handlebars rendering, along with JQuery for glue. This
demonstratse using the same tools on the server or client side, with minimal differences
between both modes.

With this we get the best of both worlds: server-side queries and rendering for the article
pages, so that they make sense for Web search engines for example, and client-side queries and
rendering for the more dynamic "search" single-page application example.

Handlebars was selected for this example as it's simple and easy to implement on both the
server and client sides. As usual with Sling, everything is pluggable so it can be replaced with
your favorite rendering engine if desired.

A small amount of Java code is used to implement the content querying and aggregation extensions.
Writing that code requires only minimal knowledge of Sling. So far that code only uses the
Sling `Resource` and `ResourceResolver` APIs to collect and aggregate content by implementing
`SlingDataFetcher` services. The GraphQL core also supports scripted data fetchers but as I
write this we don't have one in this sample, see the GraphQL core module tests if you're interested
in that feature.

This sample currently also includes its own `HandlebarsScriptEngine` implementation for
server-side rendering. We might move it to its own Sling module later if there's interest, for
now it implements just the minimum required for this sample.

## Client-side GraphQL queries

Client-side queries work using an external [GraphiQL](https://www.electronjs.org/apps/graphiql)
client (or any suitable client) that talks to our `GraphQLServlet` - see below for how to run that.

Here's an example query:

    {
        navigation {
            search
            sections {
                path
                name
            }
        }
        article(withText: "virtual") {
            path
            title
            seeAlso {
            path
            title
            tags
            }
        }
    }

Besides fixing the `DataFetcher`s to use the correct context Resource, setting this up
only required activating the `GraphQLServlet` (using an OSGi config in the Feature Model
that starts this demo) and adding the below schema file. Everything else is shared between
the server-side and client-side query variants.

    # /apps/samples/servlet/GQLschema.jsp
    type Query {
      ## fetch:samples/articlesWithText
      article (withText : String) : [Article]
    }
    
    <%@include file="/apps/samples/common/GQLschema.jsp" %>


## Unstructured content

Sling applications often deal with unstructred or semi-structured content which
doesn't have a strict schema.

To demonstrate how this works with GraphQL queries, in this sample you can use a
query such as

    {
      navigation {
        root
      }
      random
    }

Which includes a randomly generated hierarchical structure, to test how GraphQL
clients cope (they should - it's part of the standard) with results such as

    {
      "data": {
        "navigation": {
          "root": "/content/articles"
        },
        "random": {
          "key1": 112,
          "sub2": {
            "key1": false,
            "sub2": {
              "key1": true
            },
            "key5": [
              true,
              true
            ],
            "key3": true
          }
        }
      }
    }

where the "shape" and content of the `random` element can vary widely, simulating
varying content structures.

## Under the hood

The scripts and source code mentioned below are found in the source code and initial content of this
demo module.

For either server or client-side queries, the GraphQL core retrieves a schema for the current
Sling Resource by making an internal request with the `.GQLschema` extension. You can see those
schemas by adding that extension to the article and navigation pages. They are generated using the
standard Sling request processing mechanism, so very flexible and resource-type specific if needed.

The server-side GraphQL queries are defined in `json.gql` scripts for each resource type, and executed
in the context of the current Sling Resource. Here's the current `article/json.gql` query as an example:

    { 
      navigation {
        sections {
          path
          name
        }
      }
      article 
      { 
        title
        tags
        seeAlso {
          path
          title
        }
        text
      }
    }

Based on that script's name, according to the usual Sling conventions it is used by the Sling
`GraphQLScriptEngine` to execute the query and return the JSON document that provides everything
needed to render the page in one request. You can see those JSON documents by adding a `.json`
extension to the article and navigation pages.

In our examples, this JSON document includes navigation information (paths to content sections,
next/previous article etc.) and processed content like the `seeAlso` links. Those links are
fleshed out by the `SeeAlsoDataFetcher` Java class, as the raw content doesn't provide enough
information to render meaningful links. Such `DataFetcher` services are then active for both
server-side and client-side GraphQL queries.

The `search` single-page-app uses the same GraphQL queries, executed from the client side,
along with client-side Handlebars rendering. See the `search.html` and `graphql.js` source
files under `src/main/resources/SLING-INF/initial-content` for details.

For this demo, the `.rawjson` extension is configured to provide the default Sling JSON
rendering, for comparison or troubleshooting purposes.
