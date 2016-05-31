package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCommunicationProfileRepository extends JpaRepository<DeviceCommunicationProfile, Long> {
    List<DeviceCommunicationProfile> getByConfigured(boolean configured);
}
