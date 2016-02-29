/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.application;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.core.AbstractPlugin;
import org.apache.commons.configuration.Configuration;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.CorePlugin;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.Collection;

/**
 * Plugin that initializes the application identity, storage location and configuration.
 *
 * @author adrien.lauer@mpsa.com
 */
public class ApplicationPlugin extends AbstractPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPlugin.class);

    private final ApplicationDiagnosticCollector applicationDiagnosticCollector = new ApplicationDiagnosticCollector();
    private Application application;

    @Override
    public String name() {
        return "application";
    }

    @Override
    public Collection<Class<?>> requiredPlugins() {
        return Lists.<Class<?>>newArrayList(CorePlugin.class, ConfigurationProvider.class);
    }

    @Override
    public InitState init(InitContext initContext) {
        ConfigurationProvider configurationProvider = initContext.dependency(ConfigurationProvider.class);
        Configuration configuration = configurationProvider.getConfiguration();

        Configuration coreConfiguration = configuration.subset(CorePlugin.CORE_PLUGIN_PREFIX);
        ApplicationInfo applicationInfo = buildApplicationInfo(coreConfiguration);
        File seedStorage = setupApplicationStorage(coreConfiguration);
        configureJUL(coreConfiguration);

        application = new ApplicationImpl(applicationInfo, configurationProvider, seedStorage);

        initContext.dependency(CorePlugin.class).registerDiagnosticCollector("org.seedstack.seed.core.application", applicationDiagnosticCollector);

        return InitState.INITIALIZED;
    }

    private ApplicationInfo buildApplicationInfo(Configuration coreConfiguration) {
        ApplicationInfo applicationInfo = new ApplicationInfo();

        String appId = coreConfiguration.getString("application-id");
        if (appId == null || appId.isEmpty()) {
            throw SeedException.createNew(ApplicationErrorCode.MISSING_APPLICATION_IDENTIFIER).put("property",
                    CorePlugin.CORE_PLUGIN_PREFIX + ".application-id");
        }
        applicationInfo.setAppId(appId);

        String appName = coreConfiguration.getString("application-name");
        if (appName == null) {
            appName = appId;
        }
        applicationInfo.setAppName(appName);

        String appVersion = coreConfiguration.getString("application-version");
        if (appVersion == null) {
            appVersion = "1.0.0";
        }
        applicationInfo.setAppVersion(appVersion);

        LOGGER.info("Application info: {}", applicationInfo);
        applicationDiagnosticCollector.setApplicationInfo(applicationInfo);

        return applicationInfo;
    }

    private File setupApplicationStorage(Configuration coreConfiguration) {
        String storageLocation = coreConfiguration.getString("storage");

        if (storageLocation != null) {
            File seedDirectory = new File(storageLocation);

            if (!seedDirectory.exists() && !seedDirectory.mkdirs()) {
                throw SeedException.createNew(ApplicationErrorCode.UNABLE_TO_CREATE_STORAGE_DIRECTORY).put("path", seedDirectory.getAbsolutePath());
            }

            if (!seedDirectory.isDirectory()) {
                throw SeedException.createNew(ApplicationErrorCode.STORAGE_PATH_IS_NOT_A_DIRECTORY).put("path", seedDirectory.getAbsolutePath());
            }

            if (!seedDirectory.canWrite()) {
                throw SeedException.createNew(ApplicationErrorCode.STORAGE_DIRECTORY_IS_NOT_WRITABLE).put("path", seedDirectory.getAbsolutePath());
            }

            LOGGER.info("Application local storage at {}", seedDirectory.getAbsolutePath());
            applicationDiagnosticCollector.setStorageLocation(seedDirectory.getAbsolutePath());

            return seedDirectory;
        }

        return null;
    }

    private void configureJUL(Configuration coreConfiguration) {
        if (coreConfiguration.getBoolean("redirect-jul", true)) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();

            LOGGER.debug("Java logging to SLF4J redirection enabled, if you're using logback be sure to have a LevelChangePropagator in your configuration");
        }
    }

    @Override
    public Object nativeUnitModule() {
        return new ApplicationModule(this.application);
    }

    /**
     * Retrieve the application object.
     *
     * @return the application object.
     */
    public Application getApplication() {
        return this.application;
    }
}
