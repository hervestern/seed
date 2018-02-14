/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.core.internal.configuration.tool;

import java.io.PrintStream;
import java.util.Map;
import org.fusesource.jansi.Ansi;
import org.seedstack.shed.text.TextWrapper;

class DetailPrinter {
    private static final TextWrapper textWrapper = new TextWrapper(120);
    private final String baseFullName;
    private final PropertyInfo basePropertyInfo;

    DetailPrinter(String baseFullName, PropertyInfo basePropertyInfo) {
        this.baseFullName = baseFullName;
        this.basePropertyInfo = basePropertyInfo;
    }

    void printDetail(PrintStream stream) {
        Ansi ansi = Ansi.ansi();
        printDetails(ansi);
        stream.print(ansi.toString());
    }

    private void printDetails(Ansi ansi) {
        String indent = "  ";
        String[] split = baseFullName.split("\\.");
        for (int i = 0; i < split.length; i++) {
            ansi.a(split[i]).a(":");
            if (i < split.length - 1) {
                ansi.newline().a(indent);
            } else {
                ansi.a(" ");
            }
            indent += "  ";
        }
        ansi.a(String.valueOf(basePropertyInfo.getDefaultValue()));

        ansi
                .newline()
                .newline()
                .a("Inner properties")
                .newline()
                .a("----------------")
                .newline();

        for (Map.Entry<String, PropertyInfo> inner : basePropertyInfo.getInnerPropertyInfo().entrySet()) {

        }
    }
}
