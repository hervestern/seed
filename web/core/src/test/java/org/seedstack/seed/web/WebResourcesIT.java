/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.net.URL;
import javax.inject.Inject;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebResourcesIT {
    @ArquillianResource
    private URL baseUrl;
    @Inject
    private WebResourceResolver webResourceResolver;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(ShrinkWrap.create(JavaArchive.class)
                        .addAsResource("test.js", "META-INF/resources/test.js")
                );
    }

    @Test
    @RunAsClient
    public void staticResourcesAreServedFromJars() {
        RestAssured.expect()
                .statusCode(200)
                .body(Matchers.containsString("var JS = {};"))
                .when()
                .get(baseUrl + "test.js");
    }

    @Test
    @RunAsClient
    public void nonExistentResourceIs404() {
        RestAssured.expect().statusCode(404).when().get(baseUrl + "non-existent-resource");
    }

    @Test
    @RunAsClient
    public void webResourceResolver() {
        assertThat(webResourceResolver.resolveResourceInfo(new ResourceRequest("test.js"))
                .getUrl().toExternalForm()).endsWith("/test.js");
    }
}
