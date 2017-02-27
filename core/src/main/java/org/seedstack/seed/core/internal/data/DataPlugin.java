/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.data;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.Context;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import org.kametic.specifications.Specification;
import org.seedstack.seed.DataConfig;
import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.DataManager;
import org.seedstack.seed.DataSet;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.spi.DataManagementProvider;
import org.seedstack.shed.ClassLoaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This plugin provides data import and export facilities.
 */
public class DataPlugin extends AbstractSeedPlugin implements DataManagementProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPlugin.class);
    private final Specification<Class<?>> dataExporterSpecification = and(classImplements(DataExporter.class), classAnnotatedWith(DataSet.class));
    private final Specification<Class<?>> dataImporterSpecification = and(classImplements(DataImporter.class), classAnnotatedWith(DataSet.class));
    private final Map<String, Map<String, DataExporterDefinition<?>>> allDataExporters = new ConcurrentHashMap<>();
    private final Map<String, Map<String, DataImporterDefinition<?>>> allDataImporters = new ConcurrentHashMap<>();
    private boolean loadInitializationData;
    private boolean forceInitializationData;
    @Inject
    private DataManager dataManager;

    @Override
    public String name() {
        return "data";
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder().specification(dataExporterSpecification).specification(dataImporterSpecification).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public InitState initialize(InitContext initContext) {
        DataConfig dataConfig = getConfiguration(DataConfig.class);

        switch (dataConfig.getImportMode()) {
            case FORCE:
                forceInitializationData = true;
                // falls through
            case AUTO:
                loadInitializationData = true;
                break;
        }

        Collection<Class<?>> scannedDataExporterClasses = initContext.scannedTypesBySpecification().get(dataExporterSpecification);
        for (Class<?> dataExporterClass : scannedDataExporterClasses) {
            if (DataExporter.class.isAssignableFrom(dataExporterClass)) {
                DataSet dataSet = dataExporterClass.getAnnotation(DataSet.class);
                Class exportedClass = getTypeParameter(dataExporterClass, DataExporter.class);
                if (exportedClass == null) {
                    throw SeedException.createNew(DataErrorCode.MISSING_TYPE_PARAMETER).put("class", dataExporterClass);
                }
                getGroupExporters(dataSet.group()).putIfAbsent(
                        dataSet.name(),
                        new DataExporterDefinition(
                                dataSet.name(),
                                dataSet.group(),
                                exportedClass,
                                dataExporterClass
                        )
                );
            }
        }

        Collection<Class<?>> scannedDataImporterClasses = initContext.scannedTypesBySpecification().get(dataImporterSpecification);
        for (Class<?> dataImporterClass : scannedDataImporterClasses) {
            if (DataImporter.class.isAssignableFrom(dataImporterClass)) {
                DataSet dataSet = dataImporterClass.getAnnotation(DataSet.class);
                Class importedClass = getTypeParameter(dataImporterClass, DataImporter.class);
                if (importedClass == null) {
                    throw SeedException.createNew(DataErrorCode.MISSING_TYPE_PARAMETER).put("class", dataImporterClass);
                }
                getGroupImporters(dataSet.group()).putIfAbsent(
                        dataSet.name(),
                        new DataImporterDefinition(
                                dataSet.name(),
                                dataSet.group(),
                                importedClass,
                                dataImporterClass
                        )
                );
            }
        }

        return InitState.INITIALIZED;
    }

    private Map<String, DataExporterDefinition<?>> getGroupExporters(String group) {
        Map<String, DataExporterDefinition<?>> nameDataExporterDefinitionMap = allDataExporters.get(group);
        if (nameDataExporterDefinitionMap == null) {
            allDataExporters.putIfAbsent(group, nameDataExporterDefinitionMap = new ConcurrentHashMap<>());
        }
        return nameDataExporterDefinitionMap;
    }

    private Map<String, DataImporterDefinition<?>> getGroupImporters(String group) {
        Map<String, DataImporterDefinition<?>> groupImporters = allDataImporters.get(group);
        if (groupImporters == null) {
            allDataImporters.putIfAbsent(group, groupImporters = new ConcurrentHashMap<>());
        }
        return groupImporters;
    }

    @Override
    public Object nativeUnitModule() {
        return new DataModule(Collections.unmodifiableMap(allDataExporters), Collections.unmodifiableMap(allDataImporters));
    }

    @Override
    public void start(Context context) {
        ClassLoader classLoader = ClassLoaders.findMostCompleteClassLoader(DataPlugin.class);

        if (loadInitializationData) {
            for (Map<String, DataImporterDefinition<?>> dataImporterDefinitionMap : allDataImporters.values()) {
                for (DataImporterDefinition<?> dataImporterDefinition : dataImporterDefinitionMap.values()) {
                    String dataPath = String.format("META-INF/data/%s/%s.json", dataImporterDefinition.getGroup(), dataImporterDefinition.getName());
                    InputStream dataStream = classLoader.getResourceAsStream(dataPath);

                    if (dataStream != null) {
                        if (!dataManager.isInitialized(dataImporterDefinition.getGroup(), dataImporterDefinition.getName()) || forceInitializationData) {
                            LOGGER.info("Importing initialization data for {}.{}", dataImporterDefinition.getGroup(), dataImporterDefinition.getName());
                            dataManager.importData(dataStream, dataImporterDefinition.getGroup(), dataImporterDefinition.getName(), true);
                        }

                        try {
                            dataStream.close();
                        } catch (IOException e) {
                            LOGGER.warn("Unable to close data resource " + dataPath, e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> void registerDataImporter(String group, String name, Class<DataImporter<T>> dataImporterClass) {
        if (getGroupImporters(group).putIfAbsent(
                name,
                new DataImporterDefinition<>(
                        name,
                        group,
                        getTypeParameter(dataImporterClass, DataImporter.class),
                        dataImporterClass
                )
        ) != null) {
            throw SeedException.createNew(DataErrorCode.ALREADY_EXISTING_IMPORTER)
                    .put("group", group)
                    .put("name", name);
        }
    }

    @Override
    public <T> void registerDataExporter(String group, String name, Class<DataExporter<T>> dataExporterClass) {
        if (getGroupExporters(group).putIfAbsent(
                name,
                new DataExporterDefinition<>(
                        name,
                        group,
                        getTypeParameter(dataExporterClass, DataExporter.class),
                        dataExporterClass
                )
        ) != null) {
            throw SeedException.createNew(DataErrorCode.ALREADY_EXISTING_EXPORTER)
                    .put("group", group)
                    .put("name", name);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getTypeParameter(Class<?> importerOrExporterClass, Class<?> genericInterface) {
        Class actualType = null;
        // Get all generic interfaces implemented by the scanned class
        Type[] genericInterfaces = importerOrExporterClass.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                Class anInterface = (Class) ((ParameterizedType) type).getRawType();
                // If the interface is the one get its type parameter
                if (genericInterface.isAssignableFrom(anInterface)) {
                    actualType = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                }
            }
        }
        return (Class<T>) actualType;
    }
}
