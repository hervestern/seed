/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.web.internal;

import com.google.inject.servlet.GuiceFilter;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.web.ServletContextConfigurer;
import org.seedstack.seed.web.listener.SeedServletContextListener;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

@HandlesTypes(ServletContextConfigurer.class)
public class SeedServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> servletContextConfigurerClasses, ServletContext servletContext) throws ServletException {
        configureGuiceFilter(servletContext);

        servletContext.addListener(SeedServletContextListener.class);

        if (servletContextConfigurerClasses != null) {
            executeServletContextConfigurers(servletContextConfigurerClasses, servletContext);
        }
    }

    private void executeServletContextConfigurers(Set<Class<?>> servletContextConfigurerClasses, ServletContext servletContext) {
        Set<ServletContextConfigurer> servletContextConfigurers = new HashSet<ServletContextConfigurer>();

        for (Class<?> servletConfigurerClass : servletContextConfigurerClasses) {
            if (isServletContextConfigurerImpl(servletConfigurerClass)) {
                try {
                    servletContextConfigurers.add((ServletContextConfigurer) servletConfigurerClass.newInstance());
                } catch (Exception e) {
                    throw SeedException.wrap(e, WebErrorCode.UNEXPECTED_EXCEPTION);
                }
            }
        }

        servletContext.log("ServletContext configurers detected: " + servletContextConfigurerClasses);

        for (ServletContextConfigurer servletContextConfigurer : servletContextConfigurers) {
            try {
                servletContextConfigurer.configure(servletContext);
            } catch (Exception e) {
                throw SeedException.wrap(e, WebErrorCode.UNEXPECTED_EXCEPTION);
            }
        }
    }

    private boolean isServletContextConfigurerImpl(Class<?> servletConfigurerClass) {
        return !servletConfigurerClass.isInterface() && !Modifier.isAbstract(servletConfigurerClass.getModifiers()) && ServletContextConfigurer.class.isAssignableFrom(servletConfigurerClass);
    }

    private void configureGuiceFilter(ServletContext ctx) {
        boolean guiceFilterAlreadyRegistered = false;

        for (FilterRegistration filterRegistration : ctx.getFilterRegistrations().values()) {
            if (GuiceFilter.class.getName().equals(filterRegistration.getClassName())) {
                guiceFilterAlreadyRegistered = true;
                break;
            }
        }

        if (!guiceFilterAlreadyRegistered) {
            FilterRegistration.Dynamic guiceFilter = ctx.addFilter("guiceFilter", GuiceFilter.class);
            if (guiceFilter != null) {
                guiceFilter.addMappingForUrlPatterns(null, false, "/*");
            }
        } else {
            ctx.log("Guice filter already registered, avoiding automatic registration");
        }
    }
}
