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

 package org.apache.sling.samples.json.generation;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonGenerationTest {
    @Test
    public void basicTest() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final JsonGenerator generator = Json.createGenerator(bos);

        // Generation with JSR-374
        generator
            .writeStartObject()
                .write("firstName", "John")
                .write("age", 25)
                .writeStartObject("address")
                    .write("streetAddress", "21 2nd Street")
                .writeEnd()
                .writeStartArray("phoneNumber")
                    .writeStartObject().write("type", "home").writeEnd()
                    .writeStartObject().write("type", "mobile").writeEnd()
                    .writeStartObject().write("type", "work").writeEnd()
                .writeEnd()
            .writeEnd();
        generator.close();

        // Test with jsonpath
        final String json = Json.createReader(new StringReader(bos.toString())).readObject().toString();
        assertThat(json, hasJsonPath("age", equalTo(25)));
        assertThat(json, hasJsonPath("address.streetAddress", equalTo("21 2nd Street")));
        assertThat(json, hasJsonPath("phoneNumber[0].type", equalTo("home")));
        assertThat(json, hasJsonPath("phoneNumber[2].type", equalTo("work")));
    }
}
