package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.nio.ByteBuffer;

public class ByteArrayTranslationService {

    public static short toShort(final byte[] value) {
        return ByteBuffer.wrap(value).getShort();
    }

    public static byte[] fromShort(final short value) {
        return ByteBuffer.allocate(2).putShort(value).array();
    }

}
