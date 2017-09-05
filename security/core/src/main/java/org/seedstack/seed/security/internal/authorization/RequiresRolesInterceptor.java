/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal.authorization;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.security.AuthorizationException;
import org.seedstack.seed.security.Logical;
import org.seedstack.seed.security.RequiresRoles;

/**
 * Interceptor for annotation RequiresRoles
 */
public class RequiresRolesInterceptor extends AbstractInterceptor implements MethodInterceptor {

  /**
   * Constructor
   * 
   */
  public RequiresRolesInterceptor() {
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Annotation annotation = findAnnotation(invocation);
    if (annotation == null) {
      return invocation.proceed();
    }
    RequiresRoles rrAnnotation = (RequiresRoles) annotation;
    String[] roles = rrAnnotation.value();
    if (roles.length == 1) {
      this.checkRole(roles[0]);
      return invocation.proceed();
    } else {
      boolean isAllowed = this.hasRoles(roles, rrAnnotation.logical());

      if (!isAllowed) {
        if (Logical.OR.equals(rrAnnotation.logical())) {
          throw new AuthorizationException(
              "User does not have any of the roles to access method "
                  + invocation.getMethod().toString());
        } else {
          throw new AuthorizationException(
              "Subject doesn't have roles " + Arrays.toString(roles));
        }
      }

    }

    return invocation.proceed();
  }

  private Annotation findAnnotation(MethodInvocation invocation) {
    Annotation annotation = invocation.getMethod().getAnnotation(RequiresRoles.class);
    if (annotation == null) {
      annotation = invocation.getThis().getClass().getAnnotation(RequiresRoles.class);
    }
    return annotation;
  }
}
