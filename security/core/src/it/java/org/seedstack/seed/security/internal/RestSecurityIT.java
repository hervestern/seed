/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.Install;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.security.AuthorizationException;
import org.seedstack.seed.security.SecuritySupport;
import org.seedstack.seed.security.WithUser;
import org.seedstack.seed.security.internal.fixtures.AnnotatedRestClass4Security;
import org.seedstack.seed.security.internal.fixtures.AnnotatedRestMethods4Security;

import com.google.inject.AbstractModule;

/**
 * @author xiabou
 *
 */
@RunWith(SeedITRunner.class)
public class RestSecurityIT {

  @Inject
  private AnnotatedRestClass4Security annotatedClass;

  @Inject
  private AnnotatedRestMethods4Security annotatedMethods;

  @Inject
  private SecuritySupport securitySupport;

  @Test
  @WithUser(id = "Obiwan", password = "yodarulez")
  public void Obiwan_should_not_be_able_to_interpret_anything() {
    assertThatThrownBy(() -> annotatedClass.restGet())
        .isInstanceOf(AuthorizationException.class);
    assertThatThrownBy(() -> annotatedMethods.restGet())
        .isInstanceOf(AuthorizationException.class);

  }

  // RESTBOT
  @Test
  @WithUser(id = "R2D2", password = "beep")
  public void r2d2_should_be_able_to_be_a_restbot() {
    // Delete jabba!
    assertThat(SecurityUtils.getSubject().isPermitted("jabba:delete")).isTrue();
    assertThat(securitySupport.isPermitted("jabba:delete")).isTrue();

    // Update c3p0!
    assertThat(SecurityUtils.getSubject().isPermitted("c3p0:update")).isTrue();
    assertThat(securitySupport.isPermitted("c3p0:update")).isTrue();

    // Create X-Wing
    assertThat(SecurityUtils.getSubject().isPermitted("xwing:create")).isTrue();
    assertThat(securitySupport.isPermitted("xwing:create")).isTrue();

    // Read chewaka
    assertThat(SecurityUtils.getSubject().isPermitted("chewaka:read")).isTrue();
    assertThat(securitySupport.isPermitted("chewaka:read")).isTrue();

    // is a restbot
    assertThat(SecurityUtils.getSubject().hasRole("restbot")).isTrue();
    assertThat(securitySupport.hasRole("restbot")).isTrue();
  }

  @Test
  @WithUser(id = "R2D2", password = "beep")
  public void r2d2_should_be_able_to_call_any_kind_of_method() {

    assertThat(annotatedClass.restDelete()).isTrue();
    assertThat(annotatedClass.restGet()).isTrue();
    assertThat(annotatedClass.restHead()).isTrue();
    assertThat(annotatedClass.restOptions()).isTrue();
    assertThat(annotatedClass.restPost()).isTrue();
    assertThat(annotatedClass.restPut()).isTrue();
    assertThat(annotatedClass.restEmpty()).isTrue();

    assertThat(annotatedMethods.restDelete()).isTrue();
    assertThat(annotatedMethods.restGet()).isTrue();
    assertThat(annotatedMethods.restHead()).isTrue();
    assertThat(annotatedMethods.restOptions()).isTrue();
    assertThat(annotatedMethods.restPost()).isTrue();
    assertThat(annotatedMethods.restPut()).isTrue();
    assertThat(annotatedMethods.restEmpty()).isTrue();

  }

  // INTERPRETER
  @Test
  @WithUser(id = "C3P0", password = "ewokgod")
  public void c3p0_should_only_be_able_to_read_rest_as_interpreter() {
    // Read ewoks
    assertThat(SecurityUtils.getSubject().isPermitted("ewok:read")).isTrue();
    assertThat(securitySupport.isPermitted("ewok:read")).isTrue();

    // Should not be able to update itself
    assertThat(SecurityUtils.getSubject().isPermitted("c3p0:update")).isFalse();
    assertThat(securitySupport.isPermitted("c3p0:update")).isFalse();

    // Is an interpreter
    assertThat(SecurityUtils.getSubject().hasRole("interpreter")).isTrue();
    assertThat(securitySupport.hasRole("interpreter")).isTrue();
  }

  @Test
  @WithUser(id = "C3P0", password = "ewokgod")
  public void c3p0_should_be_able_to_read_data() {

    assertThatThrownBy(() -> annotatedMethods.restDelete()).isInstanceOf(AuthorizationException.class);
    assertThatThrownBy(() -> annotatedMethods.restPost()).isInstanceOf(AuthorizationException.class);
    assertThatThrownBy(() -> annotatedMethods.restPut()).isInstanceOf(AuthorizationException.class);
    assertThat(annotatedMethods.restGet()).isTrue();
    assertThat(annotatedMethods.restHead()).isTrue();
    assertThat(annotatedMethods.restOptions()).isTrue();
    assertThat(annotatedMethods.restEmpty()).isTrue();

    assertThatThrownBy(() -> annotatedClass.restDelete()).isInstanceOf(AuthorizationException.class);
    assertThatThrownBy(() -> annotatedClass.restPost()).isInstanceOf(AuthorizationException.class);
    assertThatThrownBy(() -> annotatedClass.restPut()).isInstanceOf(AuthorizationException.class);
    assertThat(annotatedClass.restGet()).isTrue();
    assertThat(annotatedClass.restHead()).isTrue();
    assertThat(annotatedClass.restOptions()).isTrue();
    assertThat(annotatedClass.restEmpty()).isTrue();

  }

  @Install
  public static class SecurityTestModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(AnnotatedRestClass4Security.class);
      bind(AnnotatedRestMethods4Security.class);
    }

  }

}
