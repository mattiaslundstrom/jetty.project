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

package org.eclipse.jetty.websocket.javax.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import jakarta.websocket.Extension;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * The "Container Default Configurator" per the JSR-356 spec.
 *
 * @see ServiceLoader behavior of {@link jakarta.websocket.server.ServerEndpointConfig.Configurator}
 */
public final class ContainerDefaultConfigurator extends Configurator
{
    private static final Logger LOG = Log.getLogger(ContainerDefaultConfigurator.class);
    private static final String NO_SUBPROTOCOL = "";

    /**
     * Default Constructor required, as
     * jakarta.websocket.server.ServerEndpointConfig$Configurator.fetchContainerDefaultConfigurator()
     * will be the one that instantiates this class in most cases.
     */
    public ContainerDefaultConfigurator()
    {
        super();
    }

    @Override
    public boolean checkOrigin(String originHeaderValue)
    {
        return true;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug(".getEndpointInstance({})", endpointClass);
        }

        try
        {
            // Since this is started via a ServiceLoader, this class has no Scope or context
            // that can be used to obtain a ObjectFactory from.
            return endpointClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            InstantiationException instantiationException = new InstantiationException();
            instantiationException.initCause(e);
            throw instantiationException;
        }
    }

    @Override
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested)
    {
        List<Extension> negotiatedExtensions = new ArrayList<>();
        for (Extension ext : requested)
        {
            // Only choose the first extension if multiple with the same name.
            long matches = negotiatedExtensions.stream().filter(e -> e.getName().equals(ext.getName())).count();
            if (matches == 0)
                negotiatedExtensions.add(ext);
        }

        return negotiatedExtensions;
    }

    @Override
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested)
    {
        if ((requested == null) || (requested.size() == 0))
        {
            // nothing requested, don't return anything
            return NO_SUBPROTOCOL;
        }

        // Nothing specifically called out as being supported by the endpoint
        if ((supported == null) || (supported.isEmpty()))
        {
            // Just return the first hit in this case
            LOG.warn("Client requested Subprotocols on endpoint with none supported: {}", String.join(",", requested));
            return NO_SUBPROTOCOL;
        }

        // Return the first matching hit from the list of supported protocols.
        for (String possible : requested)
        {
            if (possible == null)
            {
                // skip null
                continue;
            }

            if (supported.contains(possible))
            {
                return possible;
            }
        }

        LOG.warn("Client requested subprotocols {} do not match any endpoint supported subprotocols {}",
            String.join(",", requested),
            String.join(",", supported));
        return NO_SUBPROTOCOL;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response)
    {
        /* do nothing */
    }
}
