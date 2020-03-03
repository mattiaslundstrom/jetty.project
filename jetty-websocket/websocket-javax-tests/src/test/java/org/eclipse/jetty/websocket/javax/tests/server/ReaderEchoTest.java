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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.javax.tests.Fuzzer;
import org.eclipse.jetty.websocket.javax.tests.LocalServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test various {@link jakarta.websocket.Decoder.TextStream Decoder.TextStream} and {@link jakarta.websocket.Encoder.TextStream Encoder.TextStream} echo behavior of Java Readers
 */
public class ReaderEchoTest
{
    private static final Logger LOG = Log.getLogger(ReaderEchoTest.class);

    public static class BaseSocket
    {
        @OnError
        public void onError(Throwable cause) throws IOException
        {
            LOG.warn("Error", cause);
        }
    }

    @SuppressWarnings("unused")
    @ServerEndpoint("/echo/reader")
    public static class ReaderSocket extends BaseSocket
    {
        @OnMessage
        public String onReader(Reader reader) throws IOException
        {
            return IO.toString(reader);
        }
    }

    @SuppressWarnings("unused")
    @ServerEndpoint("/echo/reader-param/{param}")
    public static class ReaderParamSocket extends BaseSocket
    {
        @OnMessage
        public String onReader(Reader reader, @PathParam("param") String param) throws IOException
        {
            StringBuilder msg = new StringBuilder();
            msg.append(IO.toString(reader));
            msg.append('|');
            msg.append(param);
            return msg.toString();
        }
    }

    private static LocalServer server;

    @BeforeAll
    public static void startServer() throws Exception
    {
        server = new LocalServer();
        server.start();
        server.getServerContainer().addEndpoint(ReaderSocket.class);
        server.getServerContainer().addEndpoint(ReaderParamSocket.class);
    }

    @AfterAll
    public static void stopServer() throws Exception
    {
        server.stop();
    }

    @Test
    public void testReaderSocket() throws Exception
    {
        String requestPath = "/echo/reader";

        List<Frame> send = new ArrayList<>();
        send.add(new Frame(OpCode.TEXT).setPayload("Hello World"));
        send.add(CloseStatus.toFrame(CloseStatus.NORMAL));

        List<Frame> expect = new ArrayList<>();
        expect.add(new Frame(OpCode.TEXT).setPayload("Hello World"));
        expect.add(CloseStatus.toFrame(CloseStatus.NORMAL));

        try (Fuzzer session = server.newNetworkFuzzer(requestPath))
        {
            session.sendBulk(send);
            session.expect(expect);
        }
    }

    @Test
    public void testReaderParamSocket() throws Exception
    {
        String requestPath = "/echo/reader-param/Every%20Person";

        List<Frame> send = new ArrayList<>();
        send.add(new Frame(OpCode.TEXT).setPayload("Hello World"));
        send.add(CloseStatus.toFrame(CloseStatus.NORMAL));

        List<Frame> expect = new ArrayList<>();
        expect.add(new Frame(OpCode.TEXT).setPayload("Hello World|Every Person"));
        expect.add(CloseStatus.toFrame(CloseStatus.NORMAL));

        try (Fuzzer session = server.newNetworkFuzzer(requestPath))
        {
            session.sendBulk(send);
            session.expect(expect);
        }
    }
}
