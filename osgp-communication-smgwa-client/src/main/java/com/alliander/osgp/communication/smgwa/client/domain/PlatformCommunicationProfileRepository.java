package com.alliander.osgp.communication.smgwa.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformCommunicationProfileRepository extends JpaRepository<PlatformCommunicationProfile, Long> {
    PlatformCommunicationProfile getByProfileName(String profileName);
}
