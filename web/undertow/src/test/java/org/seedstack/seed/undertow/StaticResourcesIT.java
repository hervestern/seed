/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.undertow;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public class StaticResourcesIT {
    @Configuration("runtime.web.baseUrl")
    private String baseUrl;

    @Test
    public void classpathWebResourceWithDefaultConfigurationAreGzippedAndMinified() {
        RestAssured.expect()
                .statusCode(200)
                .header("Content-Encoding", Matchers.equalTo("gzip"))
                .body(Matchers.containsString("var minifiedJS = {};"))
                .when()
                .get(baseUrl + "/static/test.js");
    }

    @Test
    public void classpathWebResourceWithDefaultConfigurationAreGzippedOnTheFly() {
        RestAssured.expect()
                .statusCode(200)
                .header("Content-Encoding", Matchers.equalTo("gzip"))
                .body(Matchers.containsString("var JS2 = {};"))
                .when()
                .get(baseUrl + "/static/test2.js");
    }

    @Test
    public void classpathWebResource() {
        RestAssured.expect()
                .statusCode(200)
                .body(Matchers.containsString("var JS2 = {};"))
                .when()
                .get(baseUrl + "/static/test2.js");
    }

    @Test
    public void nonExistentResourceIs404() {
        RestAssured.expect().statusCode(404).when().get(baseUrl + "/non-existent-resource");
    }

    @Test
    public void notPregzippedResourceIsGzippedOnTheFlyTwice() {
        RestAssured.expect()
                .statusCode(200)
                .header("Content-Encoding", Matchers.equalTo("gzip"))
                .body(Matchers.containsString("var JS2 = {};"))
                .when()
                .get(baseUrl + "/static/test2.js");

        RestAssured.expect()
                .statusCode(200)
                .header("Content-Encoding", Matchers.equalTo("gzip"))
                .body(Matchers.containsString("var JS2 = {};"))
                .when()
                .get(baseUrl + "/static/test2.js");
    }
}
