/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.data;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.DataManager;

import java.util.Map;

class DataModule extends PrivateModule {
    private final Map<String, Map<String, DataExporterDefinition<?>>> allDataExporters;
    private final Map<String, Map<String, DataImporterDefinition<?>>> allDataImporters;

    DataModule(Map<String, Map<String, DataExporterDefinition<?>>> allDataExporters, Map<String, Map<String, DataImporterDefinition<?>>> allDataImporters) {
        this.allDataExporters = allDataExporters;
        this.allDataImporters = allDataImporters;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<String, Map<String, DataExporterDefinition<?>>>>() {
        }).toInstance(allDataExporters);
        bind(new TypeLiteral<Map<String, Map<String, DataImporterDefinition<?>>>>() {
        }).toInstance(allDataImporters);

        bind(DataManager.class).to(DataManagerImpl.class);

        // Bind importers
        for (Map<String, DataImporterDefinition<?>> dataImporterDefinitionMap : allDataImporters.values()) {
            for (DataImporterDefinition<?> dataImporterDefinition : dataImporterDefinitionMap.values()) {
                bindImporter(dataImporterDefinition);
            }
        }

        // Bind exporters
        for (Map<String, DataExporterDefinition<?>> dataExporterDefinitionMap : allDataExporters.values()) {
            for (DataExporterDefinition<?> dataExporterDefinition : dataExporterDefinitionMap.values()) {
                bindExporter(dataExporterDefinition);
            }
        }

        expose(DataManager.class);
    }

    @SuppressWarnings("unchecked")
    private <T> void bindImporter(DataImporterDefinition<T> dataImporterDefinition) {
        bind(Key.get((TypeLiteral<DataImporter<T>>) TypeLiteral.get(Types.newParameterizedType(DataImporter.class, dataImporterDefinition.getImportedClass()))))
                .to(dataImporterDefinition.getDataImporterClass());
    }

    @SuppressWarnings("unchecked")
    private <T> void bindExporter(DataExporterDefinition<T> dataExporterDefinition) {
        bind(Key.get((TypeLiteral<DataExporter<T>>) TypeLiteral.get(Types.newParameterizedType(DataExporter.class, dataExporterDefinition.getExportedClass()))))
                .to(dataExporterDefinition.getDataExporterClass());
    }
}
