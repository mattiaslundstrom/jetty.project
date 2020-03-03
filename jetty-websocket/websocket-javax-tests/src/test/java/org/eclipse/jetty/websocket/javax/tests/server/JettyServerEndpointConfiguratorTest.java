//
// ========================================================================
// Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under
// the terms of the Eclipse Public License 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0
//
// This Source Code may also be made available under the following
// Secondary Licenses when the conditions for such availability set
// forth in the Eclipse Public License, v. 2.0 are satisfied:
// the Apache License v2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.websocket.javax.tests.server;

import java.util.Iterator;
import java.util.ServiceLoader;

import jakarta.websocket.server.ServerEndpointConfig;
import org.eclipse.jetty.websocket.javax.server.config.ContainerDefaultConfigurator;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test the JettyServerEndpointConfigurator impl.
 */
public class JettyServerEndpointConfiguratorTest
{
    @Test
    public void testServiceLoader()
    {
        ServiceLoader<ServerEndpointConfig.Configurator> loader = ServiceLoader.load(jakarta.websocket.server.ServerEndpointConfig.Configurator.class);
        assertThat("loader", loader, notNullValue());
        Iterator<ServerEndpointConfig.Configurator> iter = loader.iterator();
        assertThat("loader.iterator", iter, notNullValue());
        assertThat("loader.iterator.hasNext", iter.hasNext(), is(true));

        ServerEndpointConfig.Configurator configr = iter.next();
        assertThat("Configurator", configr, notNullValue());
        assertThat("Configurator type", configr, instanceOf(ContainerDefaultConfigurator.class));
    }
}
