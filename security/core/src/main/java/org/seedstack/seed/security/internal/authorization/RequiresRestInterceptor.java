/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal.authorization;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.security.RequiresRest;
import org.seedstack.seed.security.SecuritySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequiresRestInterceptor implements MethodInterceptor {

  private SecuritySupport securitySupport;

  private Logger logger = LoggerFactory.getLogger(RequiresRestInterceptor.class);

  // Actions representing HTTP Method values (GET -> read, POST -> create, etc)
  static final String CREATE_ACTION = "create";
  static final String DELETE_ACTION = "delete";
  static final String NO_VERB_ACTION = "";
  static final String READ_ACTION = "read";
  static final String UPDATE_ACTION = "update";

  /**
   * Constructor
   * 
   * @param securitySupport
   *          the security support
   */
  public RequiresRestInterceptor(SecuritySupport securitySupport) {
    this.securitySupport = securitySupport;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Annotation annotation = findAnnotation(invocation);

    if (annotation == null) {
      return invocation.proceed();
    }
    HttpAnnotationAction verb = findHttpVerb(invocation);
    if (verb.equals(HttpAnnotationAction.NONE)) {
      logger.warn("RequiresRest filter %s%s misses verb annotation",
          invocation.getThis().getClass().getName(), invocation.getMethod().getName());
      return invocation.proceed();
    }
    RequiresRest rrAnnotation = (RequiresRest) annotation;
    String permission = String.format("%s:%s", rrAnnotation.value(), verb.getAction());
    securitySupport.checkPermission(permission);
    return invocation.proceed();

  }

  private static Annotation findAnnotation(MethodInvocation invocation) {
    Annotation annotation = invocation.getMethod().getAnnotation(RequiresRest.class);
    if (annotation == null) {
      /***
       * Annotation was not found on method, Checking class annotation <br />
       * Parent class should be checked as guice generates an $$EnhancerByGuice... child version
       * {@see https://stackoverflow.com/a/13406792}
       */
      annotation = invocation.getThis().getClass().getSuperclass()
          .getAnnotation(RequiresRest.class);
    }
    return annotation;
  }

  private static HttpAnnotationAction findHttpVerb(MethodInvocation invocation) {

    Annotation[] annotations = invocation.getMethod().getAnnotations();
    if (annotations == null || annotations.length == 0) {
      return HttpAnnotationAction.NONE;
    }

    for (Annotation annotation : annotations) {
      HttpAnnotationAction annotationVerb = HttpAnnotationAction.computeFromAnnotation(annotation);
      if (annotationVerb != HttpAnnotationAction.NONE) {
        return annotationVerb;
      }
    }
    return HttpAnnotationAction.NONE;
  }

}
