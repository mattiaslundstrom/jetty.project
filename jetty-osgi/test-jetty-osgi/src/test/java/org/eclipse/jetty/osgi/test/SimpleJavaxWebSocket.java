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

package org.eclipse.jetty.osgi.test;

import java.util.concurrent.CountDownLatch;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import static org.junit.Assert.fail;

@ClientEndpoint(
    subprotocols = {"chat"})
public class SimpleJavaxWebSocket
{
    private Session session;
    public CountDownLatch messageLatch = new CountDownLatch(1);
    public CountDownLatch closeLatch = new CountDownLatch(1);

    @OnError
    public void onError(Throwable t)
    {
        //t.printStackTrace();
        fail(t.getMessage());
    }

    @OnClose
    public void onClose(CloseReason close)
    {
        //System.out.printf("Closed: %d, \"%s\"%n",close.getCloseCode().getCode(),close.getReasonPhrase());
        closeLatch.countDown();
    }

    @OnMessage
    public void onMessage(String message)
    {
        //System.out.printf("Received: \"%s\"%n",message);
        messageLatch.countDown();
    }

    @OnOpen
    public void onOpen(Session session)
    {
        //System.out.printf("Opened%n");
        this.session = session;
    }

    public void writeMessage(String message) throws Exception
    {
        //System.out.printf("Writing: \"%s\"%n",message);
        session.getBasicRemote().sendText(message);
    }
}
