/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.undertow.internal;

import static org.seedstack.shed.ClassLoaders.findMostCompleteClassLoader;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ErrorPage;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import org.seedstack.coffig.Coffig;
import org.seedstack.seed.ApplicationConfig;
import org.seedstack.seed.undertow.UndertowConfig;
import org.seedstack.seed.web.WebConfig;
import org.xnio.XnioWorker;

class DeploymentManagerFactory {
    private static final String META_INF_RESOURCES = "META-INF/resources";
    private final ClassLoader mostCompleteClassLoader = findMostCompleteClassLoader(DeploymentManagerFactory.class);
    private final XnioWorker xnioWorker;
    private final UndertowConfig undertowConfig;
    private final ApplicationConfig applicationConfig;
    private final WebConfig.ServerConfig serverConfig;
    private final Map<String, String> initParameters;

    DeploymentManagerFactory(XnioWorker xnioWorker, Coffig configuration, Map<String, String> initParameters) {
        this.xnioWorker = xnioWorker;
        this.undertowConfig = configuration.get(UndertowConfig.class);
        this.applicationConfig = configuration.get(ApplicationConfig.class);
        this.serverConfig = configuration.get(WebConfig.ServerConfig.class);
        this.initParameters = initParameters;
    }

    DeploymentManager createDeploymentManager() {
        DeploymentInfo servletBuilder = configureDeploymentInfo();
        return Servlets.defaultContainer().addDeployment(servletBuilder);
    }

    private DeploymentInfo configureDeploymentInfo() {
        DeploymentInfo deploymentInfo = Servlets.deployment()
                .setEagerFilterInit(true)
                .setClassLoader(mostCompleteClassLoader)
                .setDeploymentName(applicationConfig.getId())
                .setDisplayName(applicationConfig.getName())
                .setDefaultSessionTimeout(serverConfig.sessions().getTimeout())
                .setResourceManager(new ClassPathResourceManager(mostCompleteClassLoader, META_INF_RESOURCES))
                .addWelcomePages(serverConfig.getWelcomeFiles())
                .addErrorPages(buildUndertowErrorPages(serverConfig.getErrorPages()))
                .addServletContextAttribute(
                        WebSocketDeploymentInfo.ATTRIBUTE_NAME,
                        new WebSocketDeploymentInfo()
                                .setBuffers(new DefaultByteBufferPool(
                                        undertowConfig.isDirectBuffers(),
                                        undertowConfig.getBufferSize()))
                                .setWorker(xnioWorker)
                )
                .setContextPath(serverConfig.getContextPath());

        for (Map.Entry<String, String> initParameter : initParameters.entrySet()) {
            deploymentInfo.addInitParameter(initParameter.getKey(), initParameter.getValue());
        }

        for (ServletContainerInitializer servletContainerInitializer : loadServletContainerInitializers()) {
            deploymentInfo.addServletContainerInitializer(
                    createServletContainerInitializerInfo(servletContainerInitializer));
        }

        return deploymentInfo;
    }

    private Collection<ErrorPage> buildUndertowErrorPages(List<WebConfig.ServerConfig.ErrorPage> errorPages) {
        List<ErrorPage> undertowErrorPages = new ArrayList<>();
        for (WebConfig.ServerConfig.ErrorPage errorPage : errorPages) {
            String location = errorPage.getLocation();
            if (!location.startsWith("/")) {
                location = "/" + location;
            }
            if (errorPage.getExceptionType() != null) {
                undertowErrorPages.add(new ErrorPage(location, errorPage.getExceptionType()));
            } else if (errorPage.getErrorCode() != null) {
                undertowErrorPages.add(new ErrorPage(location, errorPage.getErrorCode()));
            } else {
                undertowErrorPages.add(new ErrorPage(location));
            }
        }
        return undertowErrorPages;
    }

    private <T extends ServletContainerInitializer> ServletContainerInitializerInfo
    createServletContainerInitializerInfo(
            final T servletContainerInitializer) {
        return new ServletContainerInitializerInfo(servletContainerInitializer.getClass(),
                () -> new ImmediateInstanceHandle<>(servletContainerInitializer), null);
    }

    private Set<ServletContainerInitializer> loadServletContainerInitializers() {
        Set<ServletContainerInitializer> servletContainerInitializers = new HashSet<>();
        for (ServletContainerInitializer servletContainerInitializer : ServiceLoader.load(
                ServletContainerInitializer.class, mostCompleteClassLoader)) {
            servletContainerInitializers.add(servletContainerInitializer);
        }
        return servletContainerInitializers;
    }
}
