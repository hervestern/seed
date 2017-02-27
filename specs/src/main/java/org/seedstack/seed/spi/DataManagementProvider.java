/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.spi;

import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataImporter;

public interface DataManagementProvider {
    /**
     * Programmatically registers a data importer.
     *
     * @param group             the data set group.
     * @param name              the data set name.
     * @param dataImporterClass the data importer class.
     */
    <T> void registerDataImporter(String group, String name, Class<DataImporter<T>> dataImporterClass);

    /**
     * Programmatically registers a data exporter.
     *
     * @param group             the data set group.
     * @param name              the data set name.
     * @param dataExporterClass the data exporter class.
     */
    <T> void registerDataExporter(String group, String name, Class<DataExporter<T>> dataExporterClass);
}
