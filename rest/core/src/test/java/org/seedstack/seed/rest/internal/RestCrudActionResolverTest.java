/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.rest.internal;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.security.CRUDAction;

public class RestCrudActionResolverTest {

    private RestCrudActionResolver resolverUnderTest;

    @Before
    public void setup() throws Exception {
        resolverUnderTest = new RestCrudActionResolver();

    }

    @Test
    public void test_that_only_intercepts_rest_annotations() throws Exception {
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("delete"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("get"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("head"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("options"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("post"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("put"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("none"))).isFalse();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("random"))).isFalse();
    }

    @Test
    public void test_that_resolves_to_the_right_verb() throws Exception {

        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("delete"))).isPresent().contains(CRUDAction.DELETE);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("get"))).isPresent().contains(CRUDAction.READ);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("head"))).isPresent().contains(CRUDAction.READ);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("options"))).isPresent().contains(CRUDAction.READ);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("post"))).isPresent().contains(CRUDAction.CREATE);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("put"))).isPresent().contains(CRUDAction.UPDATE);
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("none"))).isNotPresent();
        assertThat(resolverUnderTest.resolve(Fixture.class.getMethod("random"))).isNotPresent();

    }

    // Test Fixture
    public static class Fixture {

        @DELETE
        public void delete() {

        }

        @GET
        public void get() {

        }

        @Deprecated
        public void random() {

        }

        @HEAD
        public void head() {
        }

        @OPTIONS
        public void options() {

        }

        @POST
        public void post() {

        }

        @PUT
        public void put() {

        }

        public void none() {

        }

    }

}
