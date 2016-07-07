package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.util.Date;

import org.apache.commons.lang3.NotImplementedException;
import org.openmuc.openiec61850.FcModelNode;

public final class ReadOnlyNodeContainer extends NodeContainer {

    public ReadOnlyNodeContainer(final String deviceIdentification, final FcModelNode fcmodelNode) {
        super(deviceIdentification, fcmodelNode);
    }

    @Override
    public void write() {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeBoolean(final SubDataAttribute child, final boolean value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeByte(final SubDataAttribute child, final byte value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeDate(final SubDataAttribute child, final Date value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeFloat(final SubDataAttribute child, final Float value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeInteger(final SubDataAttribute child, final Integer value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeShort(final SubDataAttribute child, final Short value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeString(final SubDataAttribute child, final String value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public void writeUnsignedShort(final SubDataAttribute child, final Integer value) {
        throw new NotImplementedException("Writing is not supported");
    }

    @Override
    public NodeContainer getChild(final SubDataAttribute child) {
        return new ReadOnlyNodeContainer(this.deviceIdentification,
                (FcModelNode) this.parent.getChild(child.getDescription()));
    }

    @Override
    public NodeContainer getChild(final String child) {
        return new ReadOnlyNodeContainer(this.deviceIdentification, (FcModelNode) this.parent.getChild(child));
    }
}
