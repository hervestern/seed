/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal.fixtures;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.seedstack.seed.security.RequiresRest;

public class AnnotatedRestMethods4Security {

  @DELETE
  @RequiresRest("rest")
  public boolean restDelete() {
    return true;
  }

  @GET
  @RequiresRest("rest")
  public boolean restGet() {
    return true;
  }

  @HEAD
  @RequiresRest("rest")
  public boolean restHead() {
    return true;
  }

  @OPTIONS
  @RequiresRest("rest")
  public boolean restOptions() {
    return true;
  }

  @POST
  @RequiresRest("rest")
  public boolean restPost() {
    return true;
  }

  @PUT
  @RequiresRest("rest")
  public boolean restPut() {
    return true;
  }

  // Empty
  @RequiresRest("rest")
  public boolean restEmpty() {
    return true;
  }

}
