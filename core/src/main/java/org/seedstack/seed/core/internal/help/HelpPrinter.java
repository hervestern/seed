/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.core.internal.help;

import java.io.PrintStream;
import java.util.Map;
import org.fusesource.jansi.Ansi;
import org.seedstack.seed.spi.SeedTool;

class HelpPrinter {
    private static final String INDENTATION = "  ";
    private final SeedTool seedTool;

    HelpPrinter(SeedTool seedTool) {
        this.seedTool = seedTool;
    }

    void print(PrintStream stream) {
        Ansi ansi = new Ansi();
        stream.print(ansi.toString());
    }

    static void printToolList(PrintStream stream, Map<String, SeedTool> tools) {
        Ansi ansi = new Ansi();

        tools.values().forEach((seedTool) -> {
            ansi
                    .fgBright(Ansi.Color.BLUE)
                    .a(String.format("%-20s", seedTool.toolName()))
                    .reset()
                    .a(seedTool.description())
                    .a(seedTool.description().endsWith(".") ? "" : ".")
                    .newline();
        });

        stream.print(ansi.toString());
    }
}
