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
import org.seedstack.seed.security.RequiresPermissions;

/**
 * Interceptor for the annotation RequiresPermissions
 */
public class RequiresPermissionsInterceptor extends AbstractInterceptor
    implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Annotation annotation = findAnnotation(invocation);
    if (annotation == null) {
      return invocation.proceed();
    }
    RequiresPermissions rpAnnotation = (RequiresPermissions) annotation;
    String[] perms = rpAnnotation.value();

    if (perms.length == 1) {
      this.checkPermission(perms[0]);
      return invocation.proceed();
    } else {
      boolean isAllowed = this.hasPermissions(perms, rpAnnotation.logical());

      if (!isAllowed) {
        if (Logical.OR.equals(rpAnnotation.logical())) {
          throw new AuthorizationException(
              "User does not have any of the permissions to access method "
                  + invocation.getMethod().toString());
        } else {
          throw new AuthorizationException(
              "Subject doesn't have permissions " + Arrays.toString(perms));
        }
      }

    }

    return invocation.proceed();
  }

  private Annotation findAnnotation(MethodInvocation invocation) {
    Annotation annotation = invocation.getMethod().getAnnotation(RequiresPermissions.class);
    if (annotation == null) {
      annotation = invocation.getThis().getClass().getAnnotation(RequiresPermissions.class);
    }
    return annotation;
  }
}
