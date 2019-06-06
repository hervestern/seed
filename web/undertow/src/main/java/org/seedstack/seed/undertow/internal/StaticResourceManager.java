package org.seedstack.seed.undertow.internal;

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import java.io.IOException;
import org.seedstack.seed.web.WebConfig;
import org.seedstack.shed.ClassLoaders;

public class StaticResourceManager implements ResourceManager {
    private static final String META_INF_RESOURCES = "META-INF/resources";
    private static final ClassLoader classLoader =
            ClassLoaders.findMostCompleteClassLoader(StaticResourceManager.class);
    private final ResourceManager resourceManager;
    private final String[] extensionsToTry;

    public StaticResourceManager(WebConfig.ServerConfig.StaticResourcesConfig staticResourcesConfig) {
        if (staticResourcesConfig.isEnabled()) {
            resourceManager = new ClassPathResourceManager(classLoader, META_INF_RESOURCES);
        } else {
            resourceManager = ResourceManager.EMPTY_RESOURCE_MANAGER;
        }
        computeExtensionsToTry()
        this.staticResourcesConfig = staticResourcesConfig;
    }

    @Override
    public Resource getResource(String path) throws IOException {
        Resource found = null;
        boolean isMinified = false;
        boolean isCompressed = false;
        if (staticResourcesConfig.isServeMinified()) {
            if (staticResourcesConfig.isServeCompressed()) {
                found = resourceManager.getResource(path + ".min.gz");
                isMinified = isCompressed = true;
            }
            if (found == null) {
                found = resourceManager.getResource(path + ".min");
                isMinified = true;
            }
        } else {

        }
        return resourceManager.getResource(path);
    }

    public Resource getCompressedResource(String path) throws IOException {

    }

    public Resource getCompressedResource(String path) throws IOException {
        Resource found = null;
        if (staticResourcesConfig.isServeCompressed()) {
            found = resourceManager.getResource(path + ".gz");
        }
        if (found == null) {
            found = resourceManager.getResource(path);
        }
        return found;
    }

    @Override
    public boolean isResourceChangeListenerSupported() {
        return resourceManager.isResourceChangeListenerSupported();
    }

    @Override
    public void registerResourceChangeListener(ResourceChangeListener listener) {
        resourceManager.registerResourceChangeListener(listener);
    }

    @Override
    public void removeResourceChangeListener(ResourceChangeListener listener) {
        resourceManager.removeResourceChangeListener(listener);
    }

    @Override
    public void close() throws IOException {
        resourceManager.close();
    }
}
