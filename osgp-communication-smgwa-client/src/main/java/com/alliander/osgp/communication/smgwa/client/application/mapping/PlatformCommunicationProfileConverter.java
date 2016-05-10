package com.alliander.osgp.communication.smgwa.client.application.mapping;

import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEDestAddresses;
import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEEmtCommProfile;
import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfile;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class PlatformCommunicationProfileConverter
        extends CustomConverter<PlatformCommunicationProfile, TYPEEmtCommProfile> {

    @Override
    public TYPEEmtCommProfile convert(final PlatformCommunicationProfile source,
            final Type<? extends TYPEEmtCommProfile> destinationType) {
        final TYPEEmtCommProfile profile = new TYPEEmtCommProfile();

        profile.setProfileName(source.getProfileName());

        final TYPEDestAddresses addresses = new TYPEDestAddresses();
        addresses.getDestAddress().addAll(source.getDestinationAddresses());
        profile.setDestAddresses(addresses);

        profile.setTlsKeepalive(source.getTlsSettings().isKeepAlive());
        profile.setTlsMaxIdleTime(source.getTlsSettings().getMaxIdleTime());
        profile.setTlsMaxSessionTime(source.getTlsSettings().getMaxSessionTime());
        profile.setCTls(source.getTlsSettings().getCertificate());

        profile.setCEnc(source.getSecuritySettings().getEncryptionCertificate());
        profile.setCSig(source.getSecuritySettings().getSigningCertificate());

        return profile;
    }

}
