/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.shell.commands;

import org.seedstack.seed.core.spi.command.CommandDefinition;
import org.seedstack.seed.core.spi.command.Command;

@CommandDefinition(scope = "test", name = "exception", description = "Erroneous test command")
public class ErroneousTestCommand implements Command {
    @Override
    public Object execute(Object object) throws Exception {
        throw new RuntimeException("test exception");
    }
}
