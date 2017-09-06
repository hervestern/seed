/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.seedstack.seed.security.RequiresCRUD;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.security.RequiresRoles;
import org.seedstack.seed.security.internal.authorization.RequiresCRUDInterceptor;
import org.seedstack.seed.security.internal.authorization.RequiresPermissionsInterceptor;
import org.seedstack.seed.security.internal.authorization.RequiresRolesInterceptor;
import org.seedstack.seed.security.spi.CrudActionResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

class SecurityAopModule extends AbstractModule {

    private Set<CrudActionResolver> crudActionResolvers;

    SecurityAopModule(final Collection<Class<? extends CrudActionResolver>> crudActionResolverClasses) {
        Injector injector = Guice.createInjector(new CRUDResolverModule(crudActionResolverClasses));

        crudActionResolvers = crudActionResolverClasses
                .stream()
                .map(injector::getInstance)
                .collect(Collectors.toSet());
    }

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresRoles.class), new RequiresRolesInterceptor());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresPermissions.class),new RequiresPermissionsInterceptor());

        bindCRUDInterceptor();
    }

    private void bindCRUDInterceptor() {
        RequiresCRUDInterceptor requiresCRUDInterceptor = new RequiresCRUDInterceptor(
                crudActionResolvers);
        // Allows a single annotation at class level, or multiple annotations / one per method
        bindInterceptor(Matchers.annotatedWith(RequiresCRUD.class), Matchers.any(), requiresCRUDInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresCRUD.class), requiresCRUDInterceptor);
    }

    /**
     * Private Internal module that binds every CrudActionResolver received, so it
     * can be created trough Injector on its base package
     */
    private static class CRUDResolverModule extends AbstractModule {

        private final Collection<Class<? extends CrudActionResolver>> crudActionResolversClasses;

        public CRUDResolverModule(Collection<Class<? extends CrudActionResolver>> crudActionResolvers) {
            crudActionResolversClasses = crudActionResolvers;
        }

        @Override
        protected void configure() {
            crudActionResolversClasses.stream().forEach(x -> bind(x).asEagerSingleton());
        }

    }

}
