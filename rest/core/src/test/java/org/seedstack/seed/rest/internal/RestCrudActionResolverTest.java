package org.seedstack.seed.rest.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("random"))).isFalse();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("head"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("options"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("post"))).isTrue();
        assertThat(resolverUnderTest.canResolve(Fixture.class.getMethod("put"))).isTrue();
    }

    @SuppressWarnings("unused")
    private static class Fixture {

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

    }

}
