package com.estateelite.dto.response;

import com.estateelite.model.Agency;
import com.estateelite.model.AgencyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyResponse {
    private Long id;
    private String name;
    private String licenseNumber;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private AgencyStatus status;
    private List<UserResponse> agents;

    public static AgencyResponse from(Agency a) {
        return AgencyResponse.builder()
                .id(a.getId())
                .name(a.getName())
                .licenseNumber(a.getLicenseNumber())
                .contactEmail(a.getContactEmail())
                .contactPhone(a.getContactPhone())
                .address(a.getAddress())
                .status(a.getStatus())
                .agents(a.getAgents() == null ? List.of() :
                        a.getAgents().stream().map(UserResponse::from).collect(Collectors.toList()))
                .build();
    }
}
