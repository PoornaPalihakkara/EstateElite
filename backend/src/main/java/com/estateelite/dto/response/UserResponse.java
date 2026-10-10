package com.estateelite.dto.response;

import com.estateelite.model.Role;
import com.estateelite.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private Role role;
    private Long agencyId;
    private String agencyName;
    private String branch;

    public static UserResponse from(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .profileImage(u.getProfileImage())
                .role(u.getRole())
                .agencyId(u.getAgency() != null ? u.getAgency().getId() : null)
                .agencyName(u.getAgency() != null ? u.getAgency().getName() : null)
                .branch(u.getBranch())
                .build();
    }
}
