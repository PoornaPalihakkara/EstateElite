package com.estateelite.pattern.factory;

import com.estateelite.dto.request.AgencyRequest;
import com.estateelite.model.Agency;
import com.estateelite.model.AgencyStatus;

public final class AgencyFactory {

    private AgencyFactory() {
        // utility / factory – no instances
    }

    public static Agency createFromRequest(AgencyRequest request) {
        return createFromRequest(request, AgencyStatus.ACTIVE);
    }

    public static Agency createFromRequest(AgencyRequest request, AgencyStatus initialStatus) {
        if (request == null) {
            throw new IllegalArgumentException("AgencyRequest must not be null");
        }
        if (initialStatus == null) {
            initialStatus = AgencyStatus.ACTIVE;
        }

        return Agency.builder()
                .name(request.getName())
                .licenseNumber(request.getLicenseNumber())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .status(initialStatus)
                .build();
    }

    public static Agency withStatus(Agency existing, AgencyStatus newStatus) {
        if (existing == null) {
            throw new IllegalArgumentException("Existing agency must not be null");
        }
        existing.setStatus(newStatus);
        return existing;
    }
}
