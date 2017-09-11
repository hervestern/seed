/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import org.seedstack.seed.security.RequiresCrud;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.security.RequiresRoles;
import org.seedstack.seed.security.internal.authorization.RequiresCrudInterceptor;
import org.seedstack.seed.security.internal.authorization.RequiresPermissionsInterceptor;
import org.seedstack.seed.security.internal.authorization.RequiresRolesInterceptor;
import org.seedstack.seed.security.spi.CrudActionResolver;

import java.util.Collection;

class SecurityAopModule extends AbstractModule {
    private final Collection<Class<? extends CrudActionResolver>> crudActionResolverClasses;

    SecurityAopModule(final Collection<Class<? extends CrudActionResolver>> crudActionResolverClasses) {
        this.crudActionResolverClasses = crudActionResolverClasses;
    }

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresRoles.class), new RequiresRolesInterceptor());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresPermissions.class), new RequiresPermissionsInterceptor());
        bindCrudInterceptor();
    }

    private void bindCrudInterceptor() {
        Multibinder<CrudActionResolver> crudActionResolverMultibinder = Multibinder.newSetBinder(binder(), CrudActionResolver.class);
        for (Class<? extends CrudActionResolver> crudActionResolverClass : crudActionResolverClasses) {
            crudActionResolverMultibinder.addBinding().to(crudActionResolverClass);
        }

        RequiresCrudInterceptor requiresCrudInterceptor = new RequiresCrudInterceptor();
        requestInjection(requiresCrudInterceptor);

        // Allows a single annotation at class level, or multiple annotations / one per method
        bindInterceptor(Matchers.annotatedWith(RequiresCrud.class), Matchers.any(), requiresCrudInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresCrud.class), requiresCrudInterceptor);
    }
}
