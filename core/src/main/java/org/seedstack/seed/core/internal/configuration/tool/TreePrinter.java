/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.core.internal.configuration.tool;

import java.io.PrintStream;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderer;

class TreePrinter {
    private static final String INDENTATION = "  ";
    private final Node node;

    TreePrinter(Node node) {
        this.node = node;
    }

    void printTree(PrintStream stream) {
        Ansi ansi = Ansi.ansi();
        printTree(node, "", ansi);
        stream.print(ansi.toString());
    }

    private void printTree(Node node, String leftPadding, Ansi ansi) {
        if (!node.isRootNode()) {
            ansi
                    .a(leftPadding)
                    .fg(Ansi.Color.YELLOW).a(node.getName()).reset()
                    .a(":")
                    .newline();
            for (PropertyInfo propertyInfo : node.getPropertyInfo()) {
                printProperty(propertyInfo, leftPadding, ansi);
            }
        }
        for (Node child : node.getChildren()) {
            printTree(child, leftPadding + (node.isRootNode() ? "" : INDENTATION), ansi);
        }
    }

    private void printProperty(PropertyInfo propertyInfo, String leftPadding, Ansi ansi) {
        ansi
                .a(leftPadding)
                .a(INDENTATION)
                .fgBright(Ansi.Color.BLUE)
                .a(propertyInfo.isSingleValue() ? "~" : "")
                .a(propertyInfo.isMandatory() ? "*" : "")
                .a(propertyInfo.getName())
                .reset();

        ansi
                .a(": ")
                .a(AnsiRenderer.render(propertyInfo.getShortDescription()))
                .newline();
    }
}
