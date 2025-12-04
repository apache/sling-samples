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
package org.apache.sling.samples.nashorn;

import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/nashorn-test")
public class NashornTestServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ScriptEngineManager scriptEngineManager;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        try {
            ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
            
            if (engine == null) {
                response.getWriter().println("Error: Nashorn scripting engine not found!");
                response.getWriter().println("Available engines:");
                scriptEngineManager.getEngineFactories().forEach(factory -> {
                    try {
                        response.getWriter().println("  - " + factory.getEngineName() 
                            + " (names: " + factory.getNames() + ")");
                    } catch (IOException e) {
                        // ignore
                    }
                });
                return;
            }
            
            // Sample JavaScript to evaluate
            String script = "var message = 'Hello from Nashorn!';\n" +
                          "var numbers = [1, 2, 3, 4, 5];\n" +
                          "var sum = numbers.reduce(function(a, b) { return a + b; }, 0);\n" +
                          "message + ' Sum of ' + numbers + ' is ' + sum;";
            
            // Evaluate the script
            Object result = engine.eval(script);
            
            // Output the result
            response.getWriter().println("Nashorn Scripting Engine Test");
            response.getWriter().println("=============================");
            response.getWriter().println();
            response.getWriter().println("Engine: " + engine.getFactory().getEngineName() + " " 
                + engine.getFactory().getEngineVersion());
            response.getWriter().println("Language: " + engine.getFactory().getLanguageName() + " " 
                + engine.getFactory().getLanguageVersion());
            response.getWriter().println();
            response.getWriter().println("Script executed:");
            response.getWriter().println(script);
            response.getWriter().println();
            response.getWriter().println("Result:");
            response.getWriter().println(result);
            
        } catch (ScriptException e) {
            throw new ServletException(e);
        }
    }
}
