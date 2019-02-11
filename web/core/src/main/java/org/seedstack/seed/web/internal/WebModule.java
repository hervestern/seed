/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.web.internal;

import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.seedstack.seed.web.WebResourceResolver;

class WebModule extends ServletModule {
    private final WebResourceResolverProvider webResourceResolverProvider;

    WebModule(ServletContext servletContext) {
        webResourceResolverProvider = new WebResourceResolverProvider(servletContext);
    }

    @Override
    protected void configureServlets() {
        bind(GuiceFilter.class).in(Scopes.SINGLETON);
        bind(WebResourceResolver.class).toProvider(webResourceResolverProvider);
        install(new FactoryModuleBuilder()
                .implement(WebResourceResolver.class, WebResourcesResolverImpl.class)
                .build(WebResourceResolverFactory.class));
    }

    private static class WebResourceResolverProvider implements Provider<WebResourceResolver> {
        private final ServletContext servletContext;
        @Inject
        private WebResourceResolverFactory webResourceResolverFactory;

        WebResourceResolverProvider(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public WebResourceResolver get() {
            return webResourceResolverFactory.createWebResourceResolver(servletContext);
        }
    }
}
