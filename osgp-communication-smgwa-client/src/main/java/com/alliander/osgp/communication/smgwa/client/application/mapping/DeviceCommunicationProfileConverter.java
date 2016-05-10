package com.alliander.osgp.communication.smgwa.client.application.mapping;

import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEClsCommProfile;
import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEDestAddresses;
import com.alliander.osgp.communication.smgwa.client.domain.DeviceCommunicationProfile;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class DeviceCommunicationProfileConverter
        extends CustomConverter<DeviceCommunicationProfile, TYPEClsCommProfile> {

    @Override
    public TYPEClsCommProfile convert(final DeviceCommunicationProfile source,
            final Type<? extends TYPEClsCommProfile> destinationType) {
        final TYPEClsCommProfile profile = new TYPEClsCommProfile();

        profile.setProfileName(source.getProfileName());

        final TYPEDestAddresses addresses = new TYPEDestAddresses();
        addresses.getDestAddress().addAll(source.getDestinationAddresses());
        profile.setDestAddresses(addresses);

        profile.setTlsKeepalive(source.getTlsSettings().isKeepAlive());
        profile.setTlsMaxIdleTime(source.getTlsSettings().getMaxIdleTime());
        profile.setTlsMaxSessionTime(source.getTlsSettings().getMaxSessionTime());
        profile.setCTls(source.getTlsSettings().getCertificate());

        return profile;
    }

}
