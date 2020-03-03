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

package org.eclipse.jetty.websocket.javax.common.decoders;

import java.nio.ByteBuffer;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;

public class ByteBufferDecoder extends AbstractDecoder implements Decoder.Binary<ByteBuffer>
{
    public static final ByteBufferDecoder INSTANCE = new ByteBufferDecoder();

    @Override
    public ByteBuffer decode(ByteBuffer bytes) throws DecodeException
    {
        return bytes;
    }

    @Override
    public boolean willDecode(ByteBuffer bytes)
    {
        return true;
    }
}
