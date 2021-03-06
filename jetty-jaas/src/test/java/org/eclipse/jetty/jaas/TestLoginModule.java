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

package org.eclipse.jetty.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;

import org.eclipse.jetty.jaas.callback.ServletRequestCallback;
import org.eclipse.jetty.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.jaas.spi.UserInfo;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.security.Password;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestLoginModule extends AbstractLoginModule
{
    public ServletRequestCallback _callback = new ServletRequestCallback();

    @Override
    public UserInfo getUserInfo(String username) throws Exception
    {
        return new UserInfo(username, new Password("aaa"));
    }

    @Override
    public Callback[] configureCallbacks()
    {
        return ArrayUtil.addToArray(super.configureCallbacks(), _callback, Callback.class);
    }

    @Override
    public boolean login() throws LoginException
    {
        boolean result = super.login();
        assertNotNull(_callback.getRequest());
        return result;
    }
}
