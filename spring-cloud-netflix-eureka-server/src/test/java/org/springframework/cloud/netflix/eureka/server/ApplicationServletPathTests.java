/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.netflix.eureka.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.eureka.server.ApplicationServletPathTests.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = {
        "spring.application.name=eureka", "server.servletPath=/servlet",
        "management.security.enabled=false"})
public class ApplicationServletPathTests {

    @LocalServerPort
    private int port = 0;

    @Test
    public void catalogLoads() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/eureka/apps", Map.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void dashboardLoads() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/servlet/", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        String body = entity.getBody();
        // System.err.println(body);
        assertTrue(body.contains("eureka/js"));
        assertTrue(body.contains("eureka/css"));
        // The "DS Replicas"
        assertTrue(
                body.contains("<a href=\"http://localhost:8761/eureka/\">localhost</a>"));
    }

    @Test
    public void cssAvailable() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/servlet/eureka/css/wro.css",
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void jsAvailable() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/servlet/eureka/js/wro.js",
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void adminLoads() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/servlet/env", HttpMethod.GET,
                new HttpEntity<>("parameters", headers), Map.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEurekaServer
    protected static class Application {
        public static void main(String[] args) {
            new SpringApplicationBuilder(Application.class)
                    .properties("spring.application.name=eureka",
                            "server.servletPath=/servlet")
                    .run(args);
        }
    }

}
