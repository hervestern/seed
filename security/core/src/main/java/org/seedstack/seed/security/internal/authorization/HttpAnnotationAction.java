/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal.authorization;

import static org.seedstack.seed.security.internal.authorization.RequiresRestInterceptor.CREATE_ACTION;
import static org.seedstack.seed.security.internal.authorization.RequiresRestInterceptor.DELETE_ACTION;
import static org.seedstack.seed.security.internal.authorization.RequiresRestInterceptor.NO_VERB_ACTION;
import static org.seedstack.seed.security.internal.authorization.RequiresRestInterceptor.READ_ACTION;
import static org.seedstack.seed.security.internal.authorization.RequiresRestInterceptor.UPDATE_ACTION;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom implementation to resolve Http request method </br>
 * Shiro  {@link org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter HttpMethodPermissionFilter} was taken as an example
 * 
 * @author xiabou
 *
 * @See {@link org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter}
 */
enum HttpAnnotationAction {
  DELETE(DELETE_ACTION), //
  GET(READ_ACTION), //
  HEAD(READ_ACTION), //
  NONE(NO_VERB_ACTION), //
  OPTIONS(READ_ACTION), //
  POST(CREATE_ACTION), //
  PUT(UPDATE_ACTION); //

  //Populate rest resolving dictionary
  private static final Map<String, HttpAnnotationAction> annotationMap;
  static {
    Map<String, HttpAnnotationAction> map = new HashMap<>();
    map.put(javax.ws.rs.DELETE.class.getSimpleName(), DELETE);
    map.put(javax.ws.rs.GET.class.getSimpleName(), GET);
    map.put(javax.ws.rs.HEAD.class.getSimpleName(), HEAD);
    map.put(javax.ws.rs.OPTIONS.class.getSimpleName(), OPTIONS);
    map.put(javax.ws.rs.POST.class.getSimpleName(), POST);
    map.put(javax.ws.rs.PUT.class.getSimpleName(), PUT);
    annotationMap = Collections.unmodifiableMap(map);

  }

  private final String action;

  private HttpAnnotationAction(String action) {
    this.action = action;
  }

  /***
   * Reads the annotation class and maps to a HTTP method, in case it can be done.
   * @param annotation
   * @return annotation verb, NONE in case of non javax.rs.ws annotation / null
   */
  public static HttpAnnotationAction computeFromAnnotation(Annotation annotation) {
    if(annotation==null) {
      return NONE;
    }
    return annotationMap.getOrDefault(annotation.annotationType().getSimpleName(), NONE);
  }

  //returns the verb of the action
  public String getAction() {
    return action;
  }
}
