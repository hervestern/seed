/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.spi;

import java.lang.reflect.Method;
import java.util.Optional;

import org.seedstack.seed.security.CRUDAction;

public interface CRUDActionResolver {
  /**
   * Checks if the resolver is able to resolve a {@link CRUDAction} from the specified method
   * object.
   *
   * @param method
   *          the method object.
   * @return true if it is able to resolve a {@link CRUDAction}, false otherwise.
   */
  boolean canResolve(Method method);

  /**
   * Resolves a {@link CRUDAction} from the specified method object.
   *
   * @param method
   *          the method object.
   * @return an optionally resolved {@link CRUDAction}.
   */
  Optional<CRUDAction> resolve(Method method);
}