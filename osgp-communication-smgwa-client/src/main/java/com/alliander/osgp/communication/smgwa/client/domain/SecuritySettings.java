package com.alliander.osgp.communication.smgwa.client.domain;

public class SecuritySettings {
    private byte[] encryptionCertificate;
    private byte[] signingCertificate;

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
