/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal.authorization;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.core.internal.guice.ProxyUtils;
import org.seedstack.seed.security.CrudAction;
import org.seedstack.seed.security.RequiresCrud;
import org.seedstack.seed.security.spi.CrudActionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class RequiresCrudInterceptor extends AbstractInterceptor implements MethodInterceptor {
    private Logger logger = LoggerFactory.getLogger(RequiresCrudInterceptor.class);
    @Inject
    private Set<CrudActionResolver> resolvers;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Annotation annotation = findAnnotation(invocation);

        if (annotation == null) {
            return invocation.proceed();
        }

        Optional<CrudAction> action = findVerb(invocation);
        if (!action.isPresent()) {
            logger.warn("RequiresCrud interceptor %s%s misses verb annotation", invocation.getThis().getClass().getName(), invocation.getMethod().getName());
            return invocation.proceed();
        }
        RequiresCrud rrAnnotation = (RequiresCrud) annotation;
        String permission = String.format("%s:%s", rrAnnotation.value(), action.get().getVerb());

        checkPermission(permission);
        return invocation.proceed();

    }

    private static Annotation findAnnotation(MethodInvocation invocation) {
        Annotation annotation = invocation.getMethod().getAnnotation(RequiresCrud.class);
        if (annotation == null) {
            // Annotation was not found on method, Checking class annotation
            annotation = ProxyUtils.cleanProxy(invocation.getThis().getClass())
                    .getAnnotation(RequiresCrud.class);
        }
        return annotation;
    }

    private Optional<CrudAction> findVerb(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        // returns the result of the first resolver that gives a valid action
        return resolvers.stream()
                .filter(x -> x.canResolve(method))
                .map(x -> x.resolve(method))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
