package com.estateelite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "agencies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private String contactEmail;

    private String contactPhone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgencyStatus status;

    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY)
    private List<User> agents;
}
