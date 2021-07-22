JSON Generation using JSR 374 APIs
---

This small sample demonstrates
[how to generate JSON](./src/test/java/org/apache/sling/samples/json/generation/JsonGenerationTest.java)
using the JSR-374's
[JsonGenerator](https://javadoc.io/doc/javax.json/javax.json-api/latest/javax/json/stream/JsonGenerator.html)
(thanks @enorman for mentioning that option) and how to test 
the results using [JsonPath](https://github.com/json-path/JsonPath).

Generating JSON in this way looks like a good default method for Sling, as
the only required dependency is our `org.apache.sling.commons.johnzon` module,
which embeds those JSR-374 APIs as well as the [Apache Johnzon](https://johnzon.apache.org/) 
core in a relatively small bundle (150kB as I write this).

The `JsonGenerator` is used in several of our modules already, searching for `import javax.json.stream.JsonGenerator`
in the Sling codebase reports 26 results as I write this. This minimal example can help decide if it will also work
for your use case.
