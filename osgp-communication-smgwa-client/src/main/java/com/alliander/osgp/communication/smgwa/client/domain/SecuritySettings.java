package com.alliander.osgp.communication.smgwa.client.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SecuritySettings {
    @Column
    private byte[] encryptionCertificate;
    @Column
    private byte[] signingCertificate;

    @SuppressWarnings("unused")
    private SecuritySettings() {
        // Private constructor used by hibernate
    }

    public SecuritySettings(final byte[] encryptionCertificate, final byte[] signingCertificate) {
        super();
        this.encryptionCertificate = encryptionCertificate;
        this.signingCertificate = signingCertificate;
    }

    public byte[] getEncryptionCertificate() {
        return this.encryptionCertificate.clone();
    }

    public byte[] getSigningCertificate() {
        return this.signingCertificate.clone();
    }

}
