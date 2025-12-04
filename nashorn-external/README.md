# Apache Sling Samples - Nashorn External

This OSGi bundle exemplifies the minimal setup required to use the Nashorn JavaScript engine as an external script engine in Apache Sling.

Significant implementation details:

- embeds the [Nashorn engine](https://github.com/openjdk/nashorn) inside the bundle
- also embeds the transitive ASM depedndency to make the bundle fully self-contained
- uses a more recent ASM version for compatibility with recent JDKs
- exposes a servlet at `/bin/nashorn` that executes a simple script using the Nashorn engine
- obtains the `ScriptEngineManager` as an OSGi reference

## Usage

Build with `mvn install`. Deploy with `mvn sling:install`. Test by accessing http://localhost:8080/bin/nashorn-test .
