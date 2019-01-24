/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.seed.security.principals;

/**
 * Represents a principal of a user, which is an attribute.
 *
 * @param <T> the type of the object provided by the principal
 */
public interface Principal<T> {
    /**
     * Returns the principal.
     *
     * @return the object enclosed in the principal
     */
    T value();

    /**
     * Returns the principal.
     *
     * @return the object enclosed in the principal
     */
    <U extends T> as(Class<U> );
}
